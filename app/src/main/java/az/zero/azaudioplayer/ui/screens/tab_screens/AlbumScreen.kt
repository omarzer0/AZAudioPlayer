package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.data.models.Album
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale


@Composable
fun AlbumScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val albumList by remember { viewModel.allAlbums }.observeAsState()
    if (albumList.isNullOrEmpty()) return
    val listSize = albumList?.size!!

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(listSize) { index ->
            AlbumItem(album = albumList!![index]) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                        albumList!![index].audioList.toTypedArray()
                    )
                )
            }
        }
    }


}

@Composable
fun AlbumItem(album: Album, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
    ) {

        val image = remember {
            if (album.audioList.isNullOrEmpty()) ""
            else album.audioList[0].cover
        }

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .scale(Scale.FILL)
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .build()
        )

        val artistName = remember { album.audioList[0].artist }
        val albumName = remember { album.album.name }
        val textColor = MaterialTheme.colors.onPrimary
        val imageBackgroundColor = Color.White
        val roundedCornerShape = RoundedCornerShape(12.dp)

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
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
                    shape = roundedCornerShape
                )
                .clip(roundedCornerShape)
                .background(imageBackgroundColor),
            alignment = Alignment.Center,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = albumName,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = artistName, color = textColor, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .mirror(), onClick = {}) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                stringResource(id = R.string.more),
                tint = textColor
            )
        }
    }
}