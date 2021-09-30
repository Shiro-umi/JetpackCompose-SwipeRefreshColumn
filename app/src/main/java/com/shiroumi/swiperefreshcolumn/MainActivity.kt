package com.shiroumi.swiperefreshcolumn

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handler = Handler(Looper.getMainLooper())

        window.navigationBarColor = Color.parseColor("#cccccc");

        setContent {

            // example dataList
            var dataList by remember { mutableStateOf((1..50).toList()) }

            // call 'rememberSwipeRefreshState' to get a swipeRefreshState
            // should provide a 'triggerOffset' here
            val swipeRefreshState = rememberSwipeRefreshState(
                triggerOffset = with(LocalDensity.current) { Offset(0f, (-32).dp.toPx()) }
            )

            // initialize a bottomIndicator, if needed
            val bottomIndicator = BottomIndicator { isLoading, hasMore ->
                DefaultBottomIndicator(isLoading = isLoading, hasMore = hasMore)
            }

            // onRefresh callback
            val onRefresh: () -> Unit = remember {
                {
                    handler.postDelayed({
                        val start = (1..50).random()
                        dataList = (start..start + 50).toList()

                        // should set 'swipeRefreshState.isRefreshing' to false
                        // when the refreshing is done
                        swipeRefreshState.isRefreshing = false

                        bottomIndicator.hasMore = System.currentTimeMillis() % 2 == 0L
                    }, 3000L)
                }
            }

            // onLoadMore callback
            val onLoadMore: () -> Unit = remember {
                {
                    handler.postDelayed({
                        val start = dataList.last() + 1
                        dataList = dataList + (start..start + 49).toList()

                        // should set 'swipeRefreshState.isRefreshing' to false
                        // when the refreshing is done
                        swipeRefreshState.isLoadingMore = false

                        // if there are no more data, set the 'bottomIndicator.hasMore' to false
                        bottomIndicator.hasMore = System.currentTimeMillis() % 2 == 0L
                    }, 3000L)
                }
            }


            SwipeRefreshColumn(
                swipeRefreshState = swipeRefreshState,
                customBottomIndicator = bottomIndicator,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore
            ) {
                itemsIndexed(
                    items = dataList,
                    // key should provide in case of showing bottomIndicator
                    key = { index, _ ->
                        if (index == dataList.lastIndex) bottomIndicator.indicatorKey else index
                    }
                ) { _, item ->
                    Text(
                        text = "$item",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(20.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
