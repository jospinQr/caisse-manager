package com.example.caissemanager

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.caissemanager.navigation.AppNavHost
import com.example.caissemanager.ui.theme.CaisseManagerTheme
import com.example.caissemanager.utils.SnackbarManager
import com.example.caissemanager.utils.SnackbarMessage.Companion.toMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
        enableEdgeToEdge()
        setContent {
            CaisseManagerTheme {
                val snackBarState = remember { SnackbarHostState() }
                rememberAppState(snackbarHostState = snackBarState)
                Scaffold(snackbarHost = {
                    SnackbarHost(
                        modifier = Modifier.imePadding(),
                        hostState = snackBarState,
                        snackbar = { Snackbar(snackbarData = it) })
                }) { innerPadding -> AppNavHost() }
            }
        }
    }
}


class AppState(
    val scaffoldState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {

    init {
        coroutineScope.launch {

            snackbarManager.snackbarMessages.filterNotNull().collect {
                val text = it.toMessage(resources)
                scaffoldState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }

}


@Composable
fun rememberAppState(

    snackbarHostState: SnackbarHostState,
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, snackbarManager, resources, coroutineScope) {
    AppState(snackbarHostState, snackbarManager, resources, coroutineScope)
}


@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}