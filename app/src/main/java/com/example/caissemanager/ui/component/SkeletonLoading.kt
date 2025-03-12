package com.example.caissemanager.ui.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SkeletonLoadingEffect(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray,
        MaterialTheme.colorScheme.background,
        Color.LightGray,

        )
    val shimmerAnimation = rememberInfiniteTransition(label = "")
    val translateAnim by shimmerAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim),
    )

    Box(
        modifier = modifier
            .background(brush)
    ) {

    }
}


@Composable
fun MainLoading(){

    Column(
        modifier = Modifier
            .fillMaxSize()


    ) {

        for (i in 1..10) {

            Column(Modifier.padding(horizontal = 8.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonLoadingEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonLoadingEffect(
                    modifier = Modifier
                        .width(270.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonLoadingEffect(
                    modifier = Modifier
                        .width(200.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                SkeletonLoadingEffect(
                    modifier = Modifier
                        .width(180.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}