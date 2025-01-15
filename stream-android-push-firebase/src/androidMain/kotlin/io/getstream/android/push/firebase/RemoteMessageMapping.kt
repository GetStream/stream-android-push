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

import com.google.firebase.messaging.RemoteMessage

/**
 * Maps the Firebase-specific [RemoteMessage] to a Stream-specific [StreamRemoteMessage].
 */
public fun RemoteMessage.toStreamRemoteMessage(): StreamRemoteMessage {
  return StreamRemoteMessage(
    data = data,
    senderId = senderId,
    from = from,
    to = to,
    messageType = messageType,
    messageId = messageId,
    collapseKey = collapseKey,
    sentTime = sentTime,
    ttl = ttl,
    priority = priority,
    originalPriority = originalPriority
  )
}
