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
package io.getstream.android.push.delegate
import android.content.Context
import io.getstream.android.push.PushDevice

public abstract class PushDelegate(public val context: Context) {
  /**
   * Handles push message payload.
   * If the payload can't be handled because doesn't contain the needed data, return false to notify you this
   * push message payload needs to be handled by you.
   *
   * @param metadata The metadata of a remote message.
   * @param payload The payload to be handled.
   * @return True if the payload was handled.
   */
  public abstract fun handlePushMessage(
    metadata: Map<String, Any?>,
    payload: Map<String, Any?>
  ): Boolean

  /**
   * Register a new [PushDevice]
   *
   * @param pushDevice The PushDevice to be registered.
   */
  public abstract fun registerPushDevice(pushDevice: PushDevice)
}
