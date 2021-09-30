# JetpackCompose-SwipeRefreshColumn

A custom LazyColumn supports swipe-down-refresh and load-more action.

# Preview

![refresh](https://user-images.githubusercontent.com/55919941/135392888-89e1a8e3-ffae-47fc-b528-cc4af5d75934.gif)

![loadMore](https://user-images.githubusercontent.com/55919941/135392910-70b47165-f351-4c75-97f7-d1dc450fb7d2.gif)

# Dependency
```gradle
implementation 'com.shiroumi:swiperefreshcolumn:1.0.0-alpha02'
```

# Usage
1. Initialize a SwipeRefreshState
```kotlin
val swipeRefreshState = rememberSwipeRefreshState(
    triggerOffset = with(LocalDensity.current) { Offset(0f, (-32).dp.toPx()) }
)
```

2. Provide a TopIndicator
3. Provide a BottomIndicator
4. Provide a onRefresh callback, when refresh is done, don't forget to do this:
```kotlin
swipeRefreshState.isRefreshing = false
bottomIndicator.hasMore = true // or false
```
5. Provide a onLoadMore callback, when loadMore is done, do the similar thing as Refresh
```kotlin
swipeRefreshState.isLoadingMore = false
bottomIndicator.hasMore = true // or false
```
6. Enjoy it
```kotlin
SwipeRefreshColumn(
    swipeRefreshState = swipeRefreshState,
    customTopIndicator = topIndicator,
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
```


# 1.0.0-alpha2 Update
Add Default Indicators (both Top and Bottom) with animation
