package az.zero.azaudioplayer.utils

fun createTimeLabel(mTime: Float): String {
    val time = mTime.toLong()
    val min = time / 1000 / 60
    val sec = time / 1000 % 60
    var label = "$min:"
    if (sec < 10) label += "0"
    label += sec
    return label
}