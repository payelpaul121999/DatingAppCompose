package com.pal.datingapp.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pal.datingapp.card.RightAlignedCircles
import com.pal.datingapp.card.RightAlignedCirclesForBottom
import com.pal.datingapp.data.ProfileCard
import com.pal.datingapp.data.staticData.sampleProfiles
import kotlinx.coroutines.launch



@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column() {
        SwipeCardStack(
            profiles = sampleProfiles,
            onSwipeLeft = { println("Disliked: ${it.name}") },
            onSwipeRight = { println("Liked: ${it.name}") }
        )
        RightAlignedCirclesForBottom(
            onLeftClick = {
                // Handle left circle click
                Log.d("@@@MyScreen", "Left circle clicked")
            },
            onCenterClick = {
                // Handle center circle click
                Log.d("@@@MyScreen", "Center circle clicked")
            },
            onRightClick = {
                // Handle right circle click
                Log.d("@@@MyScreen", "Right circle clicked")
            }
        )
    }

    //Text("Home Screen", modifier = modifier)
}

@Composable
fun SwipeCardStack(
    profiles: MutableList<ProfileCard>,
    onSwipeLeft: (ProfileCard) -> Unit = {},
    onSwipeRight: (ProfileCard) -> Unit = {}
) {
    var cardList by remember { mutableStateOf(profiles) }

    Box(modifier = Modifier) {
        cardList.take(3).reversed().forEachIndexed { index, card ->
            SwipeableCardReuse(
                modifier = Modifier,
                card = card,
                onSwiped = { direction ->
                    if (cardList.isNotEmpty()) {

                        // Map reversed index to actual list index
                        val actualIndex = cardList.lastIndex - index

                        val removedCard = cardList[actualIndex]

                        cardList = cardList.toMutableList().apply {
                            removeAt(actualIndex)
                        }

                        if (direction == SwipeDirection.RIGHT)
                            onSwipeRight(removedCard)
                        else
                            onSwipeLeft(removedCard)
                    }
                },
                isTopCard = index == 0
            )
        }
    }
}

enum class SwipeDirection { LEFT, RIGHT, NONE }

@Composable
fun SwipeableCardReuse(
    modifier: Modifier = Modifier,
    card: ProfileCard,
    isTopCard: Boolean,
    onSwiped: (SwipeDirection) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // rotation degrees derived from offsetX
    //val rotation by derivedStateOf { (offsetX.value / 40).coerceIn(-40f, 40f) }
    val rotation by remember {
        derivedStateOf { (offsetX.value / 40f).coerceIn(-40f, 40f) }
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // decide threshold
                        val swipeThreshold = 300f
                        when {
                            offsetX.value > swipeThreshold -> {
                                // animate out to right then call callback
                                scope.launch {
                                    // smooth animate to the right off-screen
                                    offsetX.animateTo(
                                        targetValue = size.width * 1.5f, // uses pointerInput scope size
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                    onSwiped(SwipeDirection.RIGHT)
                                    // reset offsets in case composable remains (optional)
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                            }

                            offsetX.value < -swipeThreshold -> {
                                scope.launch {
                                    offsetX.animateTo(
                                        targetValue = -size.width * 1.5f,
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                    onSwiped(SwipeDirection.LEFT)
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                            }

                            else -> {
                                // not enough: spring back to center
                                scope.launch {
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                    offsetY.animateTo(0f, animationSpec = spring())
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        // update directly while dragging
                        scope.launch {
                            // use snapTo for immediate response during drag
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
    ) {
        Column() {
            ProfileCardUI(card)
        }
    }
}

@Composable
fun ProfileCardUI(card: ProfileCard) {
    Box(
        modifier = Modifier
            .padding(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .shadow(16.dp, RoundedCornerShape(28.dp))
    ) {
        Column {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomStart)) {
            RightAlignedCircles()
            Box(modifier = Modifier.fillMaxWidth()) {
                Column( Modifier.fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.25f))) {
                    Text("${card.name}, ${card.age}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(card.profession, fontSize = 16.sp, color = Color.White)
                }
            }

        }
    }
}


