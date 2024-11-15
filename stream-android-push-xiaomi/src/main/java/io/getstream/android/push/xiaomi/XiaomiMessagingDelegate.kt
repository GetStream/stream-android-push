/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.android.push.xiaomi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.xiaomi.mipush.sdk.MiPushClient
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushProvider
import io.getstream.android.push.delegate.PushDelegateProvider
import io.getstream.log.StreamLog

/**
 * Helper class for delegating Xiaomi push messages to the Stream Chat SDK.
 */
public object XiaomiMessagingDelegate {
  internal var fallbackProviderName: String? = null
  private val logger = StreamLog.getLogger("Push:Xiaomi")

  private val mapAdapter: JsonAdapter<MutableMap<String, String>> by lazy {
    Moshi.Builder()
      .build()
      .adapter(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java))
  }

  /**
   * Handles [miPushMessage] from Xiaomi.
   * If the [miPushMessage] wasn't sent from the Stream Server and doesn't contain the needed data,
   * return false to notify you that this remoteMessage needs to be handled by you.
   *
   * @param miPushMessage The message to be handled.
   * @return True if the [miPushMessage] was sent from the Stream Server and has been handled.
   */
  @JvmStatic
  public fun handleMiPushMessage(miPushMessage: MiPushMessage): Boolean {
    logger.d { "[handleRemoteMessage] handling remote message: $miPushMessage" }
    return PushDelegateProvider.delegates.any { pushDelegate ->
      pushDelegate.handlePushMessage(
        metadata = miPushMessage.extractMetadata(),
        payload = miPushMessage.contentMap
      ).also { handled ->
        if (handled) logger.d { "[handleRemoteMessage] message handled successfully by $pushDelegate" }
      }
    }
      .also { handled ->
        if (!handled) logger.d { "[handleRemoteMessage] message was not handled by any Push Delete" }
      }
  }

  /**
   * Register new Xiaomi Token.
   *
   * @param miPushCommandMessage provided by Xiaomi.
   * @param providerName The provider name.
   */
  @JvmStatic
  public fun registerXiaomiToken(
    miPushCommandMessage: MiPushCommandMessage,
    providerName: String
  ) {
    miPushCommandMessage
      .takeIf { it.command == MiPushClient.COMMAND_REGISTER }
      ?.commandArguments
      ?.get(0)
      ?.run {
        (providerName.takeUnless { it.isBlank() } ?: fallbackProviderName)
          ?.let {
            PushDevice(
              token = this,
              pushProvider = PushProvider.XIAOMI,
              providerName = it
            )
          }
          ?.let {
            PushDelegateProvider.delegates.forEach { delegate -> delegate.registerPushDevice(it) }
          }
      }
  }

  /**
   * Transform [MiPushMessage.content] into a [Map].
   *
   * Return a [Map] obtained from the value of [MiPushMessage.content] or an empty map if content was empty.
   */
  private val MiPushMessage.contentMap: Map<String, String>
    get() = mapAdapter.fromJson(content) ?: emptyMap()

  private fun MiPushMessage.extractMetadata(): Map<String, Any> {
    return hashMapOf<String, Any>().apply {
      put("xiaomi.message_type", messageType)
      messageId?.also { put("xiaomi.message_id", it) }
      userAccount?.also { put("xiaomi.user_account", it) }
      title?.also { put("xiaomi.title", it) }
      topic?.also { put("xiaomi.topic", it) }
      alias?.also { put("xiaomi.alias", it) }
      category?.also { put("xiaomi.category", it) }
      description?.also { put("xiaomi.description", it) }
      put("xiaomi.is_arrived_message", isArrivedMessage)
      put("xiaomi.is_notified", isNotified)
      put("xiaomi.notify_type", notifyType)
      put("xiaomi.notify_id", notifyId)
      put("xiaomi.pass_through", passThrough)
      put("xiaomi.extra", extra)
    }
  }
}
