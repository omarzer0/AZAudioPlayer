package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.mirror

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onSearch: (String) -> Unit = {},
    onBackBtnClick: (() -> Unit)? = null,
    onClearClick: () -> Unit,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colors.primary),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 4.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            onBackBtnClick?.let {
                IconButton(onClick = { it() }) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                var isHintDisplayed by remember {
                    mutableStateOf(hint != "")
                }

                TextWithClearIcon(
                    modifier = Modifier.fillMaxSize(),
                    text = text,
                    isClearIconVisible = !isHintDisplayed,
                    onShouldShowHint = { isHintDisplayed = it },
                    onSearch = { onSearch(it) },
                    onClearClick = { onClearClick() }
                )

                if (isHintDisplayed) {
                    Text(
                        modifier = Modifier,
                        text = hint,
                        style = MaterialTheme.typography.h2.copy(
                            color = SecondaryTextColor
                        ),
                    )
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MaterialTheme.colors.background
        )
    }
}

@Composable
fun TextWithClearIcon(
    modifier: Modifier = Modifier,
    text: String,
    isClearIconVisible: Boolean = true,
    onShouldShowHint: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var shouldRequestFocus by remember { mutableStateOf(true) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.onPrimary
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(8f)
                .onFocusChanged {
                    onShouldShowHint(!it.isFocused && text.isEmpty())
                },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ), keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                shouldRequestFocus = false
            })
        )

        if (shouldRequestFocus) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

        if (isClearIconVisible && text.isNotEmpty()) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { onClearClick() }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = stringResource(id = R.string.clear)
                )
            }
        }
    }
}