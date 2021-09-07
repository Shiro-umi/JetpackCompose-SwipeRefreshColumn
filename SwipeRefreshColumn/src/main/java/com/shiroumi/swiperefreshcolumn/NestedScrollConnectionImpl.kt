package com.shiroumi.swiperefreshcolumn

import androidx.compose.animation.core.animate
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class NestedScrollConnectionImpl(
    private val swipeRefreshState: SwipeRefreshState,
    private val lazyListState: LazyListState,
    private val topIndicator: TopIndicator,
    private val bottomIndicator: BottomIndicator?,
    private val onRefresh: () -> Unit,
    private val onLoadMore: () -> Unit
) : NestedScrollConnection {
    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity = with(swipeRefreshState) {
        if (totalOffset <= Offset.Zero) {
            return super.onPostFling(consumed, available)
        }

        if (swipeOffset < -triggerOffset) {
            animateOffsetTo(Offset.Zero)
            return available
        }

        isRefreshing = true
        topIndicator.isRefreshing = true
        onRefresh()
        animateOffsetTo(-triggerOffset)
        return available
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = with(swipeRefreshState) {
        totalOffset += consumed

        if (NestedScrollSource.Drag == source && totalOffset >= Offset.Zero) {
            totalOffset += available / 2f
            swipeOffset += available / 2f
            return available
        }

        if (isLoadingMore) {
            return super.onPostScroll(consumed, available, source)
        }

        if (bottomIndicator?.hasMore == false) {
            bottomIndicator.isLoading = false
            return super.onPostScroll(consumed, available, source)
        }

        // loadMore
        if (lazyListState.lastVisibleItem().key == bottomIndicator?.indicatorKey) {
            isLoadingMore = true
            onLoadMore()
            bottomIndicator.isLoading = true
            return super.onPostScroll(consumed, available, source)
        }

        if (lazyListState.lastVisibleItem().index == lazyListState.totalItemCount() - 1) {
            isLoadingMore = true
            onLoadMore()
        }

        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(
        available: Velocity
    ): Velocity = with(swipeRefreshState) {
        if (totalOffset > Offset.Zero) {
            return available
        }
        return super.onPreFling(available)
    }

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when (source) {
        NestedScrollSource.Drag -> {
            tryInterceptNestedScroll(available, source)
        }
        else -> super.onPreScroll(available, source)
    }

    private fun tryInterceptNestedScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = with(swipeRefreshState) {
        if (available < Offset.Zero && totalOffset > Offset.Zero) {
            val targetOffset = swipeOffset + available / 2f
            updateBothOffset(if (targetOffset > Offset.Zero) targetOffset else Offset.Zero)
            available
        } else {
            super.onPreScroll(available, source)
        }
    }

    private suspend fun animateOffsetTo(targetValue: Offset) = animate(
        initialValue = swipeRefreshState.swipeOffset.y,
        targetValue = targetValue.y
    ) { value, _ ->
        swipeRefreshState.updateBothOffset(Offset(0f, value))
    }
}

private fun LazyListState.lastVisibleItem() = layoutInfo.visibleItemsInfo.last()

private fun LazyListState.totalItemCount() = layoutInfo.totalItemsCount

private operator fun Offset.compareTo(offset: Offset): Int = (this.y - offset.y).toInt()