package az.zero.base.utils

enum class AlbumSortBy {
    ALBUM_SORT_ASC,
    ALBUM_SORT_DESC
}

fun String.toAlbumSortBy() = when (this) {
    AlbumSortBy.ALBUM_SORT_DESC.name -> AlbumSortBy.ALBUM_SORT_DESC
    else -> AlbumSortBy.ALBUM_SORT_ASC
//    else -> throw Exception("AlbumSortBy: No field found correspond to this string")
}