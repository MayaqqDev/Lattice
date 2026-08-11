import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import earth.terrarium.cloche.api.target.CommonTarget
import com.vanniktech.maven.publish.SonatypeHost
import earth.terrarium.cloche.api.attributes.TargetAttributes
import earth.terrarium.cloche.api.attributes.MinecraftModLoader

plugins {
    id("earth.terrarium.cloche") version "0.19.13"
    id("com.vanniktech.maven.publish") version("0.28.0") // `maven-publish` doesn't support new maven central
}

version = project.version
group = project.group

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        main()
        mavenFabric()
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenForge()
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

var fabricJarOutputs = mutableListOf<Provider<out Jar>>()
var forgeJarOutputs = mutableListOf<Provider<out Jar>>()
var neoforgeJarOutputs = mutableListOf<Provider<out Jar>>()

val javaComponent = components.findByName("java") as AdhocComponentWithVariants

cloche {
    metadata {
        modId = "lattice"
        name = "Lattice"
        description = "Library for creating configuration GUIs"
        license = "MIT"
        icon = "assets/lattice/icon.png"
        author("Moulberry")

        custom("modmenu" to mapOf("badges" to listOf("library")))
    }

    mappings {
        official()
    }

    common {
        dependencies {
            compileOnly("org.jetbrains:annotations:23.0.0")
            compileOnly("org.spongepowered:mixin:0.8.3")
        }
    }

    val fabriccommon = common("fabriccommon")
    val forgecommon = common("forgecommon")
    val neoforgecommon = common("neoforgecommon")

    val commonMinecraftVersion: Attribute<String> = Attribute.of("com.moulberry.commonMinecraftVersion", String::class.java)

    fun createCommon(version: String): CommonTarget {
        val rawVersion = version.split("-")[0]
        return common(rawVersion) {
            attributes {
                attribute(commonMinecraftVersion, rawVersion)
            }
        }
    }

    fun createFabric(commonTarget: CommonTarget, version: String, fabricLoader: String) {
        val rawVersion = version.split("-")[0]
        fabric("fabric:${rawVersion}") {
            minecraftVersion = version
            loaderVersion = fabricLoader

            mixins.from("src/1.20.1/main/mixins/lattice1201.mixins.json")
            mixins.from("src/1.20.2/main/mixins/lattice1202.mixins.json")
            mixins.from("src/1.20.4/main/mixins/lattice1204.mixins.json")
            mixins.from("src/1.20.6/main/mixins/lattice1206.mixins.json")
            mixins.from("src/1.21.6/main/mixins/lattice1216.mixins.json")
            mixins.from("src/1.21.9/main/mixins/lattice1219.mixins.json")
            mixins.from("src/1.21.11/main/mixins/lattice12111.mixins.json")

            dependsOn(commonTarget)
            dependsOn(fabriccommon)

            includedClient()

            fabricJarOutputs.add(finalJar)
        }
    }

    fun createForge(commonTarget: CommonTarget, version: String, forgeVersion: String) {
        forge("forge:${version}") {
            minecraftVersion = version
            loaderVersion = forgeVersion

            mixins.from("src/1.20.1/main/mixins/lattice1201.mixins.json")
            mixins.from("src/1.20.2/main/mixins/lattice1202.mixins.json")
            mixins.from("src/1.20.4/main/mixins/lattice1204.mixins.json")

            dependsOn(commonTarget)
            dependsOn(forgecommon)

            forgeJarOutputs.add(finalJar)
        }
    }

    fun createNeoforge(commonTarget: CommonTarget, version: String, neoforgeVersion: String) {
        neoforge("neoforge:${version}") {
            minecraftVersion = version
            loaderVersion = neoforgeVersion

            mixins.from("src/1.20.6/main/mixins/lattice1206.mixins.json")
            mixins.from("src/1.21.6/main/mixins/lattice1216.mixins.json")
            mixins.from("src/1.21.9/main/mixins/lattice1219.mixins.json")
            mixins.from("src/1.21.11/main/mixins/lattice12111.mixins.json")

            dependsOn(commonTarget)
            dependsOn(neoforgecommon)

            neoforgeJarOutputs.add(finalJar)
        }
    }

    fun createAll(version: String, fabricLoader: String, forgeVersion: String?, neoforgeVersion: String?) {
        val common = createCommon(version)
        createFabric(common, version, fabricLoader)
        if (forgeVersion != null) {
            createForge(common, version, forgeVersion)
        }
        if (neoforgeVersion != null) {
            createNeoforge(common, version, neoforgeVersion)
        }
    }

    createAll("1.20.1", "0.18.5", "47.4.22", null)
    createAll("1.20.2", "0.18.5", "48.1.0", null)
    createAll("1.20.4", "0.18.5", "49.2.8", null)
    createAll("1.20.6", "0.18.5", null, "20.6.139")
    createAll("1.21.1", "0.18.5", null, "21.1.248")
    createAll("1.21.3", "0.18.5", null, "21.3.97")
    createAll("1.21.4", "0.18.5", null, "21.4.157")
    createAll("1.21.5", "0.18.5", null, "21.5.98")
    createAll("1.21.6", "0.18.5", null, "21.6.20-beta")
    createAll("1.21.9", "0.18.5", null, "21.9.16-beta")
    createAll("1.21.11", "0.18.5", null, "21.11.45")

    targets.all {
        javaComponent.withVariantsFromConfiguration(configurations.named<ConsumableConfiguration>(sourceSet.runtimeElementsConfigurationName)) {
            skip()
        }

        javaComponent.withVariantsFromConfiguration(configurations.named<ConsumableConfiguration>(sourceSet.apiElementsConfigurationName)) {
            skip()
        }
    }
}

