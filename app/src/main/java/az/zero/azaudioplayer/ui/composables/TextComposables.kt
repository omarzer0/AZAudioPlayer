package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

@Composable
fun TopWithBottomText(
    modifier: Modifier = Modifier,
    topTextModifier: Modifier = Modifier,
    bottomTextModifier: Modifier = Modifier,
    topTextString: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextString: String,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
    topTextAlign: TextAlign = TextAlign.Start,
    bottomTextAlign: TextAlign = TextAlign.Start,
    verticalSpacingBetweenTexts: Dp = 8.dp,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = topTextModifier.fillMaxWidth(),
            text = topTextString,
            color = topTextColor,
            style = topTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = topTextAlign
        )
        if (bottomTextString.isNotEmpty()) {
            Spacer(modifier = Modifier.height(verticalSpacingBetweenTexts))
            Text(
                modifier = bottomTextModifier.fillMaxWidth(),
                text = bottomTextString,
                color = bottomTextColor,
                style = bottomTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = bottomTextAlign
            )
        }
    }
}


@Composable
fun TopWithBottomTextWithAnnotatedText(
    modifier: Modifier = Modifier,
    topTextString: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextStrings: List<String>,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
    annotatedTextQuery: String = "",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = getAnnotatedText(annotatedTextQuery.trim(), topTextString),
            color = topTextColor,
            style = topTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            bottomTextStrings.forEachIndexed { index, text ->
                val addDelimiter = index != bottomTextStrings.size - 1
                val splitText = remember(annotatedTextQuery) {
                    getAnnotatedText(
                        annotatedTextQuery.trim(),
                        text + if (addDelimiter) " - " else ""
                    )
                }
                Text(
                    text = splitText,
                    color = bottomTextColor,
                    style = bottomTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

    }
}

fun getAnnotatedText(annotatedTextQuery: String, textToUse: String): AnnotatedString {
    val textStartIndex = textToUse.indexOf(annotatedTextQuery, 0, true)
    val textEndIndex = textStartIndex + annotatedTextQuery.length
    val builder = AnnotatedString.Builder(textToUse)
    return if (annotatedTextQuery.isEmpty()) builder.toAnnotatedString()
    else buildAnnotatedString {
        append(textToUse)
        addStyle(style = SpanStyle(SelectedColor), textStartIndex, textEndIndex)
    }
}
