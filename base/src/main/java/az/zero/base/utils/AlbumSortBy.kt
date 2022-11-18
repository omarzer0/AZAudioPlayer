package az.zero.base.utils

enum class AlbumSortBy {
    ASCENDING,
    DESCENDING
}

fun String.toAlbumSortBy() = when (this) {
    AlbumSortBy.DESCENDING.name -> AlbumSortBy.DESCENDING
    else -> AlbumSortBy.ASCENDING
//    else -> throw Exception("AlbumSortBy: No field found correspond to this string")
}