package az.zero.azaudioplayer.data.db.helpers

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import az.zero.azaudioplayer.domain.models.Audio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@ProvidedTypeConverter
class AudioTypeConverters {

    @TypeConverter
    fun listOfAudioToString(list: List<Audio>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun stringToListOfAudio(string: String?): List<Audio>? {
        val listType = object : TypeToken<List<Audio?>?>() {}.type
        return string?.let { Gson().fromJson(it, listType) }
    }

}