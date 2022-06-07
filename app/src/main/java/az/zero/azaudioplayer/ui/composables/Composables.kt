package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun ItemsHeader(
    text: String,
    bottomDividerVisible: Boolean = false,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 8.dp)
    ) {
        Row(
            modifier = Modifier
        ) {
            if (content != null) {
                content()
                Spacer(modifier = Modifier.width(160.dp))
            }
            Text(
                text = text, color = SecondaryTextColor,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        if (bottomDividerVisible) {
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun CustomImage(
    image: String,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color = Color.White
) {
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
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .size(width = 48.dp, height = 48.dp)
            .border(
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
            .clip(cornerShape)
            .background(imageBackgroundColor),
        alignment = Alignment.Center,
    )
}

@Composable
fun TopWithBottomText(
    modifier: Modifier = Modifier,
    topTextName: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextName: String,
    bottomTextColor: Color = SecondaryTextColor
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = topTextName,
            color = topTextColor,
            style = MaterialTheme.typography.h2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bottomTextName,
            color = bottomTextColor,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
