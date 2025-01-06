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
package io.getstream.android.push.huawei

import com.huawei.hms.push.RemoteMessage
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushProvider
import io.getstream.android.push.delegate.PushDelegateProvider
import io.getstream.log.StreamLog
import kotlin.jvm.Throws

/**
 * Helper class for delegating Huawei push messages to the Stream Chat SDK.
 */
public object HuaweiMessagingDelegate {
  internal var fallbackProviderName: String? = null
  private val logger = StreamLog.getLogger("Push:Huawei")

  /**
   * Handles [remoteMessage] from Huawei.
   * If the [remoteMessage] wasn't sent from the Stream Server and doesn't contain the needed data,
   * return false to notify you that this remoteMessage needs to be handled by you.
   *
   * @param remoteMessage The message to be handled.
   * @return True if the [remoteMessage] was sent from the Stream Server and has been handled.
   */
  @Throws(IllegalStateException::class)
  @JvmStatic
  public fun handleRemoteMessage(remoteMessage: RemoteMessage): Boolean {
    logger.d { "[handleRemoteMessage] handling remote message: $remoteMessage" }
    return PushDelegateProvider.delegates.any { pushDelegate ->
      pushDelegate.handlePushMessage(
        metadata = remoteMessage.extractMetadata(),
        payload = remoteMessage.dataOfMap
      ).also { handled ->
        if (handled) logger.d { "[handleRemoteMessage] message handled successfully by $pushDelegate" }
      }
    }
      .also { handled ->
        if (!handled) logger.d { "[handleRemoteMessage] message was not handled by any Push Delegate" }
      }
  }

  /**
   * Register new Huawei Token.
   *
   * @param token provided by Huawei.
   * @param providerName The provider name.
   */
  @Throws(IllegalStateException::class)
  @JvmStatic
  public fun registerHuaweiToken(
    token: String,
    providerName: String
  ) {
    (providerName.takeUnless { it.isBlank() } ?: fallbackProviderName)
      ?.let {
        PushDevice(
          token = token,
          pushProvider = PushProvider.HUAWEI,
          providerName = it
        )
      }
      ?.let {
        PushDelegateProvider.delegates.forEach { delegate -> delegate.registerPushDevice(it) }
      }
  }

  private fun RemoteMessage.extractMetadata(): Map<String, Any> {
    return hashMapOf<String, Any>().apply {
      from?.also { put("huawei.from", it) }
      to?.also { put("huawei.to", it) }
      messageType?.also { put("huawei.message_type", it) }
      messageId?.also { put("huawei.message_id", it) }
      collapseKey?.also { put("huawei.collapse_key", it) }
      analyticInfoMap?.also { put("huawei.analytic_info_map", it) }
      token?.also { put("huawei.token", token) }
      put("huawei.sent_time", sentTime)
      put("huawei.send_mode", sendMode)
      put("huawei.receipt_mode", receiptMode)
      put("huawei.urgency", urgency)
      put("huawei.original_urgency", originalUrgency)
      put("huawei.original_urgency", originalUrgency)
      put("huawei.ttl", ttl)
    }
  }
}
