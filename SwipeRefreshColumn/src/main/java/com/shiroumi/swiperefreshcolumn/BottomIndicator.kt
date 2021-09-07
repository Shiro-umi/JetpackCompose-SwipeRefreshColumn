package com.shiroumi.swiperefreshcolumn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

internal const val DEFAULT_BOTTOM_INDICATOR_KAY = "default_bottom_indicator_key"

class BottomIndicator(
    private val _isLoading: MutableState<Boolean> = mutableStateOf(false),
    var hasMore: Boolean = true,
    val indicatorKey: String = DEFAULT_BOTTOM_INDICATOR_KAY,
    private val composable: @Composable (isLoading: Boolean) -> Unit
) {

    var isLoading: Boolean
        get() = _isLoading.value
        set(value) {
            _isLoading.value = value
        }

    @Composable
    internal fun DoCompose() = composable(isLoading)
}
