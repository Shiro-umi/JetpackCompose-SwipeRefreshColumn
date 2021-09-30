package com.shiroumi.swiperefreshcolumn

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

/**
 * SwipeRefreshColumn with 'swipeDownRefresh' and 'loadMore' abilities.
 * ps: Paging for compose is still in alpha version, the 'loadMore' impl may be replaced by Paging
 *     when Paging for compose is released.
 *
 * @param modifier 'Modifier' for root layout
 * @param onRefresh Callback for swipe down refresh
 * @param onLoadMore Callback for loadMore
 * @param lazyListState Needed by inner LazyColumn, make this 'lazyListState' as a param gives user another chance for callback.
 * @param swipeRefreshState Core state to manage state of 'Indicators' and 'NestedScroll'
 * @param background Background for root layout
 * @param customTopIndicator TopIndicator for 'SwipeRefreshColumn'
 * @param customBottomIndicator BottomIndicator for 'SwipeRefreshColumn'
 * @param content Children for 'SwipeRefreshColumn', called in 'LazyListScope', just use it like 'LazyColumn'
 *
 * @see TopIndicator
 * @see BottomIndicator
 */
@ExperimentalAnimationApi
@Composable
fun SwipeRefreshColumn(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    swipeRefreshState: SwipeRefreshState,
    background: Color = Color.LightGray,
    customTopIndicator: TopIndicator? = null,
    customBottomIndicator: BottomIndicator? = null,
    content: LazyListScope.() -> Unit
) {

    val topIndicator = remember { customTopIndicator ?: getDefaultTopIndicator() }
    val bottomIndicator = remember { customBottomIndicator }

    val connection = remember {
        NestedScrollConnectionImpl(
            swipeRefreshState = swipeRefreshState,
            lazyListState = lazyListState,
            topIndicator = topIndicator,
            bottomIndicator = customBottomIndicator,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore
        )
    }

    LaunchedEffect(swipeRefreshState.isRefreshing) {
        if (!swipeRefreshState.isRefreshing) {
            swipeRefreshState.resetRefresh()
            topIndicator.isRefreshing = false
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(connection)
            .background(background)
    ) {
        LazyColumn(
            modifier = Modifier
                .offset { swipeRefreshState.swipeOffset.round() }
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 16.dp),
            state = lazyListState
        ) {

            content()

            bottomIndicator?.run {
                item {
                    DoCompose()
                }
            }
        }

        topIndicator.DoCompose {
            offset { (swipeRefreshState.swipeOffset + swipeRefreshState.triggerOffset).round() }
        }
    }
}

internal suspend fun SwipeRefreshState.resetRefresh() = animate(
    initialValue = swipeOffset.y,
    targetValue = Offset.Zero.y,
) { value, _ ->
    updateBothOffset(Offset(0f, value))
}

/**
 * Initialize a SwipeRefreshState
 * Must be provided for SwipeRefreshColumn
 * @param triggerOffset px offset for refresh trigger
 */
@Composable
fun rememberSwipeRefreshState(triggerOffset: Offset) =
    remember { SwipeRefreshState(triggerOffset) }
