package com.example.caissemanager.ui.screen.decaissement

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
class DeCaissementViewmodel @Inject constructor(
    private val repository: CaisseRepository,
    private val context: Context
) : ViewModel() {

    val uistate = mutableStateOf(DecaissementUiState())

    private val _caisses = MutableStateFlow<Result<List<Caisse>>>(Result.Loading)
    val caisses: StateFlow<Result<List<Caisse>>> = _caisses


    private val _montant get() = uistate.value.montant
    private val _compte get() = uistate.value.compte
    private val _description get() = uistate.value.description
    private val _searchValue get() = uistate.value.searchValue
    private val _typeCaisse get() = uistate.value.typeCaisse
    private val _date get() = uistate.value.date
    private val _devise get() = uistate.value.devise

    private val _selectedDevise = MutableStateFlow("USD")
    val selecteDevise: StateFlow<String> = _selectedDevise.asStateFlow()
    val deviseOptions = listOf("USD", "CDF")


    init {

        getByTypeCaisse()
        onOptionSelected("USD")

    }


    fun onMontant(montant: String) {
        uistate.value = uistate.value.copy(montant = montant, isMontaEmpy = false)
    }

    fun onCompte(compte: String) {
        uistate.value = uistate.value.copy(compte = compte, isCompteEmpty = false)
    }

    fun onDateChange(date: Timestamp) {

        uistate.value = uistate.value.copy(date = date)
    }

    fun onSearchValue(searchValue: String) {
        uistate.value = uistate.value.copy(searchValue = searchValue)
    }

    fun onDescription(description: String) {
        uistate.value = uistate.value.copy(description = description)
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
            typeCaisse = TYPECAISSE.DECAISSEMENT,
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

                        SnackbarManager.showMessage("Vous venez de decaisser $_montant")
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
            repository.getCaisseByType(TYPECAISSE.DECAISSEMENT).collect {
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
                        SnackbarManager.showMessage("Decaissement supprimmé avec succès")
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

data class DecaissementUiState(

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
    val isTotalShown: Boolean = false
)