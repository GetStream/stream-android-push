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

package io.getstream.android.push.permissions.snackbar

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import io.getstream.android.push.permissions.ActivityLifecycleCallbacks
import io.getstream.android.push.permissions.NotificationPermissionHandler
import io.getstream.log.taggedLogger

/**
 * Shows a [Snackbar] with a link to app settings on [android.Manifest.permission.POST_NOTIFICATIONS] permission denial.
 *
 */
public class SnackbarNotificationPermissionHandler private constructor() : NotificationPermissionHandler,
    ActivityLifecycleCallbacks() {

    private val logger by taggedLogger("Push:Snackbar-NPH")

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
        val contentLayout = findViewById<ViewGroup>(android.R.id.content)
        Snackbar.make(
            contentLayout,
            R.string.stream_push_permissions_notifications_message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.stream_push_permissions_setting_button) {
            openSystemSettings()
        }.show()
    }

    /**
     * Forwards user to the application settings screen.
     */
    private fun Context.openSystemSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri: Uri = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = uri
        }
        startActivity(intent)
    }

    public companion object {
        public fun createSnackbarNotificationPermissionHandler(
            application: Application,
        ): SnackbarNotificationPermissionHandler = SnackbarNotificationPermissionHandler()
            .also { application.registerActivityLifecycleCallbacks(it) }
    }
}
