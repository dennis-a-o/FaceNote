plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("com.google.devtools.ksp")
	id ("com.google.dagger.hilt.android")
}

android {
	namespace = "com.example.facenote.core.datastore"
	compileSdk = 34

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
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

dependencies {
	implementation(project(":core:model"))
	implementation(libs.androidx.datastore.preferences.core)
	implementation (libs.hilt.android)
	ksp (libs.hilt.compiler)

	testImplementation (libs.hilt.android.testing)
	kspTest (libs.hilt.compiler)

	testImplementation(libs.junit)
	testImplementation(libs.kotlinx.coroutines.test)
	///androidTestImplementation(libs.androidx.junit)
}