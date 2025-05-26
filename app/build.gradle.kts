plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.example.facenote"
	compileSdk = 35

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

	defaultConfig {
		applicationId = "com.example.facenote"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
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
	buildToolsVersion = "35.0.0"
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	hilt {
		enableAggregatingTask = true
	}
}

dependencies {
	implementation(project(":core:ui"))
	implementation(project(":core:navigation"))
	implementation(project(":core:data"))
	implementation(project(":core:model"))
	implementation(project(":core:notifications"))

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	api(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)

	implementation(libs.androidx.core.splashscreen)

	implementation(libs.coil.compose)
	implementation (libs.androidx.work.runtime)
	implementation(libs.threetenabp)


	implementation(libs.google.gson)

	implementation(libs.hilt.android)
	implementation(libs.androidx.hilt.work)

	ksp(libs.hilt.compiler)
	implementation(libs.androidx.hilt.navigation.compose)

	implementation(libs.androidx.navigation.compose)

	implementation(libs.play.services.auth)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}