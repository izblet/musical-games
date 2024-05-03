pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { // repo for TFLite snapshot
            name = "ossrh-snapshot"
            url = uri("http://oss.sonatype.org/content/repositories/snapshots")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "MusicalGames"
include(":app")
