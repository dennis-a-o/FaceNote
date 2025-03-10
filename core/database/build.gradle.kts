plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("com.google.devtools.ksp")
	id ("com.google.dagger.hilt.android")
}

android {
	namespace = "com.example.facenote.core.database"
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
}

dependencies {
	implementation(libs.androidx.room.runtime)
	ksp(libs.androidx.room.compiler)
	implementation(libs.androidx.room.ktx)
	implementation(libs.androidx.room.paging)
	testImplementation(libs.androidx.room.testing)
	testImplementation(libs.junit)

	implementation (libs.hilt.android)
	ksp (libs.hilt.compiler)

	testImplementation (libs.hilt.android.testing)
	kspTest (libs.hilt.compiler)

	androidTestImplementation (libs.androidx.core)
	androidTestImplementation (libs.core.ktx)
	androidTestImplementation (libs.androidx.runner)

	testImplementation(libs.junit)
	testImplementation(libs.kotlinx.coroutines.test)

	//androidTestImplementation(libs.androidx.junit)
	//androidTestImplementation(libs.androidx.espresso.core)
}