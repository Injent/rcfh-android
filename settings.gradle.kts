enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GLPM"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core:database")
include(":core:designsystem")
include(":core:data")
include(":core:datastore")
include(":core:network")
include(":core:common")
include(":core:navigation")
include(":core:model")
include(":core:sync")
include(":feature:login")
include(":core:sdui")
include(":feature:documents")
include(":feature:home")
include(":core:account")
include(":feature:settings")
include(":feature:form")
