plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.example.facenote.feature.backup"
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
		minSdk = 24
		testInstrumentationRunner ="androidx.test.runner.AndroidJUnitRunner"
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

	testOptions {
		unitTests{
			isIncludeAndroidResources = true
		}
	}
}

dependencies {
	implementation(project(":core:ui"))
	implementation(project(":core:model"))
	implementation(project(":core:domain"))
	implementation(project(":core:data"))
	implementation(project(":core:worker"))

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.com.google.android.material)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.hilt.android)
	implementation(libs.threetenabp)
	implementation(libs.androidx.work.runtime.ktx)

	ksp(libs.hilt.compiler)
	implementation(libs.androidx.hilt.navigation.compose)

	implementation(libs.androidx.lifecycle.viewmodel.compose)

	implementation (libs.google.api.client.android)
	implementation (libs.google.api.services.drive)

	implementation(libs.googleid)
	implementation(libs.androidx.credentials)
	implementation(libs.androidx.credentials.play.services.auth)
	implementation(libs.play.services.auth)


	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.test.core)
	androidTestImplementation(libs.androidx.test.rules)
	androidTestImplementation(libs.androidx.test.runner)

	androidTestImplementation(libs.androidx.espresso.core)

	androidTestImplementation(libs.hilt.android.testing)

	androidTestImplementation(libs.androidx.compose.ui.test)
	testImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)

	testImplementation(libs.mockito.core)
	testImplementation(libs.mockito.kotlin)
	testImplementation(libs.mockk)

	testImplementation(libs.robolectric)

	testImplementation(libs.kotlinx.coroutines.test)
}