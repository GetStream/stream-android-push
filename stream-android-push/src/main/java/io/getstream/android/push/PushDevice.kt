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
package io.getstream.android.push

/**
 * Device's information needed to register push notifications.
 *
 * @property token Device's token generated by push notification provider.
 * @property pushProvider Push notifications provider type.
 * @property providerName Push notifications provider name.
 * @see [PushProvider]
 */
public data class PushDevice(
  val token: String,
  val pushProvider: PushProvider,
  val providerName: String
)
