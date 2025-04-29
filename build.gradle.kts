
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.dev.tool) apply false

}

buildscript {
    // ... other configurations ...
    dependencies {
        classpath(libs.secrets.gradle.plugin) // Use the latest version
    }
}
