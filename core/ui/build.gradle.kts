plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
}

android {
	namespace = "com.example.facenote.core.ui"
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
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}
}

dependencies {
	implementation(project(":core:model"))
	implementation(libs.material3)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.ui)
	implementation(libs.ui.tooling.preview)
	debugImplementation(libs.ui.tooling)
	debugImplementation(libs.ui.test.manifest)
	implementation(libs.google.gson)
	implementation(libs.threetenabp)

	//testImplementation(libs.junit)
	//androidTestImplementation(libs.androidx.junit)
	//androidTestImplementation(libs.androidx.espresso.core)
}