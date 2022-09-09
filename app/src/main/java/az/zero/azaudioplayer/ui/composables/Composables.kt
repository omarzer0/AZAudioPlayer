package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.colorFullBorder
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror

@Composable
fun ItemsHeader(
    text: String,
    bottomDividerVisible: Boolean = false,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 8.dp)
    ) {
        Row(
            modifier = Modifier
        ) {
            if (content != null) {
                content()
                Spacer(modifier = Modifier.width(160.dp))
            }
            Text(
                text = text, color = SecondaryTextColor,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        if (bottomDividerVisible) {
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
        }
    }
}


@Composable
fun LocalImageIcon(
    localImageUrl: ImageVector,
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    iconTint: Color = MaterialTheme.colors.onPrimary,
    imageBackgroundColor: Color = Color.White,
    addBorder: Boolean = true,
    innerImagePadding: Dp = 8.dp
) {
    Icon(
        imageVector = localImageUrl,
        contentDescription = null,
        tint = iconTint,
        modifier = modifier
            .size(width = 48.dp, height = 48.dp)
            .then(
                if (addBorder) Modifier.colorFullBorder(cornerShape)
                else Modifier
            )
            .clip(cornerShape)
            .background(imageBackgroundColor)
            .padding(innerImagePadding)
    )
}


@Composable
fun TopWithBottomText(
    modifier: Modifier = Modifier,
    topTextName: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextName: String,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = topTextName,
            color = topTextColor,
            style = topTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (bottomTextName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bottomTextName,
                color = bottomTextColor,
                style = bottomTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
fun TopWithBottomTextWithAnnotatedText(
    modifier: Modifier = Modifier,
    topTextName: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomTextNames: List<String>,
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
    annotatedTextQuery: String = ""
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = getAnnotatedText(annotatedTextQuery, topTextName),
            color = topTextColor,
            style = topTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            bottomTextNames.forEachIndexed { index, text ->
                val addDelimiter = index != bottomTextNames.size - 1
                val splitText = remember(annotatedTextQuery) {
                    getAnnotatedText(
                        annotatedTextQuery,
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

@Composable
fun BasicAudioItem(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    localImageUrl: Int? = null,
    cornerShape: Shape = RoundedCornerShape(12.dp),
    onItemClick: () -> Unit,
    topText: String,
    topTextColor: Color = MaterialTheme.colors.onPrimary,
    bottomText: String = "",
    bottomTexts: List<String> = emptyList(),
    bottomTextColor: Color = SecondaryTextColor,
    topTextStyle: TextStyle = MaterialTheme.typography.h2,
    bottomTextStyle: TextStyle = MaterialTheme.typography.body1,
    imageBackgroundColor: Color? = null,
    imageModifier: Modifier = Modifier,
    addBorder: Boolean = false,
    annotatedTextQuery: String = "",
    iconVector: ImageVector,
    iconText: String,
    iconColor: Color,
    onTailItemClick: ((MenuActionType) -> Unit)? = null,
    menuItemList: List<DropDownItemWithAction> = emptyList()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick { onItemClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            imageUrl != null -> CustomImage(
                modifier = imageModifier,
                image = imageUrl,
                cornerShape = cornerShape,
                imageBackgroundColor = imageBackgroundColor,
                addBorder = addBorder
            )
            localImageUrl != null -> LocalImage(
                modifier = imageModifier,
                localImageUrl = localImageUrl,
                imageBackgroundColor = imageBackgroundColor,
                addBorder = addBorder
            )
        }

        if (imageUrl != null || localImageUrl != null) Spacer(modifier = Modifier.width(16.dp))

        when {
            bottomTexts.isEmpty() -> {
                TopWithBottomText(
                    modifier = Modifier.weight(1f),
                    topTextName = topText,
                    bottomTextName = bottomText,
                    topTextColor = topTextColor,
                    bottomTextColor = bottomTextColor,
                    topTextStyle = topTextStyle,
                    bottomTextStyle = bottomTextStyle
                )
            }

            else -> {
                TopWithBottomTextWithAnnotatedText(
                    modifier = Modifier.weight(1f),
                    topTextName = topText,
                    bottomTextNames = bottomTexts,
                    topTextColor = topTextColor,
                    bottomTextColor = bottomTextColor,
                    topTextStyle = topTextStyle,
                    bottomTextStyle = bottomTextStyle,
                    annotatedTextQuery = annotatedTextQuery
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        when {
            onTailItemClick != null -> {
                IconWithMenu(
                    iconVector = iconVector,
                    iconText = iconText,
                    iconColor = iconColor,
                    items = menuItemList
                )
            }
            else -> {
                Icon(
                    iconVector,
                    iconText,
                    tint = iconColor,
                    modifier = Modifier.mirror()
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconWithMenu(
    iconVector: ImageVector,
    iconText: String,
    iconColor: Color,
    items: List<DropDownItemWithAction>,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
        IconButton(
            modifier = Modifier.mirror(),
            onClick = { expanded = true }) {
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
                            modifier = Modifier.fillMaxWidth(),
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
    val menuActionType: MenuActionType
)

interface MenuActionType

@Composable
fun CustomEditText(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    maxLines: Int = 1,
    singleLine: Boolean = true,
    onTextChanged: (String) -> Unit = {},
) {

    Column(modifier = modifier) {

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Create a new playlist", style = MaterialTheme.typography.h2)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChanged(it.trim())
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
            textStyle = TextStyle(color = SecondaryTextColor),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
