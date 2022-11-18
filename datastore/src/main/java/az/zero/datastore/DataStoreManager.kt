package az.zero.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import az.zero.base.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context,
    @ApplicationScope private val scope: CoroutineScope,
) {

    private val mDataStore = appContext.dataStore

    val sortAudioByFlow = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("DataStoreManager", "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[stringPreferencesKey(SORT_AUDIO_BY)] ?: ""
        }

    val sortAlbumByFlow = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("DataStoreManager", "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[stringPreferencesKey(SORT_ALBUM_BY)] ?: ""
        }


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

    fun saveSortAudioBy(sortOrderName: String) {
        scope.launch {
            write(SORT_AUDIO_BY, sortOrderName)
        }
    }

    fun saveSortAlbumBy(sortOrderName: String) {
        scope.launch {
            write(SORT_ALBUM_BY, sortOrderName)
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
        const val SORT_AUDIO_BY = "SORT_AUDIO_BY"
        const val SORT_ALBUM_BY = "SORT_ALBUM_BY"
    }

    suspend fun clearDataStore() {
        mDataStore.edit { it.clear() }
    }
}