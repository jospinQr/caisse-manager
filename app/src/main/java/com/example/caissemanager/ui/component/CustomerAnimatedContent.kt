package com.example.caissemanager.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun AnimatedContentTransition(
    targetState: Any, // L'état cible (peut être un String, Int, ou tout autre type)
    initialState: Any, // L'état initial
    modifier: Modifier = Modifier,
    content: @Composable (target: Any) -> Unit // Le contenu à afficher
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (targetState.toString() > initialState.toString()) {
                // Animation vers le haut
                slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
            } else {
                // Animation vers le bas
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            }
        },
        modifier = modifier
    ) { target ->
        content(target) // Afficher le contenu personnalisé
    }
}