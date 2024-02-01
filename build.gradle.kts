
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
<<<<<<< Updated upstream
    id("com.android.application") version "8.1.4" apply false
=======
    id("com.android.application") version "8.2.1" apply false
>>>>>>> Stashed changes

    // Make sure that you have the Google services Gradle plugin dependency
    id("com.google.gms.google-services") version "4.4.0" apply false

    // Add the dependency for the Performance Monitoring Gradle plugin
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}