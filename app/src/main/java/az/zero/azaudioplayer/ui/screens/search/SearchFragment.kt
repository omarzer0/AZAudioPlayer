package az.zero.azaudioplayer.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import az.zero.azaudioplayer.utils.AudioActions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return setFragmentContent {
            var selectedId = -1

            val allAudios = viewModel.allAudio.observeAsState().value ?: emptyList()

            var text by rememberSaveable {
                mutableStateOf("")
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    HeaderSearchItem(
                        text,
                        navController = findNavController(),
                        onClearClick = { text = "" },
                        onSearch = { query -> text = query;viewModel.searchAudios(query) })
                }

                item {
                    val headerText = "${allAudios.size} ${stringResource(id = R.string.of_audios)}"
                    ItemsHeader(text = headerText)
                }

                itemsIndexed(allAudios) { index, audio ->
                    AudioItem(
                        audio,
                        isSelected = selectedId == index,
                        annotatedTextQuery = text,
                        onClick = {
                            selectedId = index
                            viewModel.audioAction(AudioActions.Toggle(audioDataId = audio.data))
                        },
                        onIconClick = {
                            // TODO on audio more icon click impl
                        })
                }
            }
        }
    }
}


@Composable
fun HeaderSearchItem(
    text: String,
    navController: NavController,
    onSearch: (String) -> Unit = {},
    onClearClick: () -> Unit,
) {
    Box {
        SearchBar(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            hint = stringResource(id = R.string.search),
            onSearch = { query ->
                onSearch(query)
            }, onBackBtnClick = {
                navController.navigateUp()
            }, onClearClick = {
                onClearClick()
            })
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onSearch: (String) -> Unit = {},
    onBackBtnClick: () -> Unit = {},
    onClearClick: () -> Unit,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colors.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = CenterVertically
        ) {

            IconButton(onClick = { onBackBtnClick() }) {
                Icon(
                    modifier = Modifier.mirror(),
                    imageVector = Icons.Filled.ArrowBack,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
            ) {
                var isHintDisplayed by remember {
                    mutableStateOf(hint != "")
                }

                TextWithClearIcon(
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
                .height(1.dp) ,
            color = MaterialTheme.colors.background
        )
    }
}

@Composable
fun TextWithClearIcon(
    text: String,
    isClearIconVisible: Boolean = true,
    onShouldShowHint: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically
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
            })
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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