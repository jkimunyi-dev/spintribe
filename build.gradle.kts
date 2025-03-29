plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

ext {
    set("kotlin_version", "2.1.0") // Update Kotlin version to match Firebase Auth
}
