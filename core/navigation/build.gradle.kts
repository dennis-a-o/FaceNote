plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
}

android {
	namespace = "com.example.facenote.core.navigation"
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
	implementation(project(":feature:notes"))
	implementation(project(":feature:note-editor"))
	implementation(project(":feature:note-gallery"))
	implementation(project(":feature:note-search"))
	implementation(project(":feature:archive"))
	implementation(project(":feature:trash"))
	implementation(project(":feature:settings"))
	implementation(project(":feature:reminder"))
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.navigation.compose)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}