package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

@Composable
fun CustomEditText(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    maxLines: Int = 1,
    textColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black,
    singleLine: Boolean = true,
    onTextChanged: (String) -> Unit = {},
) {

    Column(modifier = modifier) {

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.create_new_playlist),
            style = MaterialTheme.typography.h2)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChanged(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = SecondaryTextColor,
                unfocusedBorderColor = SecondaryTextColor,
                focusedLabelColor = SecondaryTextColor,
                unfocusedLabelColor = SecondaryTextColor,
                cursorColor = SelectedColor
            ),
            label = { Text(text = hint) },
            maxLines = maxLines,
            singleLine = singleLine,
            textStyle = TextStyle(color = textColor),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
