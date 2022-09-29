package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.colorFullBorder
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation


@Composable
fun LocalImage(
    localImageUrl: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color? = null,
    padding: Dp = 0.dp,
    addBorder: Boolean = false
) {

    MyImage(
        painter = painterResource(id = localImageUrl),
        modifier = modifier,
        cornerShape = cornerShape,
        imageBackgroundColor = imageBackgroundColor,
        contentScale = contentScale,
        addBorder = addBorder
    )
}

@Composable
fun CustomImage(
    image: String,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color? = null,
    contentScale: ContentScale = ContentScale.Crop,
    addBorder: Boolean = false
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .transformations(RoundedCornersTransformation(12.dp.value))
            .scale(Scale.FILL)
            .placeholder(R.drawable.ic_music)
            .error(R.drawable.ic_music)
            .build()
    )

    MyImage(
        painter = painter,
        modifier = modifier,
        cornerShape = cornerShape,
        imageBackgroundColor = imageBackgroundColor,
        contentScale = contentScale,
        addBorder = addBorder
    )
}

@Composable
fun MyImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color? = null,
    contentScale: ContentScale = ContentScale.FillBounds,
    addBorder: Boolean
) {

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .size(width = 48.dp, height = 48.dp)
            .then(
                if (addBorder) Modifier.colorFullBorder(cornerShape)
                else Modifier
            )
            .clip(cornerShape)
            .background(
                imageBackgroundColor
                    ?: if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
            )
            .then(
                if (painter is AsyncImagePainter) {
                    when (painter.state) {
                        !is AsyncImagePainter.State.Success -> Modifier.padding(12.dp)
                        else -> Modifier
                    }
                } else Modifier.padding(8.dp)
            ),
        alignment = Alignment.Center,
    )
}


//@Composable
//fun LocalImage(
//    localImageUrl: Int,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillBounds,
//    cornerShape: Shape = RoundedCornerShape(12.dp),
//    imageBackgroundColor: Color = Color.White,
//    addBorder: Boolean = true
//) {
//    Image(
//        painter = painterResource(id = localImageUrl),
//        contentDescription = null,
//        contentScale = contentScale,
//        modifier = modifier
//            .size(width = 48.dp, height = 48.dp)
//            .then(
//                if (addBorder) Modifier.colorFullBorder(cornerShape)
//                else Modifier
//            )
//            .clip(cornerShape)
//            .background(imageBackgroundColor)
//            .padding(8.dp),
//        alignment = Alignment.Center,
//    )
//}

//    Image(
//        painter = painter,
//        contentDescription = null,
//        contentScale = contentScale,
//        modifier = modifier
//            .size(width = 48.dp, height = 48.dp)
//            .then(
//                if (addBorder) Modifier.colorFullBorder(cornerShape)
//                else Modifier
//            )
//            .clip(cornerShape)
//            .background(imageBackgroundColor)
//            .then(
//                if (painter.state !is AsyncImagePainter.State.Success)
//                    modifier.then(Modifier.padding(8.dp))
//                else Modifier
//            ),
//        alignment = Alignment.Center,
//    )


//GlideImage(
//            imageModel = audio.cover,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .align(CenterHorizontally)
//                .width(300.dp)
//                .height(250.dp)
//                .clip(RoundedCornerShape(12.dp)),
//            loading = {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_music),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .width(300.dp)
//                        .height(250.dp)
//                )
////                dominantColor = MaterialTheme.colors.primary
//            },
//            success = { imageState ->
//                imageState.drawable?.let {
//                    viewModel.calculateDominantColor(it) { color, imageBitmap ->
//                        dominantColor = color
//                        imageOfBitmap = imageBitmap
//                    }
//                }
//                imageOfBitmap?.let {
//                    CustomImage(
//                        bitmap = imageOfBitmap,
//                        image = audio.cover,
//                        modifier = Modifier
//                            .width(300.dp)
//                            .height(250.dp)
//                    )
//                }
//            },
//            failure = {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_music),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .width(300.dp)
//                        .height(250.dp)
//                )
//                dominantColor = MaterialTheme.colors.primary
//            },
//        )