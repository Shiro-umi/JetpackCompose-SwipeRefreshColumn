# JetpackCompose-SwipeRefreshColumn

A custom LazyColumn supports swipe-down-refresh and load-more action.

# Preview

![refresh](https://user-images.githubusercontent.com/55919941/135393978-802cbf29-a194-4825-8d29-10c13a755226.gif)

![loadMore](https://user-images.githubusercontent.com/55919941/135393987-3d3741be-ad65-48ca-a9f3-3734474e0989.gif)

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
