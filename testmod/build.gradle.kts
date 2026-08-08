plugins {
    id("earth.terrarium.cloche") version "0.16.9"
}

version = "1.0.0"
group = "com.moulberry.lattice"

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        main()
        mavenFabric()
    }
}

dependencies {
    compileOnly(project(":"))
}

cloche {
    metadata {
        modId = "lattice_testmod"
        name = "Lattice Testmod"
        license = "MIT"
    }

    mappings {
        official()
    }

    fun createFabric(version: String, apiVersion: String) {
        fabric("fabric:${version}") {
            minecraftVersion = version
            loaderVersion = "0.17.3"

            includedClient()

            runs {
                client()
            }

            dependencies {
                fabricApi(apiVersion, version.split("-")[0])
                implementation(rootProject.tasks.named<Jar>("buildMergedFabric").get().outputs.files)
                include(rootProject.tasks.named<Jar>("buildMergedFabric").get().outputs.files)
            }

            metadata {
                entrypoint("main") {
                    value = "com.moulberry.lattice.testmod.LatticeTestMod"
                }
            }
        }
    }

    createFabric("1.20.1", "0.92.6")
    createFabric("1.20.2", "0.91.6")
    createFabric("1.20.4", "0.91.3")
    createFabric("1.20.6", "0.100.8")
    createFabric("1.21.1", "0.116.4")
    createFabric("1.21.3", "0.106.1")
    createFabric("1.21.4", "0.119.3")
    createFabric("1.21.5", "0.119.3")
    createFabric("1.21.6", "0.128.1")
    createFabric("1.21.9", "0.133.14")
    createFabric("1.21.11-rc2", "0.139.4")
}

tasks.register("remapFabricClients") {
    dependsOn(
        "remapFabric1201ClientMinecraftIntermediary",
        "remapFabric1202ClientMinecraftIntermediary",
        "remapFabric1204ClientMinecraftIntermediary",
        "remapFabric1206ClientMinecraftIntermediary",
        "remapFabric1211ClientMinecraftIntermediary",
        "remapFabric1213ClientMinecraftIntermediary",
        "remapFabric1214ClientMinecraftIntermediary",
        "remapFabric1215ClientMinecraftIntermediary",
        "remapFabric1216ClientMinecraftIntermediary",
        "remapFabric1219ClientMinecraftIntermediary",
        "remapFabric12111Rc2ClientMinecraftIntermediary",
        "generateFabric1201MappingsArtifact",
        "generateFabric1202MappingsArtifact",
        "generateFabric1204MappingsArtifact",
        "generateFabric1206MappingsArtifact",
        "generateFabric1211MappingsArtifact",
        "generateFabric1213MappingsArtifact",
        "generateFabric1214MappingsArtifact",
        "generateFabric1215MappingsArtifact",
        "generateFabric1216MappingsArtifact",
        "generateFabric1219MappingsArtifact",
        "generateFabric12111Rc2MappingsArtifact",
    )
}
