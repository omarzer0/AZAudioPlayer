package az.zero.azaudioplayer.ui.common_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.IndicatorHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun TextWithIconTab(
    listOfPairOfTabNamesWithIcons: List<Pair<String, ImageVector>>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    isBottomNav: Boolean = false,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabSelectorColor: Color = Color.Black,
    tabSelectorHeight: Dp = IndicatorHeight,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean = false,
    textContent: @Composable ((text: String) -> Unit)? = null,
    iconContent: @Composable ((icon: ImageVector) -> Unit)? = null,
    indicatorContent: @Composable (() -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    BottomNavOrTabLayout(
        isBottomNav = isBottomNav,
        listOfPairOfTabNamesWithIcons = listOfPairOfTabNamesWithIcons,
        modifier = modifier,
        tabHostModifier = tabHostModifier,
        tabModifier = tabModifier,
        selectedContentColor = selectedContentColor,
        unSelectedContentColor = unSelectedContentColor,
        tabSelectorColor = tabSelectorColor,
        tabSelectorHeight = tabSelectorHeight,
        tabHostBackgroundColor = tabHostBackgroundColor,
        animateScrollToPage = animateScrollToPage,
        textContent = textContent,
        iconContent = iconContent,
        content = content,
        indicatorContent = indicatorContent
    )
}

@ExperimentalPagerApi
@Composable
fun IconTab(
    listOfIcons: List<ImageVector>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    isBottomNav: Boolean = false,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabSelectorColor: Color = Color.Black,
    tabSelectorHeight: Dp = IndicatorHeight,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean = false,
    iconContent: @Composable ((icon: ImageVector) -> Unit)? = null,
    indicatorContent: @Composable (() -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    val listOfPairOfTabNamesWithIcons: List<Pair<String?, ImageVector?>> = listOfIcons.map {
        Pair(null, it)
    }

    BottomNavOrTabLayout(
        isBottomNav = isBottomNav,
        listOfPairOfTabNamesWithIcons = listOfPairOfTabNamesWithIcons,
        modifier = modifier,
        tabHostModifier = tabHostModifier,
        tabModifier = tabModifier,
        selectedContentColor = selectedContentColor,
        unSelectedContentColor = unSelectedContentColor,
        tabSelectorColor = tabSelectorColor,
        tabSelectorHeight = tabSelectorHeight,
        tabHostBackgroundColor = tabHostBackgroundColor,
        animateScrollToPage = animateScrollToPage,
        iconContent = iconContent,
        content = content,
        indicatorContent = indicatorContent
    )
}

@ExperimentalPagerApi
@Composable
fun TextTab(
    listOfTabNames: List<String>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    isBottomNav: Boolean = false,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabSelectorColor: Color = Color.Black,
    tabSelectorHeight: Dp = IndicatorHeight,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean = false,
    textContent: @Composable ((text: String) -> Unit)? = null,
    iconContent: @Composable ((icon: ImageVector) -> Unit)? = null,
    indicatorContent: @Composable (() -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    val listOfPairOfTabNamesWithIcons: List<Pair<String?, ImageVector?>> = listOfTabNames.map {
        Pair(it, null)
    }

    BottomNavOrTabLayout(
        isBottomNav = isBottomNav,
        listOfPairOfTabNamesWithIcons = listOfPairOfTabNamesWithIcons,
        modifier = modifier,
        tabHostModifier = tabHostModifier,
        tabModifier = tabModifier,
        selectedContentColor = selectedContentColor,
        unSelectedContentColor = unSelectedContentColor,
        tabSelectorColor = tabSelectorColor,
        tabSelectorHeight = tabSelectorHeight,
        tabHostBackgroundColor = tabHostBackgroundColor,
        animateScrollToPage = animateScrollToPage,
        textContent = textContent,
        iconContent = iconContent,
        content = content,
        indicatorContent = indicatorContent
    )
}


@ExperimentalPagerApi
@Composable
private fun AZTabPager(
    listOfPairOfTabNamesWithIcons: List<Pair<String?, ImageVector?>>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabSelectorColor: Color,
    tabSelectorHeight: Dp,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean,
    textContent: @Composable ((text: String) -> Unit)? = null,
    iconContent: @Composable ((icon: ImageVector) -> Unit)? = null,
    indicatorContent: @Composable (() -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = tabHostModifier,
            backgroundColor = tabHostBackgroundColor,
            indicator = { tabPositions ->
                if (indicatorContent != null) {
                    Box(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    ) {
                        indicatorContent()
                    }
                } else {
                    TabRowDefaults.Indicator(
                        Modifier
                            .pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = tabSelectorColor,
                        height = tabSelectorHeight,
                    )
                }
            }
        ) {
            listOfPairOfTabNamesWithIcons.forEachIndexed { index, items ->
                Tab(
                    selected = tabIndex == index,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unSelectedContentColor,
                    modifier = tabModifier,
                    onClick = {
                        coroutineScope.launch {
                            if (animateScrollToPage) pagerState.animateScrollToPage(index)
                            else pagerState.scrollToPage(index)
                        }
                    },
                    text = items.first?.let {
                        {
                            if (textContent == null) Text(text = it)
                            else textContent.invoke(it)
                        }
                    },
                    icon = items.second?.let {
                        {
                            if (iconContent == null) Icon(
                                imageVector = it,
                                contentDescription = null
                            )
                            else iconContent.invoke(it)
                        }
                    },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            count = listOfPairOfTabNamesWithIcons.size
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

@ExperimentalPagerApi
@Composable
private fun AZBottomTabPager(
    listOfPairOfTabNamesWithIcons: List<Pair<String?, ImageVector?>>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean,
    textContent: @Composable ((text: String) -> Unit)? = null,
    iconContent: @Composable ((icon: ImageVector) -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            count = listOfPairOfTabNamesWithIcons.size,
        ) { index ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = { content(index) }
            )
        }

        TabRow(
            selectedTabIndex = tabIndex,
            modifier = tabHostModifier,
            backgroundColor = tabHostBackgroundColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 0.dp,
                    color = Color.Transparent,
                )
            }
        ) {
            listOfPairOfTabNamesWithIcons.forEachIndexed { index, items ->
                Tab(
                    selected = tabIndex == index,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unSelectedContentColor,
                    modifier = tabModifier,
                    onClick = {
                        coroutineScope.launch {
                            if (animateScrollToPage) pagerState.animateScrollToPage(index)
                            else pagerState.scrollToPage(index)
                        }
                    },
                    text = items.first?.let {
                        {
                            if (textContent == null) Text(text = it)
                            else textContent.invoke(it)
                        }
                    },
                    icon = items.second?.let {
                        {
                            if (iconContent == null) Icon(
                                imageVector = it,
                                contentDescription = null
                            )
                            else iconContent.invoke(it)
                        }
                    },
                )
            }
        }
    }
}


@ExperimentalPagerApi
@Composable
private fun BottomNavOrTabLayout(
    isBottomNav: Boolean,
    listOfPairOfTabNamesWithIcons: List<Pair<String?, ImageVector?>>,
    modifier: Modifier = Modifier,
    tabHostModifier: Modifier = Modifier,
    tabModifier: Modifier = Modifier,
    selectedContentColor: Color = Color.White,
    unSelectedContentColor: Color = Color.LightGray,
    tabHostBackgroundColor: Color = MaterialTheme.colors.primarySurface,
    animateScrollToPage: Boolean = true,
    textContent: @Composable() ((text: String) -> Unit)? = null,
    iconContent: @Composable() ((icon: ImageVector) -> Unit)? = null,
    content: @Composable (index: Int) -> Unit,
    tabSelectorColor: Color,
    tabSelectorHeight: Dp = IndicatorHeight,
    indicatorContent: @Composable (() -> Unit)? = null,
) {
    if (isBottomNav) {
        AZBottomTabPager(
            listOfPairOfTabNamesWithIcons = listOfPairOfTabNamesWithIcons,
            modifier = modifier,
            tabHostModifier = tabHostModifier,
            tabModifier = tabModifier,
            selectedContentColor = selectedContentColor,
            unSelectedContentColor = unSelectedContentColor,
            tabHostBackgroundColor = tabHostBackgroundColor,
            animateScrollToPage = animateScrollToPage,
            textContent = textContent,
            iconContent = iconContent,
            content = content
        )
    } else {
        AZTabPager(
            listOfPairOfTabNamesWithIcons = listOfPairOfTabNamesWithIcons,
            modifier = modifier,
            tabHostModifier = tabHostModifier,
            tabModifier = tabModifier,
            selectedContentColor = selectedContentColor,
            unSelectedContentColor = unSelectedContentColor,
            tabHostBackgroundColor = tabHostBackgroundColor,
            tabSelectorColor = tabSelectorColor,
            tabSelectorHeight = tabSelectorHeight,
            animateScrollToPage = animateScrollToPage,
            textContent = textContent,
            iconContent = iconContent,
            content = content,
            indicatorContent = indicatorContent
        )
    }
}