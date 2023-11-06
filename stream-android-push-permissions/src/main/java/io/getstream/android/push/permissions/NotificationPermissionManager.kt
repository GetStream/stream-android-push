/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
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

package io.getstream.android.push.permissions

import android.app.Application
import io.getstream.log.taggedLogger

public class NotificationPermissionManager private constructor(
    private val pushNotificationPermissionRequester: PushNotificationPermissionRequester,
    private val requestPermissionOnAppLaunch: () -> Boolean,
    private val onPermissionStatus: (NotificationPermissionStatus) -> Unit,
) : PushNotificationPermissionRequester.PushNotificationPermissionCallback {
    private val logger by taggedLogger("Push:Notifications-PM")
    private var started = false

    private fun initialize() {
        logger.d { "[initialize] no args" }
        pushNotificationPermissionRequester.addCallback(this)
    }

    public fun start() {
        logger.d { "[start] no args" }
        requestPermission()
    }

    public fun stop() {
        logger.d { "[stop] no args" }
        started = false
    }

    override fun onAppLaunched() {
        logger.d { "[onAppLaunched] no args" }
        if (requestPermissionOnAppLaunch()) {
            requestPermission()
        }
    }

    override fun onPermissionStatusChanged(status: NotificationPermissionStatus) {
        onPermissionStatus(status)
    }

    private fun requestPermission() {
        if (!started) {
            pushNotificationPermissionRequester.requestPermission()
        }
        started = true
    }

    public companion object {
        public fun createNotificationPermissionsManager(
            application: Application,
            requestPermissionOnAppLaunch: () -> Boolean,
            onPermissionStatus: (NotificationPermissionStatus) -> Unit,
        ): NotificationPermissionManager =
            NotificationPermissionManager(
                PushNotificationPermissionRequester.getInstance(application),
                requestPermissionOnAppLaunch,
                onPermissionStatus,
            ).also { it.initialize() }
    }
}
