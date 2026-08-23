package com.example.anima.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

//horizontal transition to use in AnimatedContent
fun horizontalSlideTransition(forward: Boolean): ContentTransform =
    (slideInHorizontally { if (forward) it else -it } + fadeIn())
        .togetherWith(slideOutHorizontally { if (forward) -it else it } + fadeOut())