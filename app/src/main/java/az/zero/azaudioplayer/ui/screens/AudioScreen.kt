package az.zero.azaudioplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.theme.SelectedColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun AudioScreen() {
    var selected by remember { mutableStateOf(0) }

    LazyColumn {
        val listSize = 20
        items(listSize) { index ->
            AudioItem(isSelected = selected == index) {
                selected = index
            }
        }
    }
}

@Composable
fun AudioItem(isSelected: Boolean, onClick: () -> Unit) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Name", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Description", color = textColor, fontSize = 14.sp)
        }
        IconButton(onClick = {}) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = textColor
            )
        }
    }
}