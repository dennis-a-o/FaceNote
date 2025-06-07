plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.ksp)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.roborazzi)
}

android {
	namespace = "com.example.facenote.feature.settings"
	compileSdk = 35

	defaultConfig {
		minSdk = 24
		testInstrumentationRunner = "com.example.facenote.core.testing.FaceNoteTestRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		compose = true
	}

	packaging {
		resources {
			excludes.addAll(listOf(
				"META-INF/DEPENDENCIES",
				"META-INF/LICENSE",
				"META-INF/LICENSE.txt",
				"META-INF/license.txt",
				"META-INF/NOTICE",
				"META-INF/NOTICE.txt",
				"META-INF/notice.txt",
				"META-INF/*.kotlin_module"
			))
		}
	}

	testOptions {
		unitTests{
			isIncludeAndroidResources = true
			isReturnDefaultValues = true
			all {
				it.systemProperties["roboletric.pixelCopyRenderMode"] = "hardware"
			}
		}
	}
}

dependencies {
	implementation(project(":core:ui"))
	implementation(project(":core:model"))
	implementation(project(":core:domain"))
	implementation(project(":core:data"))
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.hilt.android)

	ksp(libs.hilt.compiler)
	implementation(libs.androidx.hilt.navigation.compose)

	implementation(libs.androidx.paging.runtime.ktx)
	implementation(libs.androidx.paging.compose)

	implementation(libs.coil.compose)

	implementation(project(":core:testing"))
	implementation(project(":ui-test-hilt-manifest"))

	testImplementation(libs.junit)
	testImplementation(libs.mockk)
	testImplementation(libs.kotlinx.coroutines.test)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.hilt.android.testing)
	testImplementation(libs.androidx.compose.ui.test.junit4)
	testImplementation(libs.androidx.compose.ui.test.manifest)
	testImplementation(libs.hilt.android.testing)
	testImplementation(libs.robolectric)
	testImplementation(libs.roborazzi)
	testImplementation(libs.roborazzi.compose)
	testImplementation(libs.roborazzi.junit.rule)
}