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

package io.getstream.android.push.huawei

import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

internal class ChatHuaweiMessagingService : HmsMessageService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "[onHuaweiMessageReceived] remoteMessage: $remoteMessage")
        try {
            HuaweiMessagingDelegate.handleRemoteMessage(remoteMessage)
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "[onHuaweiMessageReceived] error while handling remote message", exception)
        } finally {
            stopSelf()
        }
    }

    override fun onNewToken(token: String) {
        try {
            HuaweiMessagingDelegate.registerHuaweiToken(token)
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "[onHuaweiNewToken] error while registering Huawei Token", exception)
        }
    }

    companion object {
        private const val TAG = "Push:Huawei"
    }
}
