package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation

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
fun LocalImage(
    localImageUrl: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color = Color.White,
    addBorder: Boolean = true
) {
    Image(
        painter = painterResource(id = localImageUrl),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .size(width = 48.dp, height = 48.dp)
            .then(
                if (addBorder) Modifier.colorFullBorder(cornerShape)
                else Modifier
            )
            .clip(cornerShape)
            .background(imageBackgroundColor)
            .padding(8.dp),
        alignment = Alignment.Center,
    )
}

@Composable
fun LocalImageIcon(
    localImageUrl: ImageVector,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    iconTint :Color= MaterialTheme.colors.onPrimary,
    imageBackgroundColor: Color = Color.White,
    addBorder: Boolean = true
) {
//    Image(
//        imageVector = localImageUrl,
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

    Icon(
        imageVector = localImageUrl,
        contentDescription = null,
        tint = iconTint,
        modifier = modifier
            .size(width = 48.dp, height = 48.dp)
            .then(
                if (addBorder) Modifier.colorFullBorder(cornerShape)
                else Modifier
            )
            .clip(cornerShape)
            .background(imageBackgroundColor)
            .padding(8.dp)
    )
}

@Composable
fun CustomImage(
    image: String,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    imageBackgroundColor: Color = Color.White,
    contentScale: ContentScale = ContentScale.FillBounds,
    addBorder: Boolean = true
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
            .background(imageBackgroundColor)
            .then(
                if (painter.state !is AsyncImagePainter.State.Success)
                    modifier.then(Modifier.padding(8.dp))
                else Modifier
            ),
        alignment = Alignment.Center,
    )
}

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


@Composable
fun TopWithBottomText(
    modifier: Modifier = Modifier,
    topTextName: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextName: String,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = topTextName,
            color = topTextColor,
            style = topTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bottomTextName,
            color = bottomTextColor,
            style = bottomTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@Composable
fun BasicAudioItem(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    localImageUrl: Int? = null,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    onItemClick: () -> Unit,
    topText: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomText: String,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
    imageBackgroundColor: Color = Color.White,
    imageModifier: Modifier = Modifier,
    addBorder: Boolean = true,
    iconVector: ImageVector,
    iconText: String,
    iconColor: Color
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick { onItemClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            imageUrl != null -> CustomImage(
                modifier = imageModifier,
                image = imageUrl,
                cornerShape = cornerShape,
                imageBackgroundColor = imageBackgroundColor,
                addBorder = addBorder
            )
            localImageUrl != null -> LocalImage(
                modifier = imageModifier,
                localImageUrl = localImageUrl,
                imageBackgroundColor = imageBackgroundColor,
                addBorder = addBorder
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(0.6f),
            topTextName = topText,
            bottomTextName = bottomText,
            topTextColor = topTextColor,
            bottomTextColor = bottomTextColor,
            topTextStyle = topTextStyle,
            bottomTextStyle = bottomTextStyle
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            modifier = Modifier
                .mirror()
                .weight(0.1f), onClick = {}) {
            Icon(
                iconVector,
                iconText,
                tint = iconColor
            )
        }
    }
}