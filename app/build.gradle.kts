plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.homework_7_panchenko_es"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.homework_7_panchenko_es"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // по желанию, но удобно
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Базовые AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material3 (даёт Theme.Material3.DayNight.NoActionBar)
    implementation("com.google.android.material:material:1.12.0")

    // Выровнять Kotlin-версии и убрать старые jdk7/jdk8 артефакты
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    // (ниже — жёсткий вырез старья, чтобы не было дублей)
    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
