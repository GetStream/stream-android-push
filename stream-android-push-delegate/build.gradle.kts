/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-android-push/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

import io.getstream.android.push.Configuration

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.nexus.plugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "stream-android-push"
    coordinates(
        Configuration.artifactGroup,
        artifactId,
        Configuration.versionName
    )

    pom {
        name.set(artifactId)
        description.set("The Stream Push Registration Library offers a pluggable and flexible approach to managing push notifications.")
    }
}

kotlin {
    androidTarget { publishLibraryVariants("release") }
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    @Suppress("OPT_IN_USAGE")
    applyHierarchyTemplate {
        common {
            group("android") {
                withAndroidTarget()
            }
            group("jvm") {
                withJvm()
            }
            group("skia") {
                withJvm()
                group("apple") {
                    group("ios") {
                        withIosX64()
                        withIosArm64()
                        withIosSimulatorArm64()
                    }
                    group("macos") {
                        withMacosX64()
                        withMacosArm64()
                    }
                }
            }
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }

        commonMain {
            dependencies {
                api(project(":stream-android-push"))
                implementation(libs.stream.log)
            }
        }
    }

    explicitApi()
}

android {
    compileSdk = Configuration.compileSdk
    namespace = "io.getstream.android.push"
    defaultConfig {
        minSdk = Configuration.minSdk
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}