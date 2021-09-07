package com.example.swiperefreshcolumn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class TopIndicator(
    private val _isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    private val composable: @Composable (isRefreshing: Boolean) -> Unit
) {

    var isRefreshing: Boolean
        get() = _isRefreshing.value
        set(value) {
            _isRefreshing.value = value
        }

    @Composable
    internal fun DoCompose(config: Modifier.() -> Modifier) = Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().config()
    ) {
        composable(isRefreshing)
    }
}

internal fun getDefaultTopIndicator(): TopIndicator = TopIndicator { isRefreshing ->
    Box(modifier = if (isRefreshing) Modifier.refreshing() else Modifier.default())
}

private fun Modifier.default() =
    this@default
        .background(Color.Green)
        .fillMaxWidth()
        .height(32.dp)

private fun Modifier.refreshing() =
    this@refreshing
        .background(Color.Red)
        .fillMaxWidth()
        .height(32.dp)