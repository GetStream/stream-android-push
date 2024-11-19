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
 * Push notifications provider type.
 */
public enum class PushProvider(public val key: String) {
  /** Firebase push notification provider */
  FIREBASE("firebase"),

  /** Huawei push notification provider */
  HUAWEI("huawei"),

  /** Xiaomi push notification provider */
  XIAOMI("xiaomi"),

  /** Unknown push notification provider */
  UNKNOWN("unknown"),
  ;

  public companion object {
    public fun fromKey(key: String): PushProvider = entries.firstOrNull { it.key == key } ?: UNKNOWN
  }
}
