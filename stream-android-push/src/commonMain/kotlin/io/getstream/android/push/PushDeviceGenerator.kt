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
package io.getstream.android.push

/**
 * Generator responsible for providing information needed to register the push notifications provider
 */
public interface PushDeviceGenerator {
  /**
   * Checks if push notification provider is valid for this device
   */
  public fun isValidForThisDevice(): Boolean

  /**
   * Called when this [PushDeviceGenerator] has been selected to be used.
   */
  public fun onPushDeviceGeneratorSelected()

  /**
   * Asynchronously generates a [PushDevice] and calls [onPushDeviceGenerated] callback once it's ready
   *
   * @param onPushDeviceGenerated Callback that will be called once the [PushDevice] is generated
   */
  public fun asyncGeneratePushDevice(onPushDeviceGenerated: (pushDevice: PushDevice) -> Unit)
}
