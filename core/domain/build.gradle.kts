plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.example.facenote.core.domain"
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
}

dependencies {
	implementation(project(":core:data"))
	implementation(project(":core:model"))
	implementation(project(":core:storage"))

	implementation(libs.androidx.core.ktx)
	implementation (libs.hilt.android)
	implementation (libs.androidx.paging.runtime.ktx)
	implementation (libs.androidx.paging.compose)

	ksp (libs.hilt.compiler)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
}