package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

    @Composable
    fun <T> CustomDropdown(
        modifier: Modifier = Modifier,
        isDropDownExpanded: Boolean,
        onDismissDropDown: () -> Unit,
        dropDownItems: List<Pair<String, T>>,
        onActionClick: (actions: T) -> Unit,
    ) {
        DropdownMenu(
            modifier = modifier,
            expanded = isDropDownExpanded,
            onDismissRequest = onDismissDropDown
        ) {
            dropDownItems.forEach {
                DropdownMenuItem(onClick = {
                    onActionClick(it.second)
                }) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.first,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }