package az.zero.player.audio_data_source

import android.app.PendingIntent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import az.zero.db.entities.DBAudio
import az.zero.player.audio_data_source.State.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioDataSource @Inject constructor() {

    private val _audiosLiveData = MutableLiveData<List<DBAudio>>()
    val audiosLiveData: LiveData<List<DBAudio>> = _audiosLiveData

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

    fun updateAudioList(newAudioList: List<DBAudio>) {
        Log.e("playPauseOrToggleData", "${newAudioList.size}")
        _audiosLiveData.postValue(newAudioList)
//        state = STATE_INITIALIZED
    }

    var pendingIntent: PendingIntent? = null
        private set

    fun setDestinationAndGraphIds(mPendingIntent: PendingIntent) {
        pendingIntent = mPendingIntent
    }

}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}