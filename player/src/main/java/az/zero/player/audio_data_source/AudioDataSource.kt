package az.zero.player.audio_data_source

import androidx.lifecycle.distinctUntilChanged
import az.zero.player.AudioRepository
import az.zero.player.audio_data_source.State.*
import javax.inject.Inject

class AudioDataSource @Inject constructor(
    private val audioRepository: AudioRepository
) {
    val audiosLiveData = audioRepository.getAllAudio().distinctUntilChanged()

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