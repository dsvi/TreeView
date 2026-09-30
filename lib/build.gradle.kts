import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

group = "anarchy.ds.compose"
version = "0.0.6"

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.common)
    implementation(compose.material3)
}


mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "io.gitlab.kompose",
        artifactId = "tree-view",
        version = version.toString()
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("TreeView control for kotlin compose")
        description.set("This implement a dynamic (lazy) tree view for kotlin compose UI framework")
        inceptionYear.set("2024")
        url.set("https://github.com/dsvi/treeview")

        licenses {
            license {
                name.set("Public")
                url.set("https://opensource.org/license/unlicense")
            }
        }

        // Specify developer information
        developers {
            developer {
                id.set("44100Hz")
                //name.set("John Doe")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/dsvi/treeview")
        }
    }
    // Enable GPG signing for all publications
    signAllPublications()

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}