plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("com.google.devtools.ksp")
	id ("com.google.dagger.hilt.android")
}

android {
	namespace = "com.example.facenote.core.data"
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

	implementation(libs.androidx.core.ktx)
	implementation(project(":core:model"))
	implementation(project(":core:database"))
	implementation(project(":core:datastore"))
	implementation(project(":core:storage"))
	implementation (libs.hilt.android)
	ksp (libs.hilt.compiler)
	implementation(libs.androidx.room.runtime)
	ksp(libs.androidx.room.compiler)
	implementation(libs.androidx.room.paging)
	testImplementation(libs.androidx.room.testing)
	implementation (libs.androidx.paging.runtime.ktx)
	implementation (libs.androidx.paging.compose)

	testImplementation (libs.junit)
	testImplementation (libs.mockito.core)
	testImplementation (libs.mockito.inline)
	testImplementation (libs.androidx.core.testing)

	testImplementation(libs.junit)
	testImplementation(libs.kotlinx.coroutines.test)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}