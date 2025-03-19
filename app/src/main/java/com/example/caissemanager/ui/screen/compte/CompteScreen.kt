package com.example.caissemanager.ui.screen.compte

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.domain.model.TYPECOMPTE
import com.example.caissemanager.utils.Result

@Composable
fun CompteScreen(
    modifier: Modifier = Modifier, viewModel: CompteViewModel, onBack: () -> Unit
) {


    val listCompteResult by viewModel.comptesState.collectAsState(initial = Result.Loading)


    CompteScreenContent(

        uistate = viewModel.uistate.value,
        onCodeCompteChange = viewModel::onDeviseChange,
        onlibelleChange = viewModel::onDesignationChange,
        onSave = viewModel::onSaveClick,
        onBack = { onBack() },
        onTypeCompteChange = viewModel::onTypeCompteChange,
        onTypeCompteClick = viewModel::onTypeCompteClick,
        onTypeCompteDropMenuDismiss = viewModel::onTypeCompteDropMenuDismiss,
        onAddCompteClick = viewModel::onAddCompteClick,
        onAddSheetDismiss = viewModel::onAddCompteDismiss,
        listCompteResult = listCompteResult,
        onDeleteClick = viewModel::onDeleteClick,
        onEditClick = viewModel::onEditClick,
        onDelete = { viewModel.onDelete(it) },
        onDeleAlertDialogDismiss = viewModel::onDeleDialogDismiss,
        onEditSheetDismiss = viewModel::onEditSheetDismiss,
        onUpdateCompteChange = viewModel::onUpdateCompteChange,
        onEdit = viewModel::onEdit,
        onUpdateDesignationChange = viewModel::onUpdateDesignationChange,
        onUpdateTypeChange = viewModel::onUpDateTypeCompteChange
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
    onAddSheetDismiss: () -> Unit,
    onAddCompteClick: () -> Unit,
    listCompteResult: Result<List<Compte>>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDelete: (Compte) -> Unit,
    onDeleAlertDialogDismiss: () -> Unit,
    onEditSheetDismiss: () -> Unit,
    onUpdateCompteChange: (Compte) -> Unit,
    onEdit: () -> Unit,
    onUpdateDesignationChange: (String) -> Unit,
    onUpdateTypeChange: (TYPECOMPTE) -> Unit

) {


    var selectedCompte by remember { mutableStateOf<Compte?>(null) }
    val showDetailDialog by remember { derivedStateOf { selectedCompte != null } }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Compte", fontWeight = FontWeight.W900) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddCompteClick() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }


    ) { innerPadding ->


        when (val result = listCompteResult) {


            is Result.Loading -> {

                Box(modifier = Modifier.fillMaxSize()) {

                    CircularProgressIndicator()

                }
            }

            is Result.Succes -> {

                if (result.data.isNullOrEmpty()) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null)
                        Text("Aucun compte")
                    }

                } else {
                    LazyColumn(
                        Modifier.padding(innerPadding),

                        ) {
                        items(result.data) {

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCompte = it
                                        onUpdateCompteChange(it)
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                Column {
                                    Text(it.designation, fontWeight = FontWeight.Bold)
                                    Text(
                                        it.code.takeLast(8),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(it.typeCompte.name, style = MaterialTheme.typography.bodySmall)
                            }
                            HorizontalDivider()

                        }

                    }

                }
            }

            is Result.Error -> {
                Text(result.e?.message.toString())
            }
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

    if (uistate.isAddSheetShown) {


        ModalBottomSheet(onDismissRequest = onAddSheetDismiss) {
            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                OutlinedTextField(
                    value = uistate.designation,
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

    }

    if (uistate.isEditSheetShow) {


        ModalBottomSheet(onDismissRequest = onEditSheetDismiss) {
            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(text = uistate.updateCompte?.code!!)

                OutlinedTextField(
                    value = uistate.updateCompte.designation,
                    onValueChange = { onUpdateDesignationChange(it) },
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
                OutlinedTextField(value = uistate.updateCompte.typeCompte.name,
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
                                        onUpdateTypeChange(typeCompte)
                                        onTypeCompteDropMenuDismiss()
                                    }

                                    )

                                }

                            }
                        }
                    })
                Spacer(Modifier.height(4.dp))


                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    onEdit()
                    selectedCompte = null
                }) { Text("Modifier") }
            }
        }

    }

    if (showDetailDialog) {
        AlertDialog(
            onDismissRequest = { selectedCompte = null }, // Fermer en cliquant à l'extérieur
            title = { Text("Détails du compte") },
            text = {
                selectedCompte?.let { compte ->
                    Column {
                        Text(
                            "Code compte: ${compte.code}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Designation: ${compte.designation}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Type: ${compte.typeCompte}",
                            style = MaterialTheme.typography.bodyMedium
                        )


                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { onEditClick() }) {
                    Text("Modifier")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onDeleteClick() }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    if (uistate.isDeleteDialogShown) {

        AlertDialog(
            dismissButton = { TextButton(onClick = { onDeleAlertDialogDismiss() }) { Text("Non") } },
            confirmButton = {
                TextButton(onClick = {
                    selectedCompte?.let { onDelete(it) }
                    selectedCompte = null
                }) {
                    Text(
                        "Oui",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            onDismissRequest = { onDeleAlertDialogDismiss() },
            text = { Text("Voulez vous vraiement suprimmer ?") },
            title = { Text("Suppression") },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }

        )
    }
}



