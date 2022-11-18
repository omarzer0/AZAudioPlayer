package az.zero.base.utils

enum class AudioSortBy {
    DATE_OF_UPDATE,
    AUDIO_NAME,
    ARTIST_NAME
}

fun String.toAudioSortBy() = when (this) {
    AudioSortBy.AUDIO_NAME.name -> AudioSortBy.AUDIO_NAME
    AudioSortBy.ARTIST_NAME.name -> AudioSortBy.ARTIST_NAME
    else -> AudioSortBy.DATE_OF_UPDATE
//    else -> throw Exception("AudioSortBy: No field found correspond to this string")
}