package az.zero.azaudioplayer.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

fun createTimeLabel(mTime: Float): String {
    val time = mTime.toLong()
    val min = time / 1000 / 60
    val sec = time / 1000 % 60
    var label = "$min:"
    if (sec < 10) label += "0"
    label += sec
    return label
}

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    mediatorLiveData.addSource(this) {
        mediatorLiveData.value = it
    }
    return mediatorLiveData
}
