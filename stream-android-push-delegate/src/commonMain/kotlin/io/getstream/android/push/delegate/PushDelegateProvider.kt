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

import kotlin.concurrent.Volatile

public interface PushDelegateProvider {

  public companion object {
    @Volatile
    public var isInitialized: Boolean = false
    public const val METADATA_VALUE: String = "io.getstream.android.push.PushDelegate"
    internal var _delegates: List<PushDelegate> = emptyList()
    public val delegates: List<PushDelegate>
      get() = _delegates
  }
}
