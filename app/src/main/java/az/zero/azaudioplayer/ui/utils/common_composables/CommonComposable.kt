package az.zero.azaudioplayer.ui.utils.common_composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import az.zero.azaudioplayer.ui.utils.ui_extensions.isColorDark
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun ChangeStatusBarColor(
    systemUiController: SystemUiController = rememberSystemUiController(),
    statusColor: Color,
    useDarkIcons: Boolean = !statusColor.isColorDark(),
    navigationBarColor: Color? = null,
) {
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusColor,
            darkIcons = useDarkIcons
        )

        if (navigationBarColor != null)
            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = useDarkIcons
            )
    }
}