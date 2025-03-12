package com.example.caissemanager.ui.screen.compte

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caissemanager.data.repository.CompteRepository
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.domain.model.TYPECAISSE
import com.example.caissemanager.domain.model.TYPECOMPTE
import com.example.caissemanager.utils.Result
import com.example.caissemanager.utils.SnackbarManager
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompteViewModel @Inject constructor(
    private val compteRepository: CompteRepository,
    private val context: Context
) : ViewModel() {


    val uistate = mutableStateOf(CompteUistate())

    val comptesState: StateFlow<Result<List<Compte>>> = compteRepository.comptes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Arrête après 5s d'inactivité
            initialValue = Result.Loading
        )
    private val _devise get() = uistate.value.devise
    private val _libelle get() = uistate.value.libelle
    private val _typeCompte get() = uistate.value.typeCompte

    private val _selectedOption = MutableStateFlow("USD")

    val selectedOption: StateFlow<String> = _selectedOption.asStateFlow()

    val deviseOptions = listOf("USD", "CDF")
    fun onDeviseChange(string: String) {
        uistate.value = uistate.value.copy(devise = string)
    }

    fun onLibelleChange(libelle: String) {
        uistate.value = uistate.value.copy(libelle = libelle)
    }

    fun onTypeCompteChange(typeCompte: TYPECOMPTE) {
        uistate.value = uistate.value.copy(typeCompte = typeCompte)
    }


    fun onOptionSelected(option: String) {
        _selectedOption.value = option
        onDeviseChange(option)
    }

    fun onSaveClick() {


        if (_libelle.isEmpty()) {
            uistate.value = uistate.value.copy(isLibeleEmpty = true)
            return
        }


        val compte = Compte(designation = _libelle, typeCompte = _typeCompte)

        viewModelScope.launch {

            compteRepository.save(compte).collect { result ->

                when (result) {
                    is Result.Loading -> {
                        uistate.value = uistate.value.copy(isLoading = true)
                    }

                    is Result.Succes -> {
                        uistate.value = uistate.value.copy(
                            isLoading = false,
                            libelle = "",
                            typeCompte = TYPECOMPTE.RECETTE
                        )
                        SnackbarManager.showMessage("Compte ajouté avec succès")

                    }

                    is Result.Error -> {
                        uistate.value = uistate.value.copy(isLoading = false)
                        SnackbarManager.showMessage("${result.e?.message}")

                    }

                }


            }
        }
    }


    fun onTypeCompteClick() {

        uistate.value = uistate.value.copy(isTypeCompteDropMenu = true)
    }

    fun onTypeCompteDropMenuDismiss() {
        uistate.value = uistate.value.copy(isTypeCompteDropMenu = false)
    }


}

data class CompteUistate(
    val devise: String = "",
    val libelle: String = "",
    val typeCompte: TYPECOMPTE = TYPECOMPTE.RECETTE,
    val isLoading: Boolean = false,
    val isTypeCompteDropMenu: Boolean = false,

    val comptes: List<Compte> = emptyList(),
    val isComptEmpty: Boolean = false,
    val isLibeleEmpty: Boolean = false
)


enum class DEVISE {

    USD,
    CDF
}