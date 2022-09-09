package az.zero.azaudioplayer.ui.utils.ui_extensions

import android.util.LayoutDirection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.core.text.layoutDirection
import java.util.*

@Stable
fun Modifier.mirror(): Modifier {
    return if (Locale.getDefault().layoutDirection == LayoutDirection.RTL)
        this.scale(scaleX = -1f, scaleY = 1f)
    else
        this
}

@Stable
fun Color.isColorDark(): Boolean {
    val darkness: Double =
        1 - (0.299 * this.red + 0.587 * this.green + 0.114 * this.blue / 255)
    return darkness >= 0.5
}

@Stable
fun Modifier.colorFullBorder(cornerShape: Shape = RoundedCornerShape(12.dp)) = this.border(
    border = BorderStroke(
        width = 0.5.dp, brush = Brush.linearGradient(
            colors = listOf(
                Color.Red,
                Color.Blue
            )
        )
    ),
    shape = cornerShape
)


fun getColorFromHex(colorString: String): Color {
    val colorToConvert = if (!colorString.startsWith('#')) "#$colorString"
    else colorString
    return Color(android.graphics.Color.parseColor(colorToConvert))
}