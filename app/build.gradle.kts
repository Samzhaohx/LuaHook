
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {

    namespace = "com.kulipai.luahook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kulipai.luahook"
        minSdk = 28
        targetSdk = 35
        versionCode = 7
        versionName = "2.6-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // 启用代码压缩
            isShrinkResources = true // 启用资源压缩
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

//        isCoreLibraryDesugaringEnabled = true

    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.okhttp)
    compileOnly(fileTree("compileOnly"))
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    // The core module that provides APIs to a shell
    implementation(libs.core)

    implementation(libs.xphelper)
    implementation(libs.dexkit)

}