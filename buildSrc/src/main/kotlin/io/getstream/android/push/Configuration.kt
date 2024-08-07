package io.getstream.android.push

object Configuration {
    const val compileSdk = 34
    const val targetSdk = 32
    const val sampleTargetSdk = 33
    const val minSdk = 21
    const val majorVersion = 1
    const val minorVersion = 1
    const val patchVersion = 8
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.getstream"
}
