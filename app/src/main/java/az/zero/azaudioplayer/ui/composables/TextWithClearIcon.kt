package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

@Composable
fun TextWithClearIcon(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onTextValueChanged: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current
        var isHintDisplayed by remember { mutableStateOf(hint != "") }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                maxLines = 1,
                singleLine = true,
                cursorBrush = SolidColor(SelectedColor),
                onValueChange = {
                    if (text != it) {
                        onTextValueChanged(it)
                    }
                },
                textStyle = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.onPrimary),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged {
                        isHintDisplayed = !it.isFocused && text.isEmpty()
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ), keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                })
            )

            if (!isHintDisplayed && text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onClearClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = stringResource(id = R.string.clear)
                    )
                }
            }
        }

        if (isHintDisplayed) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                text = hint,
                style = MaterialTheme.typography.h2.copy(
                    color = SecondaryTextColor
                )
            )
        }
    }
}