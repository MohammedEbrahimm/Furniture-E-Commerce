// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {// Glide
        google()
        mavenCentral()

        maven { url = uri("https://jitpack.io") } // Add this line for JitPack repository


        dependencies {
            // Pass data between destinations
            val nav_version = "2.7.7"
            classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}


plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.dagger.hilt.android") version "2.46" apply false// dagger with hilt
}
