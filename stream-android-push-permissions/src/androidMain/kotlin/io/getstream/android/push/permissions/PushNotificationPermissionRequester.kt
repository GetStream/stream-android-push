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
package io.getstream.android.push.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class PushNotificationPermissionRequester private constructor() : ActivityLifecycleCallbacks() {
  private val TAG = "PnPermissionRequester"
  private val pushNotificationPermissionCallbacks = mutableListOf<PushNotificationPermissionCallback>()
  private var currentActivity: Activity? = null
  private val permissionContract = ActivityResultContracts.RequestPermission()
  private val uiScope by lazy { CoroutineScope(Dispatchers.Main) }

  fun addCallback(callback: PushNotificationPermissionCallback) {
    pushNotificationPermissionCallbacks.add(callback)
  }

  override fun onActivityCreated(
    activity: Activity,
    bunlde: Bundle?
  ) {
    Log.v(TAG, "[onActivityCreated] activity: $activity")
    super.onActivityCreated(activity, bunlde)
    currentActivity = activity
  }

  override fun onActivityStarted(activity: Activity) {
    Log.v(TAG, "[onActivityStarted] activity: $activity")
    currentActivity = activity
    activity.registerPermissionCallback()
    super.onActivityStarted(activity)
  }

  override fun onActivityResumed(activity: Activity) {
    Log.v(TAG, "[onActivityResumed] activity: $activity")
    currentActivity = activity
    super.onActivityResumed(activity)
  }

  override fun onActivityStopped(activity: Activity) {
    Log.v(TAG, "[onActivityStopped] activity: $activity")
    activity.unregisterPermissionCallback()
    super.onActivityStopped(activity)
  }

  override fun onFirstActivityStarted(activity: Activity) {
    Log.i(TAG, "[onFirstActivityStarted] activity: $activity")
    super.onFirstActivityStarted(activity)
    pushNotificationPermissionCallbacks.forEach { it.onAppLaunched() }
  }

  override fun onLastActivityStopped(activity: Activity) {
    Log.i(TAG, "[onLastActivityStopped] activity: $activity")
    super.onLastActivityStopped(activity)
    currentActivity = null
  }

  internal fun requestPermission() {
    uiScope.launch { currentActivity?.requestPermission() }
  }

  internal fun onPermissionStatus(permissionStatus: NotificationPermissionStatus) {
    Log.v(TAG, "[onPermissionStatus] permissionStatus: $permissionStatus")
    pushNotificationPermissionCallbacks.forEach { it.onPermissionStatusChanged(permissionStatus) }
  }

  private fun Activity.registerPermissionCallback() {
    if (this !is ComponentActivity) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    Log.i(TAG, "[registerPermissionCallback] activity: ${this::class.simpleName}")
    val launcher =
      registerForActivityResult(permissionContract) { isGranted: Boolean ->
        when (isGranted) {
          true -> onPermissionStatus(NotificationPermissionStatus.GRANTED)
          else -> onPermissionStatus(NotificationPermissionStatus.DENIED)
        }
      }
    Log.v(TAG, "[registerPermissionCallback] launcher: $launcher")
    putActivityResultLauncher(launcher)
  }

  private fun Activity.unregisterPermissionCallback() {
    if (this !is ComponentActivity) return
    Log.i(TAG, "[unregisterPermissionCallback] activity: ${this::class.simpleName}")
    val launcher = getActivityResultLauncher()
    Log.d(TAG, "[unregisterPermissionCallback] found launcher: $launcher")
    launcher?.unregister()
  }

  private fun Activity.requestPermission() {
    when {
      Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
        Log.w(TAG, "[requestPermission] not supported on this version")
        onPermissionStatus(NotificationPermissionStatus.GRANTED)
      }
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED -> {
        Log.v(TAG, "[requestPermission] already granted")
        onPermissionStatus(NotificationPermissionStatus.GRANTED)
      }
      ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS) -> {
        Log.i(TAG, "[requestPermission] rationale requested")
        onPermissionStatus(NotificationPermissionStatus.RATIONALE_NEEDED)
      }
      else -> {
        val launcher = (this as? ComponentActivity)?.getActivityResultLauncher()
        Log.i(TAG, "[requestPermission] launcher: $launcher")
        launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        onPermissionStatus(NotificationPermissionStatus.REQUESTED)
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun ComponentActivity.getActivityResultLauncher(): ActivityResultLauncher<String>? = contentLayout()
    .getTag(R.id.stream_post_notifications_permission) as? ActivityResultLauncher<String>

  private fun ComponentActivity.putActivityResultLauncher(launcher: ActivityResultLauncher<String>) = contentLayout()
    .setTag(R.id.stream_post_notifications_permission, launcher)

  private fun ComponentActivity.contentLayout(): View =
    findViewById<ViewGroup>(android.R.id.content)

  interface PushNotificationPermissionCallback {
    fun onAppLaunched()
    fun onPermissionStatusChanged(status: NotificationPermissionStatus)
  }

  companion object {

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var INSTANCE: PushNotificationPermissionRequester? = null

    fun getInstance(application: Application): PushNotificationPermissionRequester {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: PushNotificationPermissionRequester().also {
          INSTANCE = it
          application.registerActivityLifecycleCallbacks(it)
        }
      }
    }
  }
}
