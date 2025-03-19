package com.example.caissemanager.ui.screen.compte

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caissemanager.data.repository.CompteRepository
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.domain.model.TYPECOMPTE
import com.example.caissemanager.utils.Result
import com.example.caissemanager.utils.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompteViewModel @Inject constructor(
    private val compteRepository: CompteRepository,

    ) : ViewModel() {


    val uistate = mutableStateOf(CompteUistate())

    val comptesState: StateFlow<Result<List<Compte>>> = compteRepository.comptes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Arrête après 5s d'inactivité
            initialValue = Result.Loading
        )

    private val _libelle get() = uistate.value.designation
    private val _typeCompte get() = uistate.value.typeCompte
    private val _updateCompte get() = uistate.value.updateCompte


    fun onDeviseChange(string: String) {
        uistate.value = uistate.value.copy(devise = string)
    }

    fun onDesignationChange(designation: String) {
        uistate.value = uistate.value.copy(designation = designation)
    }

    fun onTypeCompteChange(typeCompte: TYPECOMPTE) {
        uistate.value = uistate.value.copy(typeCompte = typeCompte)
    }


    //pour selection le compte à changer
    fun onUpdateCompteChange(compte: Compte) {
        uistate.value = uistate.value.copy(updateCompte = compte)
    }

    fun onUpdateDesignationChange(libelle: String) {
        uistate.value =
            uistate.value.copy(updateCompte = _updateCompte?.copy(designation = libelle))
    }

    fun onUpDateTypeCompteChange(typeCompte: TYPECOMPTE) {
        uistate.value =
            uistate.value.copy(updateCompte = _updateCompte?.copy(typeCompte = typeCompte))
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
                            designation = "",
                            typeCompte = TYPECOMPTE.RECETTE,
                            isAddSheetShown = false
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

    fun onAddCompteClick() {
        uistate.value = uistate.value.copy(isAddSheetShown = true)
    }

    fun onAddCompteDismiss() {
        uistate.value = uistate.value.copy(isAddSheetShown = false)
    }


    fun onDeleteClick() {
        uistate.value = uistate.value.copy(isDeleteDialogShown = true)
    }


    fun onDeleDialogDismiss() {

        uistate.value = uistate.value.copy(isDeleteDialogShown = false)
    }


    fun onDelete(compte: Compte) {


        viewModelScope.launch {

            compteRepository.delete(compte).collect { result ->

                when (result) {

                    is Result.Loading -> {
                        uistate.value =
                            uistate.value.copy(isLoading = true, isDeleteDialogShown = false)
                    }

                    is Result.Succes -> {

                        uistate.value = uistate.value.copy(isLoading = false)
                        SnackbarManager.showMessage("Suppression réussit")
                    }

                    is Result.Error -> {

                        uistate.value = uistate.value.copy(isLoading = false)
                        SnackbarManager.showMessage("${result.e?.message}")
                    }
                }


            }

        }

    }


    fun onEditClick() {

        uistate.value = uistate.value.copy(isEditSheetShow = true)
    }

    fun onEditSheetDismiss() {

        uistate.value = uistate.value.copy(isEditSheetShow = false)
    }


    fun onEdit() {

        viewModelScope.launch {


            _updateCompte?.let {
                compteRepository.update(it).collect { result ->

                    when (result) {

                        is Result.Loading -> {
                            uistate.value = uistate.value.copy(isLoading = true)
                        }

                        is Result.Succes -> {

                            uistate.value =
                                uistate.value.copy(isLoading = false, isEditSheetShow = false)
                            SnackbarManager.showMessage("Modification reussit")
                        }

                        is Result.Error -> {

                            uistate.value =
                                uistate.value.copy(isLoading = false, isEditSheetShow = false)
                            SnackbarManager.showMessage(result.e?.message.toString())
                        }

                    }


                }
            }


        }
    }

}

data class CompteUistate(
    val codeCompte: String = "",
    val devise: String = "",
    val designation: String = "",
    val typeCompte: TYPECOMPTE = TYPECOMPTE.RECETTE,
    val isLoading: Boolean = false,
    val isTypeCompteDropMenu: Boolean = false,

    val comptes: List<Compte> = emptyList(),
    val isComptEmpty: Boolean = false,
    val isLibeleEmpty: Boolean = false,
    val isAddSheetShown: Boolean = false,
    val isDeleteDialogShown: Boolean = false,
    val isEditSheetShow: Boolean = false,
    val updateCompte: Compte? = null
)


enum class DEVISE {

    USD,
    CDF
}