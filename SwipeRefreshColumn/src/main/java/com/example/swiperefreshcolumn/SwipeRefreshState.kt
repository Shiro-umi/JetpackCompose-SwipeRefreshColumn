package com.example.swiperefreshcolumn

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

class SwipeRefreshState(
    val triggerOffset: Offset,
    private val _isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    private val _isLoadingMore: MutableState<Boolean> = mutableStateOf(false),
    private val _swipeOffset: MutableState<Offset> = mutableStateOf(Offset(0f, 0f)),
    private val _totalScrolledOffset: MutableState<Offset> = mutableStateOf(Offset(0f, 0f))
) {
    var isRefreshing: Boolean
        get() = _isRefreshing.value
        set(value) {
            _isRefreshing.value = value
        }

    var isLoadingMore: Boolean
        get() = _isLoadingMore.value
        set(value) {
            _isLoadingMore.value = value
        }

    var swipeOffset: Offset
        get() = _swipeOffset.value
        set(value) {
            _swipeOffset.value = value
        }

    var totalOffset: Offset
        get() = _totalScrolledOffset.value
        set(value) {
            _totalScrolledOffset.value = value
        }

    fun updateBothOffset(value: Offset) {
        totalOffset = value
        swipeOffset = value
    }

    fun loadMoreDone() {
        isLoadingMore = false
    }
}