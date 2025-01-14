pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url="https://developer.huawei.com/repo/") {
            content {
                includeGroup("com.huawei.agconnect")
                includeGroup("com.huawei.android.hms")
                includeGroup("com.huawei.hms")
                includeGroup("com.huawei.hmf")
            }
        }
    }
}
rootProject.name = "Push"
include(":app")
include(":stream-android-push")
include(":stream-android-push-delegate")
include(":stream-android-push-firebase")
include(":stream-android-push-huawei")
include(":stream-android-push-permissions")
include(":stream-android-push-permissions-snackbar")
include(":stream-android-push-xiaomi")
