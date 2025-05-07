package com.example.wowHub.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import com.example.wowHub.R
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback



@Composable
fun HomeScreen() {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val items = remember { mutableStateListOf<Int>() }
    var viewportHeight by remember { mutableStateOf(0f) }
    val fadeDistance = 75f // px
    val itemHeight = 100.dp
    val itemSpacing = 150.dp
    val scrollSpeed = 30f // px per drag event
    val headerHeightPx = 180f
    var videoItemY by remember { mutableStateOf(0f) }

    // Initialize with some items
    LaunchedEffect(Unit) {
        if (items.isEmpty()) items.addAll(0 until 20)
    }

    // Infinite loading
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex > items.size - 10) {
            items.addAll(items.size until items.size + 20)
        }
    }

    // Arrow animation
    val infiniteTransition = rememberInfiniteTransition(label = "bob")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    // Arrow logic: show arrow unless at the bottom
    val isAtBottom = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == items.lastIndex

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                viewportHeight = coordinates.size.height.toFloat()
            }
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        coroutineScope.launch {
                            val direction = if (dragAmount > 0) 1 else -1
                            val currentIndex = listState.firstVisibleItemIndex
                            val currentOffset = listState.firstVisibleItemScrollOffset
                            val targetOffset = (currentOffset + direction * scrollSpeed).toInt().coerceAtLeast(0)
                            listState.animateScrollToItem(currentIndex, targetOffset)
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Text
            item {
                var itemY by remember { mutableStateOf(0f) }
                val alpha = calculateFade(itemY, viewportHeight, fadeDistance, headerHeightPx)
                Text(
                    text = "Welcome to WowHub",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight(2),
                    modifier = Modifier
                        .alpha(alpha)
                        .padding(top = 32.dp)
                        .onGloballyPositioned { coordinates ->
                            itemY = coordinates.positionInRoot().y
                        }
                )
            }

            // WoW Pixel Logo
            item {
                var itemY by remember { mutableStateOf(0f) }
                val alpha = calculateFade(itemY, viewportHeight, fadeDistance, headerHeightPx)
                Image(
                    painter = painterResource(id = R.drawable.wow_pixel),
                    contentDescription = "WoW Pixel Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .alpha(alpha)
                        .onGloballyPositioned { coordinates ->
                            itemY = coordinates.positionInRoot().y
                        }
                )
            }

            item {
                FadingTextItem(
                    text = "A unique perspective on 2-day raiding",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Our Core Principles:",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Self-Accountability",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Curiosity",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Innovation",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Competitiveness",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "And above all...",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

            item {
                FadingTextItem(
                    text = "Performance.",
                    viewportHeight = viewportHeight,
                    fadeDistance = fadeDistance,
                    headerHeightPx = headerHeightPx
                )
            }

           item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .onGloballyPositioned { coordinates ->
                        videoItemY = coordinates.positionInRoot().y
                    }
            ) {
                // Only show the video if the item is visible in the viewport
                NativeYouTubePlayer(
                    videoId = "sy74BxaYnyA",
                    startSeconds = 51f
                )
            }
        }



            // Infinite items (example)
            itemsIndexed(items) { index, item ->
                var itemY by remember { mutableStateOf(0f) }
                val alpha = calculateFade(itemY, viewportHeight, fadeDistance, headerHeightPx)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .alpha(alpha)
                        .onGloballyPositioned { coordinates ->
                            itemY = coordinates.positionInRoot().y
                        }
                        .background(Color(0xFF222244))
                ) {
                    Text(
                        text = "Item $item",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Arrow anchored to bottom of screen
        if (!isAtBottom) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Scroll down",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp)
                    .offset(y = yOffset.dp)
            )
        }
    }
}

private fun calculateFade(itemY: Float, viewportHeight: Float, fadeDistance: Float, headerHeightPx: Float): Float {
    return when {
        itemY < headerHeightPx + fadeDistance -> (itemY - headerHeightPx) / fadeDistance
        itemY > viewportHeight - fadeDistance -> (viewportHeight - itemY) / fadeDistance
        else -> 1f
    }.coerceIn(0f, 1f)
}

@Composable
fun FadingTextItem(
    text: String,
    viewportHeight: Float,
    fadeDistance: Float,
    headerHeightPx: Float = 0f,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displaySmall,
    fontWeight: FontWeight = FontWeight(2),
    fontFamily: FontFamily? = null,
    modifier: Modifier = Modifier
) {
    var itemY by remember { mutableStateOf(0f) }
    val alpha = calculateFade(itemY, viewportHeight, fadeDistance, headerHeightPx)
    Text(
        text = text,
        color = Color.White,
        style = style,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        modifier = modifier
            .alpha(alpha)
            .padding(horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
                itemY = coordinates.positionInRoot().y
            }
    )
}

@Composable
fun NativeYouTubePlayer(videoId: String, startSeconds: Float) {
    var isPlayerReady by remember { mutableStateOf(false) }
    var shouldPlay by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }
    
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                // Configure the player
                enableAutomaticInitialization = true
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        isPlayerReady = true
                        player = youTubePlayer
                        // Load and play the video immediately
                        youTubePlayer.loadVideo(videoId, startSeconds)
                    }
                })
                
                // Make the player uninteractable
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false

            }
        },
        update = {
            // When the player is ready and should play, ensure it's playing
            if (isPlayerReady && shouldPlay) {
                player?.play()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    )
    
    // Update shouldPlay based on visibility
    LaunchedEffect(Unit) {
        shouldPlay = true
    }
}