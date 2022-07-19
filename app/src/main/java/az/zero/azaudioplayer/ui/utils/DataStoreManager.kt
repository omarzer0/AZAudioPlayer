package az.zero.azaudioplayer.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
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

    suspend fun read(key: String, defaultValue: String): String {
        return mDataStore.data.map { settings ->
            settings[stringPreferencesKey(key)] ?: defaultValue
        }.first().toString()
    }

    suspend fun clearDataStore() {
        mDataStore.edit { it.clear() }
    }

    fun saveLastPlayedAudio(audioID: String) {
        scope.launch {
            write(LAST_PLAYED_AUDIO_ID_KEY, audioID)
        }
    }

    companion object {
        const val LAST_PLAYED_AUDIO_ID_KEY = ""
    }
}