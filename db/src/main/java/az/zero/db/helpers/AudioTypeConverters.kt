package az.zero.db.helpers

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import az.zero.db.entities.DBAudio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@ProvidedTypeConverter
class AudioTypeConverters {

    @TypeConverter
    fun listOfAudioToString(list: List<DBAudio>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun stringToListOfAudio(string: String?): List<DBAudio>? {
        val listType = object : TypeToken<List<DBAudio?>?>() {}.type
        return string?.let { Gson().fromJson(it, listType) }
    }

}