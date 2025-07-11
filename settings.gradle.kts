pluginManagement {
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

rootProject.name = "FaceNote"
include(":app")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":core:navigation")
include(":feature:notes")
include(":feature:settings")
include(":core:database")
include(":core:datastore")
include(":core:model")
include(":core:storage")
include(":feature:note-editor")
include(":feature:note-gallery")
include(":feature:note-search")
include(":feature:archive")
include(":feature:trash")
include(":core:notifications")
include(":core:worker")
include(":feature:reminder")
include(":feature:backup")
include(":core:drive")
include(":core:testing")
include(":ui-test-hilt-manifest")
