package com.example.caissemanager.ui.screen.encaissement

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caissemanager.data.repository.CaisseRepository
import com.example.caissemanager.domain.model.Caisse
import com.example.caissemanager.domain.model.TYPECAISSE
import com.example.caissemanager.utils.Result
import com.example.caissemanager.utils.SnackbarManager
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class EnCaissementViewmodel @Inject constructor(
    private val repository: CaisseRepository,
    private val context: Context
) : ViewModel() {

    val uistate = mutableStateOf(EncaissementUiState())

    private val _caisses = MutableStateFlow<Result<List<Caisse>>>(Result.Loading)
    val caisses: StateFlow<Result<List<Caisse>>> = _caisses


    private val _montant get() = uistate.value.montant
    private val _compte get() = uistate.value.compte
    private val _description get() = uistate.value.description
    private val _date get() = uistate.value.date
    private val _devise get() = uistate.value.devise


    //for update
    private val _upDateCaisse get() = uistate.value.upDateCaisse
    private val _updateMontant get() = uistate.value.upDateMontant
    private val _updateCompte get() = uistate.value.upDateCompte
    private val _updateDescription get() = uistate.value.upDateDescription
    private val _updateDate get() = uistate.value.upDateDate
    private val _upDateDevise get() = uistate.value.upDateDevise

    private val _selectedDevise = MutableStateFlow("USD")
    val selecteDevise: StateFlow<String> = _selectedDevise.asStateFlow()
    val deviseOptions = listOf("USD", "CDF")


    init {

        getByTypeCaisse()
        onOptionSelected("USD")

    }


    fun onMontantChange(montant: String) {
        uistate.value = uistate.value.copy(montant = montant, isMontaEmpy = false)
    }

    fun onCompteChange(compte: String) {
        uistate.value = uistate.value.copy(compte = compte, isCompteEmpty = false)
    }

    fun onDateChange(date: Timestamp) {

        uistate.value = uistate.value.copy(date = date)
    }

    fun onSearchValueChange(searchValue: String) {
        uistate.value = uistate.value.copy(searchValue = searchValue)
    }

    fun onDescriptionChange(description: String) {
        uistate.value = uistate.value.copy(description = description)
    }


    //for update

    fun onUpdateCaisseChange(caisse: Caisse) {
        uistate.value = uistate.value.copy(upDateCaisse = caisse)
    }

    fun onUpdateMontantChange(montant: Double) {
        uistate.value = uistate.value.copy(upDateCaisse = _upDateCaisse.copy(montant = montant))
    }

    fun onUpdateCompte(compte: String) {
        uistate.value = uistate.value.copy(upDateCaisse = _upDateCaisse.copy(compte = compte))
    }

    fun onUpateDateChange(date: Timestamp) {

        uistate.value = uistate.value.copy(upDateCaisse = _upDateCaisse.copy(date = date))
    }

    fun onUpDeviseChange(devise: String) {
        uistate.value = uistate.value.copy(upDateCaisse = _upDateCaisse.copy(devise = devise))
    }

    fun onUpdateDescriptionChange(description: String) {
        uistate.value =
            uistate.value.copy(upDateCaisse = _upDateCaisse.copy(descriptionCaisse = description))
    }


    fun onTypeCaisse(typeCaisse: String) {
        uistate.value = uistate.value.copy(typeCaisse = typeCaisse)
    }

    fun onSaveClick() {


        if (_montant.isEmpty()) {
            uistate.value = uistate.value.copy(isMontaEmpy = true)
            return
        }

        if (_compte.isEmpty()) {
            uistate.value = uistate.value.copy(isCompteEmpty = true)
            return
        }


        val caisse = Caisse(
            descriptionCaisse = _description,
            compte = _compte,
            typeCaisse = TYPECAISSE.ENCAISSEMENT,
            montant = _montant.toDouble(),
            date = _date,
            devise = _devise,

            )

        viewModelScope.launch {

            repository.save(caisse).collect { result ->

                when (result) {

                    is Result.Loading -> {

                        uistate.value = uistate.value.copy(isSavigin = true)
                    }

                    is Result.Succes -> {
                        uistate.value = uistate.value.copy(
                            isSavigin = false,
                            montant = "",
                            compte = "",
                            date = Timestamp.now(),
                            isAddSheetShown = false
                        )

                        SnackbarManager.showMessage("Encaissement ajouté avec succès")
                    }

                    is Result.Error -> {
                        uistate.value = uistate.value.copy(isSavigin = false)
                        SnackbarManager.showMessage("${result.e?.message}")
                    }
                }
            }

        }

    }


    fun onRefresh() {

        viewModelScope.launch {

            uistate.value = uistate.value.copy(caisse = emptyList(), isRefreshing = true)

            uistate.value = uistate.value.copy(isRefreshing = false)
        }


    }

    fun getByTypeCaisse() {

        viewModelScope.launch {
            repository.getCaisseByType(TYPECAISSE.ENCAISSEMENT).collect {
                _caisses.value = it
            }

        }

    }

    fun onAddClick() {
        uistate.value = uistate.value.copy(isAddSheetShown = true)
    }

    fun onAddSheetDismiss() {
        uistate.value = uistate.value.copy(isAddSheetShown = false)
    }


    fun onCompteClick() {
        uistate.value = uistate.value.copy(isCompteMenuShown = true)
    }

    fun onCompteMenuDismiss() {
        uistate.value = uistate.value.copy(isCompteMenuShown = false)
    }

    fun onDelete(caisse: Caisse) {

        viewModelScope.launch {

            repository.delete(caisse).collect {

                when (it) {

                    is Result.Loading -> {
                        uistate.value = uistate.value.copy(isSavigin = true)
                    }

                    is Result.Succes -> {
                        uistate.value =
                            uistate.value.copy(isSavigin = false, isDeleteAskDialogShown = false)
                        SnackbarManager.showMessage("Encaissement supprimmé avec succès")
                    }

                    is Result.Error -> {
                        uistate.value =
                            uistate.value.copy(isSavigin = false, isDeleteAskDialogShown = false)
                        Toast.makeText(context, it.e?.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }

    fun onDeleteClick() {
        uistate.value = uistate.value.copy(isDeleteAskDialogShown = true)

    }

    fun onDeleAlertDialogDismiss() {

        uistate.value = uistate.value.copy(isDeleteAskDialogShown = false)
    }

    fun onEditClick() {

        uistate.value = uistate.value.copy(isEditSheetShown = true)
    }

    fun onEditSheetDismiss() {

        uistate.value = uistate.value.copy(isEditSheetShown = false)
    }

    fun onDatePickeClick() {

        uistate.value = uistate.value.copy(isDatePickerShown = true)
    }

    fun onDateTimePickerDismiss() {
        uistate.value = uistate.value.copy(isDatePickerShown = false)
    }

    private fun onDeviseChange(string: String) {
        uistate.value = uistate.value.copy(devise = string)
    }


    fun onOptionSelected(option: String) {
        _selectedDevise.value = option
        onDeviseChange(option)
    }

    fun onTotalShown() {
        uistate.value = uistate.value.copy(isTotalShown = true)
    }

    fun onTotalHidden() {

        uistate.value = uistate.value.copy(isTotalShown = false)
    }


}

data class EncaissementUiState(

    val montant: String = "",
    val compte: String = "",
    val description: String = "",
    val typeCaisse: String = "",
    val searchValue: String = "",
    val date: Timestamp = Timestamp.now(),
    val isLoading: Boolean = false,
    val isAddSheetShown: Boolean = false,
    val isCompteMenuShown: Boolean = false,
    val caisse: List<Caisse> = emptyList(),
    val isRefreshing: Boolean = false,
    val isMontaEmpy: Boolean = false,
    val isCompteEmpty: Boolean = false,
    val isSavigin: Boolean = false,
    val isDeleteAskDialogShown: Boolean = false,
    val isEditSheetShown: Boolean = false,
    val isDatePickerShown: Boolean = false,
    val devise: String = "USD",
    val isTotalShown: Boolean = false,


    //for update
    val upDateCaisse: Caisse = Caisse(),
    val upDateMontant: String = "",
    val upDateCompte: String = "",
    val upDateDescription: String = "",
    val upDateTypeCaisse: String = "",
    val upDateDate: Timestamp = Timestamp.now(),
    val upDateDevise: String = ""
)