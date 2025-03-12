package com.example.caissemanager.ui.screen.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController

import com.example.caissemanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.caissemanager.utils.Result

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val context: Context
) : ViewModel() {


    val uistate = mutableStateOf(LoginUistate())

    private val _userMail get() = uistate.value.email
    private val _passWord get() = uistate.value.password

    private var _navController: NavController? = null


    fun initNavController(navController: NavController) {
        _navController = navController
    }


    init {
        uistate.value = uistate.value.copy(email = "mugamba@gmail.com")
    }

    fun onEmailChange(email: String) {

        uistate.value = uistate.value.copy(email = email, isEemailEmpty = false)
    }

    fun onPasswordChange(password: String) {
        uistate.value = uistate.value.copy(password = password, isPasswordShort = false)
    }


    fun onLogin() {


        if (_userMail.isEmpty()) {
            uistate.value = uistate.value.copy(isEemailEmpty = true)
            return
        }

        if (_passWord.length < 8) {
            uistate.value = uistate.value.copy(isPasswordShort = true)
            return
        }

        viewModelScope.launch {

            repository.login(email = _userMail, passWord = _passWord).collect { result ->


                when (result) {

                    is Result.Loading -> {
                        uistate.value = uistate.value.copy(isLoading = true)
                    }

                    is Result.Succes -> {

                        uistate.value = uistate.value.copy(isLoading = false)
                        _navController?.navigate("/home") {
                            popUpTo("/login") {
                                inclusive = true
                            }
                        }

                    }

                    is Result.Error -> {
                        uistate.value = uistate.value.copy(isLoading = false)
                        Toast.makeText(context, result.e?.message, Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }

    }

    fun onPasswordVisibilityChange() {
        uistate.value = uistate.value.copy(isPasswordVisible = !uistate.value.isPasswordVisible)
    }
}

data class LoginUistate(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isEemailEmpty: Boolean = false,
    val isPasswordShort: Boolean = false,
    val isPasswordVisible: Boolean = false,

    )