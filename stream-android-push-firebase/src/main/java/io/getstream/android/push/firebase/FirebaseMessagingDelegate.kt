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
package io.getstream.android.push.firebase

import com.google.firebase.messaging.RemoteMessage
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushProvider
import io.getstream.android.push.delegate.PushDelegateProvider
import io.getstream.log.StreamLog

/**
 * Helper class for delegating Firebase push messages to the Stream Chat SDK.
 */
public object FirebaseMessagingDelegate {
  internal var fallbackProviderName: String? = null
  private val logger = StreamLog.getLogger("Push:Firebase")

  /**
   * Handles [remoteMessage] from Firebase.
   * If the [remoteMessage] wasn't sent from the Stream Server and doesn't contain the needed data,
   * return false to notify you that this remoteMessage needs to be handled by you.
   *
   * @param remoteMessage The message to be handled.
   * @return True if the [remoteMessage] was sent from the Stream Server and has been handled.
   */
  @JvmStatic
  public fun handleRemoteMessage(remoteMessage: RemoteMessage): Boolean {
    logger.d { "[handleRemoteMessage] handling remote message: $remoteMessage" }
    return PushDelegateProvider.delegates.any { pushDelegate ->
      pushDelegate.handlePushMessage(
        metadata = remoteMessage.extractMetadata(),
        payload = remoteMessage.data
      ).also { handled ->
        if (handled) logger.d { "[handleRemoteMessage] message handled successfully by $pushDelegate" }
      }
    }
      .also { handled ->
        if (!handled) logger.d { "[handleRemoteMessage] message was not handled by any Push Delete" }
      }
  }

  /**
   * Register new Firebase Token.
   *
   * @param token provided by Firebase.
   * @param providerName The provider name.
   */
  @JvmStatic
  public fun registerFirebaseToken(
    token: String,
    providerName: String
  ) {
    (providerName.takeUnless { it.isBlank() } ?: fallbackProviderName)
      ?.let {
        PushDevice(
          token = token,
          pushProvider = PushProvider.FIREBASE,
          providerName = it
        )
      }
      ?.let {
        PushDelegateProvider.delegates.forEach { delegate -> delegate.registerPushDevice(it) }
      }
  }

  private fun RemoteMessage.extractMetadata(): Map<String, Any> {
    return hashMapOf<String, Any>().apply {
      senderId?.also { put("firebase.sender_id", it) }
      from?.also { put("firebase.from", it) }
      to?.also { put("firebase.to", it) }
      messageType?.also { put("firebase.message_type", it) }
      messageId?.also { put("firebase.message_id", it) }
      collapseKey?.also { put("firebase.collapse_key", it) }
      put("firebase.sent_time", sentTime)
      put("firebase.ttl", ttl)
      put("firebase.priority", priority)
      put("firebase.priority", originalPriority)
    }
  }
}