tasks.withType<Jar>().named { it != "buildMergedFabric" && it != "buildMergedForge" && it != "buildMergedNeoforge" }.configureEach {
    outputs.cacheIf { true }
}

tasks.register<Jar>("buildMergedFabric") {
    archiveBaseName.set("lattice-fabric")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    fabricJarOutputs.forEach { jarProvider ->
        from(project.zipTree(jarProvider.get().archiveFile))
    }

    archiveClassifier = "fabric"

    manifest {
        attributes["Fabric-Loom-Mixin-Remap-Type"] = "static"
        attributes["Fabric-Jar-Type"] = "classes"
        attributes["Fabric-Mapping-Namespace"] = "intermediary"
    }
}

tasks.register<Jar>("buildMergedForge") {
    archiveBaseName.set("lattice-forge")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    forgeJarOutputs.forEach { jarProvider ->
        from(project.zipTree(jarProvider.get().archiveFile))
    }

    archiveClassifier = "forge"
}

tasks.register<Jar>("buildMergedNeoforge") {
    archiveBaseName.set("lattice-neoforge")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    neoforgeJarOutputs.forEach { jarProvider ->
        from(project.zipTree(jarProvider.get().archiveFile))
    }

    archiveClassifier = "neoforge"
}

configurations.all {
    resolutionStrategy.capabilitiesResolution {
        withCapability("cpw.mods:modlauncher") {
            selectHighestVersion()
        }
    }
}

fun baseFabricConfiguration(configuration: Configuration) {
    configuration.outgoing.capability("com.moulberry:lattice:${rootProject.version}")
    configuration.outgoing.artifact(tasks.named<Jar>("buildMergedFabric").get())

    configuration.attributes.attribute(TargetAttributes.MOD_LOADER, MinecraftModLoader.fabric)
    configuration.attributes.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
    configuration.attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
    configuration.attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    configuration.attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
}

fun baseForgeConfiguration(configuration: Configuration) {
    configuration.outgoing.capability("com.moulberry:lattice:${rootProject.version}")
    configuration.outgoing.artifact(tasks.named<Jar>("buildMergedForge").get())

    configuration.attributes.attribute(TargetAttributes.MOD_LOADER, MinecraftModLoader.forge)
    configuration.attributes.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
    configuration.attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
    configuration.attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    configuration.attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
}

fun baseNeoforgeConfiguration(configuration: Configuration) {
    configuration.outgoing.capability("com.moulberry:lattice:${rootProject.version}")
    configuration.outgoing.artifact(tasks.named<Jar>("buildMergedNeoforge").get())

    configuration.attributes.attribute(TargetAttributes.MOD_LOADER, MinecraftModLoader.neoforge)
    configuration.attributes.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
    configuration.attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
    configuration.attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    configuration.attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
}

var fabricConfigurationApi = configurations.create("fabricApi") {
    baseFabricConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
}

var fabricConfigurationRuntime = configurations.create("fabricRuntime") {
    baseFabricConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
}

var forgeConfigurationApi = configurations.create("forgeApi") {
    baseForgeConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
}

var forgeConfigurationRuntime = configurations.create("forgeRuntime") {
    baseForgeConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
}

var neoforgeConfigurationApi = configurations.create("neoforgeApi") {
    baseNeoforgeConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
}

var neoforgeConfigurationRuntime = configurations.create("neoforgeRuntime") {
    baseNeoforgeConfiguration(this)
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
}

javaComponent.addVariantsFromConfiguration(fabricConfigurationApi) {
    mapToMavenScope("compile")
    mapToOptional()
}
javaComponent.addVariantsFromConfiguration(fabricConfigurationRuntime) {
    mapToMavenScope("runtime")
    mapToOptional()
}

javaComponent.addVariantsFromConfiguration(forgeConfigurationApi) {
    mapToMavenScope("compile")
    mapToOptional()
}
javaComponent.addVariantsFromConfiguration(forgeConfigurationRuntime) {
    mapToMavenScope("runtime")
    mapToOptional()
}

javaComponent.addVariantsFromConfiguration(neoforgeConfigurationApi) {
    mapToMavenScope("compile")
    mapToOptional()
}
javaComponent.addVariantsFromConfiguration(neoforgeConfigurationRuntime) {
    mapToMavenScope("runtime")
    mapToOptional()
}

mavenPublishing {
    configure(JavaLibrary(JavadocJar.Javadoc(), true))

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("com.moulberry", "lattice", version.toString())

    pom {
        name = "Lattice"
        description = "Library for creating Minecraft configuration GUIs"
        url = "https://github.com/Moulberry/Lattice"
        inceptionYear = "2025"
        packaging = "jar"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit"
            }
        }

        developers {
            developer {
                name = "Moulberry"
                url = "https://github.com/Moulberry"
            }
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/Moulberry/Lattice/issues"
        }

        scm {
            url = "https://github.com/Moulberry/Lattice/"
            connection = "scm:git:git://github.com/Moulberry/Lattice.git"
            developerConnection = "scm:git:ssh://git@github.com/Moulberry/Lattice.git"
        }
    }
}
