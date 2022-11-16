package az.zero.base.utils

sealed class AudioActions {
    object Play : AudioActions()
    object Pause : AudioActions()
    data class Toggle(val audioDataId: String) : AudioActions()
}