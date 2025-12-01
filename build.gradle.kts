// Top-level build file where you can add configuration options common to all sub-projects/modules.
val kotlinVersion = "1.9.23"
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
}