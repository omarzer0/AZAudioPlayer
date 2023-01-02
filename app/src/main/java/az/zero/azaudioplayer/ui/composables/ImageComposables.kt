package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.ui_extensions.colorFullBorder
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun LocalImage(
    modifier: Modifier = Modifier,
    localImageUrl: Int,
    contentScale: ContentScale = ContentScale.FillBounds,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color? = null,
) {

    Image(
        painter = painterResource(id = localImageUrl),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .clip(cornerShape)
            .background(
                imageBackgroundColor
                    ?: if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
            ),
        alignment = Alignment.Center,
    )
}

@Composable
fun CustomImage(
    modifier: Modifier = Modifier,
    image: String,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val borderColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
    val bgColor = imageBackgroundColor ?: borderColor

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .scale(Scale.FILL)
            .placeholder(R.drawable.ic_music)
            .error(R.drawable.ic_music)
            .build()
    )
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .colorFullBorder(
                brush = SolidColor(borderColor),
                cornerShape = cornerShape,
                borderWidth = 0.5.dp
            )
            .clip(cornerShape)
            .background(bgColor),
        alignment = Alignment.Center,
    )
}