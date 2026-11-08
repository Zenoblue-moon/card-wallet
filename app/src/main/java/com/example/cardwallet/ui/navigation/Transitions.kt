package com.example.cardwallet.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cardwallet.R

data class TransitionOption(val key: String)

val transitionOptions = listOf(
    TransitionOption("fade"),
    TransitionOption("slide_right"),
    TransitionOption("slide_left"),
    TransitionOption("slide_up"),
    TransitionOption("scale"),
    TransitionOption("none"),
)

@Composable
fun transitionLabel(key: String): String = stringResource(
    when (key) {
        "slide_right" -> R.string.transition_slide_right
        "slide_left" -> R.string.transition_slide_left
        "slide_up" -> R.string.transition_slide_up
        "scale" -> R.string.transition_scale
        "none" -> R.string.transition_none
        else -> R.string.transition_fade
    }
)

data class NavTransitionSet(
    val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition,
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition,
)

fun navTransitionSet(key: String): NavTransitionSet = when (key) {
    "slide_right" -> NavTransitionSet(
        enter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        exit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        popEnter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
        popExit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
    )
    "slide_left" -> NavTransitionSet(
        enter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
        exit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
        popEnter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        popExit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
    )
    "slide_up" -> NavTransitionSet(
        enter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300)) },
        exit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300)) },
        popEnter = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300)) },
        popExit = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300)) },
    )
    "scale" -> NavTransitionSet(
        enter = { scaleIn(initialScale = 0.92f, animationSpec = tween(300)) + fadeIn(tween(300)) },
        exit = { scaleOut(targetScale = 0.92f, animationSpec = tween(300)) + fadeOut(tween(300)) },
        popEnter = { scaleIn(initialScale = 0.92f, animationSpec = tween(300)) + fadeIn(tween(300)) },
        popExit = { scaleOut(targetScale = 0.92f, animationSpec = tween(300)) + fadeOut(tween(300)) },
    )
    "none" -> NavTransitionSet(
        enter = { EnterTransition.None },
        exit = { ExitTransition.None },
        popEnter = { EnterTransition.None },
        popExit = { ExitTransition.None },
    )
    else -> NavTransitionSet(
        enter = { fadeIn(tween(300)) },
        exit = { fadeOut(tween(300)) },
        popEnter = { fadeIn(tween(300)) },
        popExit = { fadeOut(tween(300)) },
    )
}