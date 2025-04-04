/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
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
package io.getstream.android.push.permissions

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

public class PushPermissionsInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    PushNotificationPermissionRequester.getInstance(context.applicationContext as Application)
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
