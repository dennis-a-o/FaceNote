plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.ksp)
	alias(libs.plugins.dagger.hilt.android)
}

android {
	namespace = "com.example.facenote.feature.note_gallery"
	compileSdk = 35

	defaultConfig {
		minSdk = 24

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


	dependencies {
		implementation(project(":core:ui"))
		implementation(project(":core:model"))
		implementation(project(":core:domain"))
		implementation(project(":core:data"))

		implementation(platform(libs.androidx.compose.bom))
		implementation(libs.androidx.material3)
		implementation(libs.androidx.ui.tooling.preview)
		implementation(libs.androidx.navigation.compose)
		implementation(libs.hilt.android)

		ksp(libs.hilt.compiler)
		implementation(libs.androidx.hilt.navigation.compose)
		implementation(libs.coil.compose)

		testImplementation(libs.junit)
		androidTestImplementation(libs.androidx.junit)
		androidTestImplementation(libs.androidx.espresso.core)
	}
}