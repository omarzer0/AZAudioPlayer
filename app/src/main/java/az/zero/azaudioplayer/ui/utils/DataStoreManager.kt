package az.zero.azaudioplayer.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import az.zero.azaudioplayer.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context,
    @ApplicationScope private val scope: CoroutineScope
) {

    private val mDataStore = appContext.dataStore

    suspend fun write(key: String, value: String) {
        mDataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    suspend fun write(key: String, value: Int) {
        mDataStore.edit { settings ->
            settings[intPreferencesKey(key)] = value
        }
    }

    suspend fun read(key: String, defaultValue: String): String {
        return mDataStore.data.map { settings ->
            settings[stringPreferencesKey(key)] ?: defaultValue
        }.first().toString()
    }

    suspend fun read(key: String, defaultValue: Int): Int {
        return mDataStore.data.map { settings ->
            settings[intPreferencesKey(key)] ?: defaultValue
        }.first()
    }


    fun saveLastPlayedAudio(audioID: String) {
        scope.launch {
            write(LAST_PLAYED_AUDIO_ID_KEY, audioID)
        }
    }

    fun saveRepeatMode(repeatMode: Int) {
        scope.launch {
            write(REPEAT_MODE, repeatMode)
        }
    }

    companion object {
        const val LAST_PLAYED_AUDIO_ID_KEY = "LAST_PLAYED_AUDIO_ID_KEY"
        const val REPEAT_MODE = "REPEAT_MODE"
    }

    suspend fun clearDataStore() {
        mDataStore.edit { it.clear() }
    }
}