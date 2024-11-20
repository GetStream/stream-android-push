/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
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

public data class StreamRemoteMessage(
  val data: Map<String, String>,
  val senderId: String?,
  val from: String?,
  val to: String?,
  val messageType: String?,
  val messageId: String?,
  val collapseKey: String?,
  val sentTime: Long,
  val ttl: Int,
  val priority: Int,
  val originalPriority: Int
)
