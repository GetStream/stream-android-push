/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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
package io.getstream.android.push.firebase

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.firebase.messaging.FirebaseMessaging
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.android.push.PushProvider
import io.getstream.log.StreamLog

/**
 * Generator responsible for providing information needed to register Firebase push notifications provider
 *
 * @param firebaseMessaging The [FirebaseMessaging] instance used for retrieving the token.
 * @param providerName The push provider name.
 * @param context The context used to check if Firebase is available on this device.
 */
public class FirebasePushDeviceGenerator @JvmOverloads constructor(
  private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
  private val providerName: String,
  private val context: Context
) : PushDeviceGenerator {

  private val logger = StreamLog.getLogger("Push:Firebase")

  override fun isValidForThisDevice(): Boolean {
    val isValidForThisDevice =
      GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    logger.i { "Is Firebase available on on this device -> $isValidForThisDevice" }
    return isValidForThisDevice
  }

  override fun onPushDeviceGeneratorSelected() {
    FirebaseMessagingDelegate.fallbackProviderName = providerName
  }

  override fun asyncGeneratePushDevice(onPushDeviceGenerated: (pushDevice: PushDevice) -> Unit) {
    logger.i { "Getting Firebase token" }
    firebaseMessaging.token.addOnCompleteListener {
      if (it.isSuccessful) {
        logger.i { "Firebase returned token successfully" }
        onPushDeviceGenerated(
          PushDevice(
            token = it.result,
            pushProvider = PushProvider.FIREBASE,
            providerName = providerName
          )
        )
      } else {
        logger.i { "Error: Firebase didn't returned token" }
      }
    }
  }
}
