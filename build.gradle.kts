@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.nexus.plugin) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

// Where to publish: "streamRepo", "central", or both, comma-separated. The release workflow
// always passes it; with nothing passed this stays on Central so a local build behaves as before.
// streamRepo stages a Maven-2 tree under build/staged-repo for a separate upload job.
val publishTargets = providers.gradleProperty("streamPublishTargets")
    .getOrElse("central")
    .split(",")
    .map(String::trim)
    .filter(String::isNotEmpty)
    .toSet()

require(publishTargets.isNotEmpty() && (publishTargets - setOf("central", "streamRepo")).isEmpty()) {
    "'streamPublishTargets' must be a comma-separated subset of central, streamRepo but was '$publishTargets'"
}

subprojects {
    plugins.withId(rootProject.libs.plugins.nexus.plugin.get().pluginId) {
        if ("central" in publishTargets) {
            extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
                publishToMavenCentral(automaticRelease = true)
            }
        }
        if ("streamRepo" in publishTargets) {
            extensions.configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "streamRepoStaging"
                        url = rootProject.layout.buildDirectory.dir("staged-repo").get().asFile.toURI()
                    }
                }
            }
        }
    }
}

apiValidation {
    ignoredProjects.addAll(listOf("app"))
    nonPublicMarkers.add("kotlin.PublishedApi")
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    }

    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            ktlint().setUseExperimental(true).editorConfigOverride(
                mapOf(
                    "indent_size" to "2",
                    "continuation_indent_size" to "2"
                )
            )
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("${layout.buildDirectory}/**/*.kts")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}