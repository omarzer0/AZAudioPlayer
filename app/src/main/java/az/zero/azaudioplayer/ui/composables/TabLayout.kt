package az.zero.azaudioplayer.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.IndicatorHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun TextTab(
    modifier: Modifier = Modifier,
    initialTabPosition: Int = 0,
    tabs: List<String>,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabSelectorColor: Color = Color.Black,
    tabSelectorHeight: Dp = IndicatorHeight,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean = false,
    onTabChange: ((Int) -> Unit)? = null,
    content: @Composable (index: Int) -> Unit,
) {

    val pagerState = rememberPagerState(initialPage = initialTabPosition)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage != 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onTabChange?.invoke(page)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = tabHostModifier,
            divider = {},
            backgroundColor = tabHostBackgroundColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .pagerTabIndicatorOffset(
                            pagerState = pagerState,
                            tabPositions = tabPositions
                        )
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape),
                    color = tabSelectorColor,
                    height = tabSelectorHeight,
                )
            }
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = pagerState.currentPage == index,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unSelectedContentColor,
                    modifier = tabModifier,
                    onClick = {
                        coroutineScope.launch {
                            if (animateScrollToPage) pagerState.animateScrollToPage(index)
                            else pagerState.scrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = item,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            count = tabs.size
        ) { index ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = { content(index) }
            )
        }
    }
}
