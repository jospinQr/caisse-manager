package com.example.caissemanager.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caissemanager.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController
) {

    LaunchedEffect(Unit) {
        viewModel.initNavController(navController = navController)
        delay(3000)
        viewModel.navigateToLogin()


    }

    Scaffold { innerpadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.clip(CircleShape),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null
                )


            }

            CircularProgressIndicator(modifier)
        }

    }


}