package com.example.caissemanager.ui.screen.encaissement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CurrencyFranc
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caissemanager.R
import com.example.caissemanager.domain.model.Caisse
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.domain.model.TYPECOMPTE
import com.example.caissemanager.ui.component.MainLoading
import com.example.caissemanager.ui.screen.compte.CompteViewModel
import com.example.caissemanager.ui.screen.home.HomeViewModelUiState
import com.example.caissemanager.utils.Result
import com.example.caissemanager.utils.StaticFunction
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EncaissementScreen(
    modifier: Modifier = Modifier,
    viewModel: EnCaissementViewmodel = hiltViewModel(),
    compteViewModel: CompteViewModel,
    homeUiState: HomeViewModelUiState,

    ) {

    val uistate = viewModel.uistate.value
    val compteState by compteViewModel.comptesState.collectAsState(initial = Result.Loading)
    val caisseState by viewModel.caisses.collectAsState(initial = Result.Loading)
    val selectedDiviseOption by viewModel.selecteDevise.collectAsState()

    EncaissementScreenContent(
        uistate = uistate,
        onAddClick = { viewModel.onAddClick() },
        onDismiss = { viewModel.onAddSheetDismiss() },
        onMontantChange = { viewModel.onMontant(it) },
        onCompteChange = { viewModel.onCompte(it) },
        onSearValueChange = { viewModel.onSearchValue(it) },
        onDescriptionChange = { viewModel.onDescription(it) },
        comptes = compteState,
        onCompteClick = { viewModel.onCompteClick() },
        onCompteDropMenuDismiss = { viewModel.onCompteMenuDismiss() },
        onSaveClick = { viewModel.onSaveClick() },
        onRefresh = { viewModel.onRefresh() },
        caisseState = caisseState,
        onDelete = { viewModel.onDelete(caisse = it) },
        onDeleteClick = { viewModel.onDeleteClick() },
        onDeleAlertDialogDismiss = { viewModel.onDeleAlertDialogDismiss() },
        homeUiState = homeUiState,
        onEditClick = { viewModel.onEditClick() },
        onEditSheetDismiss = { viewModel.onEditSheetDismiss() },
        onDeviseChange = { viewModel.onOptionSelected(it) },
        onDateChange = {
            viewModel.onDateChange(it)
        },
        onDatePickerClick = {
            viewModel.onDatePickeClick()
        },
        onDateTimePickerDismiss = {
            viewModel.onDateTimePickerDismiss()
        },
        deviseOptions = viewModel.deviseOptions,
        selectedDiviseOption = selectedDiviseOption,
        onOptionSelected = { viewModel.onOptionSelected(it) },
        onTotalShown = { viewModel.onTotalShown() },
        onTotalHidden = { viewModel.onTotalHidden() }

    )

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncaissementScreenContent(
    uistate: EncaissementUiState,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit,
    onMontantChange: (String) -> Unit,
    onCompteChange: (String) -> Unit,
    onSearValueChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    comptes: Result<List<Compte>>,
    onCompteClick: () -> Unit,
    onCompteDropMenuDismiss: () -> Unit,
    onSaveClick: () -> Unit,
    onRefresh: () -> Unit,
    caisseState: Result<List<Caisse>>,
    onDelete: (Caisse) -> Unit,
    onDeleteClick: () -> Unit,
    onDeleAlertDialogDismiss: () -> Unit,
    homeUiState: HomeViewModelUiState,
    onEditClick: () -> Unit,
    onEditSheetDismiss: () -> Unit,
    onDateChange: (Timestamp) -> Unit,
    onDatePickerClick: () -> Unit,
    onDateTimePickerDismiss: () -> Unit,
    deviseOptions: List<String>,
    onDeviseChange: (String) -> Unit,
    selectedDiviseOption: String,
    onOptionSelected: (String) -> Unit,
    onTotalShown: () -> Unit,
    onTotalHidden: () -> Unit,

    ) {


    var selectedCaisse by remember { mutableStateOf<Caisse?>(null) }
    val showDialog by remember { derivedStateOf { selectedCaisse != null } }
    val dateState = rememberDatePickerState()
    var totalUSD by remember { mutableDoubleStateOf(0.0) }
    var totalCDF by remember { mutableDoubleStateOf(0.0) }



    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                FloatingActionButton(
                    onClick = { onTotalShown() },
                    modifier = Modifier
                        .size(40.dp), // Taille personnalisée pour le petit bouton
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Petit FAB")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    modifier = Modifier,
                    onClick = {
                        onAddClick()

                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Add, contentDescription = null
                    )
                }
            }
        }
    ) { innerPadding ->


        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize()
                .padding()
        ) {


            when (val result = caisseState) {

                is Result.Loading -> {
                    MainLoading()
                }

                is Result.Succes -> {

                    if (result.data?.isNotEmpty() == true) {

                        // Filtrer les données en fonction de searchValue
                        val filteredCaisses = remember(uistate.searchValue, result.data) {
                            if (uistate.searchValue.isBlank()) {
                                result.data // Afficher tout si la recherche est vide
                            } else {
                                result.data.filter { caisse ->
                                    caisse.descriptionCaisse.contains(
                                        uistate.searchValue,
                                        ignoreCase = true
                                    ) ||
                                            caisse.codeCaisse.contains(
                                                uistate.searchValue,
                                                ignoreCase = true
                                            ) ||
                                            caisse.compte.contains(
                                                uistate.searchValue,
                                                ignoreCase = true
                                            )
                                }
                            }
                        }

                        totalCDF =
                            filteredCaisses.filter { it.devise == "CDF" }.sumOf { it.montant }
                        totalUSD =
                            filteredCaisses.filter { it.devise == "USD" }.sumOf { it.montant }

                        PullToRefreshBox(state = rememberPullToRefreshState(),
                            isRefreshing = uistate.isRefreshing,
                            onRefresh = {
                                onRefresh()
                            }) {


                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedVisibility(
                                    visible = homeUiState.isSearchShown, modifier = Modifier
                                ) {

                                    Box(Modifier.padding(bottom = 12.dp)) {
                                        TextField(
                                            modifier = Modifier.clip(RoundedCornerShape(100)),
                                            value = uistate.searchValue,
                                            onValueChange = { onSearValueChange(it) },
                                            placeholder = { Text("Rechercher") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Rounded.Search,
                                                    contentDescription = null
                                                )
                                            },
                                            colors = TextFieldDefaults.colors(
                                                disabledIndicatorColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent

                                            )
                                        )
                                    }


                                }


                                LazyColumn(
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {


                                    itemsIndexed(filteredCaisses) { index, caisse ->

                                        val color = colors[index % colors.size]
                                        val deviseSuffix = if (caisse.devise == "USD") "$" else "Fc"

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    BorderStroke(
                                                        width = 0.5.dp,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    ), shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    selectedCaisse = caisse

                                                }


                                        ) {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(end = 12.dp, start = 4.dp)
                                            ) {

                                                Row(
                                                    modifier = Modifier
                                                        .align(Alignment.CenterStart)
                                                        .padding(vertical = 16.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .clip(CircleShape)
                                                            .background(color = color.copy(alpha = 0.2f)),
                                                        contentAlignment = Alignment.BottomCenter

                                                    ) {

                                                        Text(
                                                            modifier = Modifier


                                                                .align(Alignment.Center),
                                                            text = if (caisse.descriptionCaisse.isBlank()) "O" else caisse.descriptionCaisse[0].toString()
                                                                .toUpperCase(),
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Column(
                                                        modifier = Modifier.padding(horizontal = 12.dp),
                                                        horizontalAlignment = Alignment.Start
                                                    ) {
                                                        Text(
                                                            "${
                                                                NumberFormat.getInstance()
                                                                    .format(caisse.montant)
                                                            }  $deviseSuffix",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.W900
                                                        )
                                                        Text(
                                                            caisse.compte,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,

                                                            )

                                                    }
                                                }
                                                Column(
                                                    modifier = Modifier
                                                        .padding(horizontal = 12.dp)
                                                        .align(Alignment.CenterEnd),
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        caisse.codeCaisse.takeLast(10),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        StaticFunction.formatTimestampToRelativeTime(
                                                            caisse.date
                                                        ),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )

                                                }

                                            }
                                        }
                                        Spacer(Modifier.height(8.dp))


                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Votre caisse est vide",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.W900
                                )
                                Image(
                                    painter = painterResource(R.drawable.poor),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }

                        }
                    }
                }

                is Result.Error -> {
                    Text("Une Erreur est survenu ${caisseState.e?.message}")
                }
            }


        }
    }


    //bottom Sheet for adding new caisse
    if (uistate.isAddSheetShown) {

        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .imePadding()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Row {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        value = uistate.montant,
                        onValueChange = { onMontantChange(it) },
                        leadingIcon = {
                            AnimatedContent(
                                targetState = uistate.devise == "USD",
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        // Monte vers le haut
                                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                                slideOutVertically { height -> height } + fadeOut()
                                    } else {
                                        // Descend vers le bas
                                        slideInVertically { height -> height } + fadeIn() togetherWith
                                                slideOutVertically { height -> -height } + fadeOut()
                                    }
                                }) { target ->
                                Icon(
                                    imageVector = if (target) Icons.Rounded.AttachMoney else Icons.Rounded.CurrencyFranc,
                                    contentDescription = null
                                )

                            }
                        },
                        label = { Text("Montant", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        ),
                        isError = uistate.isMontaEmpy,
                        maxLines = 1,
                        singleLine = true,
                        supportingText = {
                            if (uistate.isMontaEmpy) {
                                Text(text = "Veuillez saisir un montant")
                            }
                        })
                    deviseOptions.forEach { option ->

                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = option == selectedDiviseOption,
                                onClick = { onOptionSelected(option) })
                            Text(option, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                when (val state = comptes) {
                    is Result.Loading -> {
                        CircularProgressIndicator()
                    }

                    is Result.Succes -> {
                        OutlinedTextField(value = uistate.compte,
                            onValueChange = { onCompteChange(it) },
                            label = { Text("Compte") },
                            isError = uistate.isCompteEmpty,
                            supportingText = {
                                if (uistate.isCompteEmpty) {
                                    Text(text = "Veuillez selectionner un compte")
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = {

                                    onCompteClick()
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = null
                                    )


                                    DropdownMenu(expanded = uistate.isCompteMenuShown,
                                        onDismissRequest = { onCompteDropMenuDismiss() }) {


                                        state.data?.filter { it.typeCompte == TYPECOMPTE.RECETTE || it.typeCompte == TYPECOMPTE.BANK }
                                            ?.forEach { compte ->

                                                DropdownMenuItem(text = { Text(compte.designation) },
                                                    onClick = {
                                                        onCompteChange(compte.designation)
                                                        onCompteDropMenuDismiss()
                                                    }

                                                )

                                            }

                                    }
                                }
                            })
                    }

                    is Result.Error -> {
                        Text(state.e?.message.toString())
                    }
                }

                OutlinedTextField(
                    value = uistate.description,
                    onValueChange = { onDescriptionChange(it) },
                    label = { Text("Description") },
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = uistate.date.toDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        ),
                    onValueChange = { onDateChange(Timestamp.now()) },
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = {

                            onDatePickerClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                        }
                    },


                    )
                Spacer(Modifier.height(10.dp))


                Button(onClick = {
                    onSaveClick()
                }) {
                    Text("Enregistrer")
                }

            }


        }
    }

    // Bottom Sheet  for edit a caisse
    if (uistate.isEditSheetShown) {
        ModalBottomSheet(onDismissRequest = onEditSheetDismiss) {
            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = uistate.montant,
                    onValueChange = { onMontantChange(it) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.AttachMoney,
                            contentDescription = null
                        )
                    },
                    label = { Text("Montant") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = uistate.isMontaEmpy,
                    supportingText = { if (uistate.isMontaEmpy) Text("Veuillez saisir un montant") }
                )
                Spacer(Modifier.height(10.dp))

                when (val state = comptes) {
                    is Result.Loading -> CircularProgressIndicator()
                    is Result.Succes -> {
                        OutlinedTextField(
                            value = uistate.compte,
                            onValueChange = { onCompteChange(it) },
                            label = { Text("Compte") },
                            isError = uistate.isCompteEmpty,
                            supportingText = { if (uistate.isCompteEmpty) Text("Veuillez sélectionner un compte") },
                            trailingIcon = {
                                IconButton(onClick = { onCompteClick() }) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = null
                                    )
                                    DropdownMenu(
                                        expanded = uistate.isCompteMenuShown,
                                        onDismissRequest = { onCompteDropMenuDismiss() }
                                    ) {
                                        state.data?.forEach { compte ->
                                            DropdownMenuItem(
                                                text = { Text(compte.designation) },
                                                onClick = {
                                                    onCompteChange(compte.designation)
                                                    onCompteDropMenuDismiss()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    is Result.Error -> Text(state.e?.message.toString())
                }

                OutlinedTextField(
                    value = uistate.description,
                    onValueChange = { onDescriptionChange(it) },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = uistate.date.toDate().toString(),
                    onValueChange = { },
                    label = { Text("Description") }
                )
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    onEditSheetDismiss();

                }) {
                    Text("Mettre à jour")
                }
            }
        }
    }

    if (uistate.isDatePickerShown) {

        DatePickerDialog(
            onDismissRequest = { onDateTimePickerDismiss() },
            dismissButton = {
                TextButton(onClick = {
                    onDateTimePickerDismiss()
                }) { Text("Annuler") }
            },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        // Convertir les millisecondes en Timestamp Firestore
                        onDateChange(Timestamp(millis / 1000, 0))
                        onDateTimePickerDismiss()
                    }
                }) { Text("Confirmer") }
            },
        ) {


            DatePicker(
                state = dateState,
                showModeToggle = false,
                title = { Text("Sélectionnez une date", Modifier.padding(start = 14.dp)) },
            )


        }
    }

    if (uistate.isSavigin) {

        AlertDialog(onDismissRequest = { }, text = {
            Row(verticalAlignment = Alignment.CenterVertically) {

                CircularProgressIndicator()
                Spacer(Modifier.width(8.dp))
                Text("Enregistrement en cours...", style = MaterialTheme.typography.bodySmall)
            }
        }, dismissButton = {}, confirmButton = {})
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { selectedCaisse = null }, // Fermer en cliquant à l'extérieur
            title = { Text("Détails de la caisse") },
            text = {
                selectedCaisse?.let { caisse ->
                    Column {
                        Text(
                            "Code caisse: ${caisse.codeCaisse}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (caisse.devise == "USD") "Montant: ${
                                NumberFormat.getInstance().format(caisse.montant)
                            } $" else "Montant: ${
                                NumberFormat.getInstance().format(caisse.montant)
                            } Fc",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Compte: ${caisse.compte}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Description: ${caisse.descriptionCaisse}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Date: ${StaticFunction.formatTimestampToRelativeTime(caisse.date)}",
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

    if (uistate.isDeleteAskDialogShown) {

        AlertDialog(
            dismissButton = { TextButton(onClick = { onDeleAlertDialogDismiss() }) { Text("Non") } },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(selectedCaisse!!)
                    selectedCaisse = null
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

    if (uistate.isTotalShown) {

        AlertDialog(
            title = { Text("Solde de recette") },
            onDismissRequest = { onTotalHidden() },
            text = {
                Column {

                    Text("Fanc congolais : ${NumberFormat.getInstance().format(totalCDF)} Fc")
                    Text("Dollars américain : ${NumberFormat.getInstance().format(totalUSD)} $")
                }

            }, confirmButton = {})
    }
}

val colors = listOf(
    Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta
)