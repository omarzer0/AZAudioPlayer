package az.zero.base.utils

enum class AudioSortBy {
    ARTIST_DATE_OF_UPDATE,
    ARTIST_BY_AUDIO_NAME,
    ARTIST_BY_ARTIST_NAME
}

fun String.toAudioSortBy() = when (this) {
    AudioSortBy.ARTIST_BY_AUDIO_NAME.name -> AudioSortBy.ARTIST_BY_AUDIO_NAME
    AudioSortBy.ARTIST_BY_ARTIST_NAME.name -> AudioSortBy.ARTIST_BY_ARTIST_NAME
    else -> AudioSortBy.ARTIST_DATE_OF_UPDATE
//    else -> throw Exception("AudioSortBy: No field found correspond to this string")
}