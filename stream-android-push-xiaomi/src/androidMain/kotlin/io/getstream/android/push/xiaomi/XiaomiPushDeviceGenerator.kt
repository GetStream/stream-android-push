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
package io.getstream.android.push.xiaomi

import android.content.Context
import com.xiaomi.channel.commonutils.android.Region
import com.xiaomi.mipush.sdk.MiPushClient
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.android.push.PushProvider
import io.getstream.log.StreamLog
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Generator responsible for providing information needed to register Xiaomi push notifications provider.
 *
 * @property appId The App ID for the app registered on Xiaomi Developer Console.
 * @property appKey The App Key for the app registered on Xiaomi Developer Console.
 * @property providerName Name for the provider name.
 * @property region Computer area to be used by Xiaomi SDK.
 */
public class XiaomiPushDeviceGenerator(
  context: Context,
  private val appId: String,
  private val appKey: String,
  private val providerName: String,
  private val region: Region = Region.Global
) : PushDeviceGenerator {
  private val logger = StreamLog.getLogger("Push:Xiaomi")
  private val appContext = context.applicationContext
  private var isAlreadyRegistered = AtomicBoolean(false)

  override fun isValidForThisDevice(): Boolean = true

  override fun onPushDeviceGeneratorSelected() {
    XiaomiMessagingDelegate.fallbackProviderName = providerName
  }

  override fun asyncGeneratePushDevice(onPushDeviceGenerated: (pushDevice: PushDevice) -> Unit) {
    logger.i { "Getting Xiaomi token" }
    if (isAlreadyRegistered.compareAndSet(false, true)) {
      MiPushClient.setRegion(region)
      MiPushClient.registerPush(appContext, appId, appKey)
    } else {
      MiPushClient.getRegId(appContext)?.let {
        onPushDeviceGenerated(PushDevice(it, PushProvider.XIAOMI, providerName))
      }
    }
  }
}
