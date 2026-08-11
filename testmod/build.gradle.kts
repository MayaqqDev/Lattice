plugins {
    id("earth.terrarium.cloche") version "0.19.13"
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
