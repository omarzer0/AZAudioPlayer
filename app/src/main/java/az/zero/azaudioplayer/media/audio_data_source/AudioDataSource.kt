/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package az.zero.azaudioplayer.media.audio_data_source

import androidx.lifecycle.distinctUntilChanged
import az.zero.azaudioplayer.db.AudioDao
import az.zero.azaudioplayer.media.audio_data_source.State.*
import javax.inject.Inject

class AudioDataSource @Inject constructor(
    private val audioDao: AudioDao
) {
    val audiosLiveData = audioDao.getAllDbAudio().distinctUntilChanged()

    init {
        audiosLiveData.observeForever {
            if (it.isNotEmpty()) state = STATE_INITIALIZED
        }
    }
    // TODO Here define the list of playlist that vm and browse tree observe

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        // if not fully loaded
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            // not ready so add it to waiting onReadyListeners
            onReadyListeners += action
            return false
        }
        // else call the action with either success (if INITIALIZED successfully) or error
        action(state == STATE_INITIALIZED)
        return true
    }

}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}