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

import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log

public class PushDelegateProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        initializeDelegates()
        return true
    }

    private fun initializeDelegates() {
        context?.let {
            synchronized(Companion) {
                if (!isInitialized) {
                    it.discoverDelegates()
                }
                isInitialized = true
            }
        }
    }

    private fun Context.discoverDelegates() {
        val provider = ComponentName(packageName, PushDelegateProvider::class.java.name)
        val providerInfo = packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
        discoverDelegates(providerInfo.metaData)
    }

    private fun discoverDelegates(metadata: Bundle) {
        _delegates = metadata
            .keySet()
            .filter { metadata.getString(it) == METADATA_VALUE }
            .mapNotNull { it.toPushDelegate() }
    }

    private fun String.toPushDelegate(): PushDelegate? =
        try {
            Class.forName(this)
                .takeIf { PushDelegate::class.java.isAssignableFrom(it) }
                ?.getDeclaredConstructor()
                ?.newInstance() as? PushDelegate
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "PushDelegate not created from '$this'", e)
            null
        }

    override fun query(
        uri: Uri,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?,
    ): Cursor? {
        throw IllegalStateException("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        throw IllegalStateException("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalStateException("Not allowed.")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String?>?,
    ): Int {
        throw IllegalStateException("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?,
    ): Int {
        throw IllegalStateException("Not allowed.")
    }

    public companion object {
        @Volatile
        private var isInitialized = false
        private const val METADATA_VALUE = "io.getstream.android.push.PushDelegate"
        private const val TAG = "Push"
        private var _delegates: List<PushDelegate> = emptyList()
        public val delegates: List<PushDelegate>
            get() = _delegates
    }
}
