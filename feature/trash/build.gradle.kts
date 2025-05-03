plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	id ("com.google.dagger.hilt.android")
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.example.facenote.feature.trash"
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

	implementation(libs.androidx.paging.runtime.ktx)
	implementation(libs.androidx.paging.compose)

	implementation(libs.coil.compose)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}