package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconWithMenu(
    modifier: Modifier = Modifier,
    iconVector: ImageVector,
    iconText: String,
    iconColor: Color,
    items: List<DropDownItemWithAction>,
    onIconClick: (MenuActionType) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
        IconButton(
            modifier = modifier.mirror(),
            onClick = {
                expanded = true
            }) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {

                items.forEach {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                        }) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableSafeClick {
                                    onIconClick(it.menuActionType)
                                    expanded = false
                                },
                            text = stringResource(id = it.stringID), textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Icon(
                iconVector,
                iconText,
                tint = iconColor,
            )
        }
    }
}

data class DropDownItemWithAction(
    val stringID: Int,
    val menuActionType: MenuActionType,
)

interface MenuActionType

