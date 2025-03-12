package com.example.caissemanager.ui.screen.compte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caissemanager.domain.model.TYPECOMPTE

@Composable
fun CompteScreen(
    modifier: Modifier = Modifier, viewModel: CompteViewModel, onBack: () -> Unit
) {

    val selectedOption by viewModel.selectedOption.collectAsState()
    CompteScreenContent(

        uistate = viewModel.uistate.value,
        onCodeCompteChange = viewModel::onDeviseChange,
        onlibelleChange = viewModel::onLibelleChange,
        onSave = viewModel::onSaveClick,
        onBack = { onBack() },
        onTypeCompteChange = viewModel::onTypeCompteChange,
        onTypeCompteClick = viewModel::onTypeCompteClick,
        onTypeCompteDropMenuDismiss = viewModel::onTypeCompteDropMenuDismiss,
        deviseOptions = viewModel.deviseOptions,
        selectedOption = selectedOption,
        onOptionSelected = { viewModel.onOptionSelected(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompteScreenContent(
    uistate: CompteUistate,
    onCodeCompteChange: (String) -> Unit,
    onlibelleChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onTypeCompteChange: (TYPECOMPTE) -> Unit,
    onTypeCompteClick: () -> Unit,
    onTypeCompteDropMenuDismiss: () -> Unit,
    deviseOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit

) {

    Scaffold(topBar = {
        TopAppBar(title = { Text("Creér un compte", fontWeight = FontWeight.W900) },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            })
    }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            OutlinedTextField(
                value = uistate.libelle,
                onValueChange = { onlibelleChange(it) },
                label = { Text("Nom du compte") },
                leadingIcon = { Icon(Icons.Rounded.Abc, contentDescription = null) },
                isError = uistate.isLibeleEmpty,
                supportingText = {
                    if (uistate.isLibeleEmpty) {
                        Text(
                            "Ne peut pas être vide", style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                maxLines = 1,
                singleLine = true,

                )

            Spacer(Modifier.height(18.dp))
            OutlinedTextField(value = uistate.typeCompte.name,
                onValueChange = { },
                label = { Text("Type de compte") },
                trailingIcon = {
                    IconButton(onClick = {

                        onTypeCompteClick()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null
                        )


                        DropdownMenu(expanded = uistate.isTypeCompteDropMenu,
                            onDismissRequest = { onTypeCompteDropMenuDismiss() }) {


                            TYPECOMPTE.entries.forEach { typeCompte ->

                                DropdownMenuItem(text = { Text(typeCompte.name) }, onClick = {
                                    onTypeCompteChange(typeCompte)
                                    onTypeCompteDropMenuDismiss()
                                }

                                )

                            }

                        }
                    }
                })
            Spacer(Modifier.height(4.dp))


            Spacer(Modifier.height(24.dp))
            Button(onClick = { onSave() }) { Text("Enrgistrer") }
        }

    }

    if (uistate.isLoading) {


        AlertDialog(confirmButton = {},
            dismissButton = {},
            title = {},
            text = { CircularProgressIndicator() },
            onDismissRequest = {}

        )
    }

}




