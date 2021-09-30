package com.shiroumi.swiperefreshcolumn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal const val DEFAULT_BOTTOM_INDICATOR_KAY = "default_bottom_indicator_key"

class BottomIndicator(
    private val _isLoading: MutableState<Boolean> = mutableStateOf(false),
    var hasMore: Boolean = true,
    val indicatorKey: String = DEFAULT_BOTTOM_INDICATOR_KAY,
    private val composable: @Composable (isLoading: Boolean, hasMore: Boolean) -> Unit
) {

    var isLoading: Boolean
        get() = _isLoading.value
        set(value) {
            _isLoading.value = value
        }

    @Composable
    internal fun DoCompose() = composable(isLoading, hasMore)
}

@ExperimentalAnimationApi
@Composable
fun DefaultBottomIndicator(
    isLoading: Boolean,
    hasMore: Boolean
) {
    var prev by remember { mutableStateOf(Offset(0f, 0f)) }
    var prevOffsetAngle by remember { mutableStateOf(0f) }
    var offsetAngle by remember { mutableStateOf(0f) }
    var startAngle by remember { mutableStateOf(prev.x) }
    var endAngle by remember { mutableStateOf(prev.y) }

    LaunchedEffect(hasMore) {
        if (!hasMore) {
            prevOffsetAngle = offsetAngle
            return@LaunchedEffect
        }
        animate(
            initialValue = prevOffsetAngle,
            targetValue = prevOffsetAngle + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(7000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ) { value, _ ->
            offsetAngle = value
        }
    }

    LaunchedEffect(hasMore, endAngle) {
        if (!hasMore) return@LaunchedEffect
        if ((endAngle - startAngle).toInt() == 270) {
            animate(
                initialValue = startAngle,
                targetValue = endAngle - 10f,
                animationSpec = tween(durationMillis = 1000)
            ) { value, _ ->
                startAngle = value
            }
        }
    }


    LaunchedEffect(hasMore, startAngle) {
        if (!hasMore) if (!hasMore) {
            prev = Offset(startAngle, endAngle)
            return@LaunchedEffect
        }
        if ((endAngle - startAngle).toInt() == 10
            || endAngle.toInt() == prev.y.toInt()
        ) {
            animate(
                initialValue = endAngle,
                targetValue = startAngle + 270f,
                animationSpec = tween(durationMillis = 1000),
            ) { value, _ ->
                endAngle = value
            }
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(4.dp)
    ) {

        AnimatedVisibility(
            visible = !hasMore,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "End",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        AnimatedVisibility(
            visible = hasMore,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .align(Alignment.Center)
                    .rotate(offsetAngle)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawArc(
                        color = Color.Gray,
                        startAngle = startAngle,
                        sweepAngle = endAngle - startAngle,
                        useCenter = false,
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
