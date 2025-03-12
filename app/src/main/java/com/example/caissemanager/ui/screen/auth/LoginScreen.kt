package com.example.caissemanager.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.math.log


@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uistate by loginViewModel.uistate


    LaunchedEffect(Unit) {

        loginViewModel.initNavController(navController)
    }
    LoginScreenContent(
        uistate = uistate,
        onEmailChange = loginViewModel::onEmailChange,
        onPassWordChange = loginViewModel::onPasswordChange,
        onLogin = { loginViewModel.onLogin() },
        onPassWordVisibilityChange = loginViewModel::onPasswordVisibilityChange

    )
}

@Composable
fun LoginScreenContent(
    uistate: LoginUistate,
    onEmailChange: (String) -> Unit,
    onPassWordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onPassWordVisibilityChange: () -> Unit
) {
    Scaffold(modifier = Modifier) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                "Connexion",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W900
            )
            Spacer(Modifier.height(4.dp))
            Icon(modifier = Modifier.size(22.dp),imageVector = Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = uistate.email,
                onValueChange = { onEmailChange(it) },
                label = { Text("E-mail") },
                isError = uistate.isEemailEmpty,
                supportingText = {
                    if (uistate.isEemailEmpty) {
                        Text(
                            "L'adresse e-mail ne doit pas être vide",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Rounded.AlternateEmail, contentDescription = null) }
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = uistate.password,
                onValueChange = { onPassWordChange(it) },
                label = { Text("Mot de passe") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onPassWordVisibilityChange() }) {
                        Icon(
                            imageVector = if (uistate.isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                isError = uistate.isPasswordShort,
                supportingText = {
                    if (uistate.isPasswordShort) {
                        Text(
                            "Le mot de passe doit contenir au moins 8 caractères",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                maxLines = 1,
                singleLine = true,
                visualTransformation = if (!uistate.isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None
            )
            Spacer(Modifier.height(24.dp))

            Button(onClick = { onLogin() }) { Text("Connexion") }
        }

    }



    if (uistate.isLoading) {


        AlertDialog(
            confirmButton = {},
            dismissButton = {},
            title = {},
            text = { CircularProgressIndicator() },
            onDismissRequest = {}

        )
    }
}
