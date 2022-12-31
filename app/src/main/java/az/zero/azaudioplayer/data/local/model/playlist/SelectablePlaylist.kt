package az.zero.azaudioplayer.data.local.model.playlist

import az.zero.db.entities.DBPlaylist

data class SelectablePlaylist(
    val isSelected: Boolean,
    val playlist: DBPlaylist
)