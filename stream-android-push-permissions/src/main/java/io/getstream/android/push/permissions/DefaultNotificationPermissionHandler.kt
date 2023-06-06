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

import android.app.Activity
import android.app.Application
import android.widget.Toast
import io.getstream.log.taggedLogger

/**
 * Shows a [Toast] with a link to app settings on [android.Manifest.permission.POST_NOTIFICATIONS] permission denial.
 *
 */
public class DefaultNotificationPermissionHandler private constructor() : NotificationPermissionHandler,
    ActivityLifecycleCallbacks() {

    private val logger by taggedLogger("Push:Default-NPH")

    private var currentActivity: Activity? = null

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        currentActivity = activity
    }

    override fun onLastActivityStopped(activity: Activity) {
        super.onLastActivityStopped(activity)
        currentActivity = null
    }

    override fun onPermissionDenied() {
        logger.i { "[onPermissionDenied] currentActivity: $currentActivity" }
        currentActivity?.showNotificationBlocked()
    }

    private fun Activity.showNotificationBlocked() {
        Toast.makeText(
            this, R.string.stream_push_permissions_notifications_message, Toast.LENGTH_LONG
        ).show()
    }

    public companion object {
        public fun createDefaultNotificationPermissionHandler(
            application: Application,
        ): DefaultNotificationPermissionHandler = DefaultNotificationPermissionHandler()
            .also { application.registerActivityLifecycleCallbacks(it) }
    }
}
