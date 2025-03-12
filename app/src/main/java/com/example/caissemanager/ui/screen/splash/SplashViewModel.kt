package com.example.caissemanager.ui.screen.splash

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val currentUser: FirebaseUser?) : ViewModel() {

    private var _navControlle: NavController? = null


    fun initNavController(navController: NavController) {
        _navControlle = navController
    }


    fun navigateToLogin() {


        if (currentUser != null) {
            _navControlle?.navigate("/home") {

                popUpTo(0) { inclusive = true }
            }
        } else {
            _navControlle?.navigate("/login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }


}