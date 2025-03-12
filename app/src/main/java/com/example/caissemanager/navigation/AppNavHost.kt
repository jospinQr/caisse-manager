package com.example.caissemanager.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caissemanager.ui.screen.splash.SplashScreen
import com.example.caissemanager.ui.screen.auth.LoginScreen
import com.example.caissemanager.ui.screen.compte.CompteScreen
import com.example.caissemanager.ui.screen.compte.CompteViewModel
import com.example.caissemanager.ui.screen.home.HomeScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val compteViewModel: CompteViewModel = hiltViewModel()

    NavHost(
        modifier = Modifier.padding(),
        navController = navController,
        startDestination = "/splash"
    ) {


        composable(route = "/splash") {

            SplashScreen(navController=navController)
        }

        composable(route = "/login") {

            LoginScreen(navController = navController)
        }

        composable(route = "/home", enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }) {

            HomeScreen(

                compteViewModel = compteViewModel,
                onNavigateToCompte = { navController.navigate("/compte") }
            )
        }

        composable(
            route = "/compte",
            enterTransition = { slideInHorizontally() },

            ) {
            CompteScreen(
                viewModel = compteViewModel,
                onBack = { navController.popBackStack() }
            )
        }

    }
}

