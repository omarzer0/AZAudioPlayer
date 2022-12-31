package az.zero.azaudioplayer.data.local.model.audio

import az.zero.db.entities.DBAudio

data class SelectableAudio(
    val audio: DBAudio,
    val selected: Boolean = false
)
