package az.zero.azaudioplayer.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.BuildConfig
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.BasicHeaderWithBackBtn
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.composables.clickableSafeClick
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            SettingScreen(
                onBackPressed = {
                    findNavController().navigateUp()
                }
            )
        }
    }
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BasicHeaderWithBackBtn(
            text = stringResource(id = R.string.settings),
            onBackPressed = onBackPressed
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.about_app),
            style = MaterialTheme.typography.body1.copy(
                color = SecondaryTextColor
            ),
            textAlign = TextAlign.Start
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            item {
                TopWithBottomText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    topTextString = stringResource(id = R.string.version),
                    bottomTextString = BuildConfig.VERSION_NAME,
                    verticalSpacingBetweenTexts = 0.dp
                )
            }

            item {
                SettingsItem(
                    text = stringResource(id = R.string.privacy_policies),
                    onClick = {}
                )
            }

            item {
                SettingsItem(
                    text = stringResource(id = R.string.open_source_licence),
                    onClick = {}
                )
            }

        }

        Text(
            text = stringResource(R.string.copy_rights),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onBackground
            )
        )
    }
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick {
                onClick()
            }
            .padding(vertical = 8.dp, horizontal = 16.dp)

    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.onBackground
            )
        )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            stringResource(id = R.string.more),
            tint = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
            modifier = Modifier.mirror()
        )
    }
}