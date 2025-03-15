package com.example.caissemanager.ui.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caissemanager.data.repository.CaisseRepository
import com.example.caissemanager.domain.model.TYPECAISSE
import com.example.caissemanager.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val caisseRepository: CaisseRepository) :
    ViewModel() {


    val uistate = mutableStateOf(HomeViewModelUiState())


    init {

        getTotalCDFEncaissement()
    }

    fun onChangingMenu(int: Int) {

        if (int == 0)
            uistate.value = uistate.value.copy(
                title = "Recettes"

            ) else if (int == 1) {
            uistate.value = uistate.value.copy(
                title = "Dépenses"
            )
        }
    }


    fun onSearchClick() {

        uistate.value = uistate.value.copy(
            isSearchShown = !uistate.value.isSearchShown
        )
    }

    fun onMenuClick() {

        uistate.value = uistate.value.copy(isMenuShow = true)

    }

    fun onMenuDismiss() {
        uistate.value = uistate.value.copy(isMenuShow = false)
    }

    fun getTotalCDFEncaissement() {

        viewModelScope.launch {
            caisseRepository.getAllCaisse().collect { result ->

                when (result) {

                    is Result.Loading -> {
                        uistate.value =
                            uistate.value.copy(isTotLoading = true, totErrorMessage = null)
                    }

                    is Result.Succes -> {

                        if (result.data != null) {
                            val totalUSDEncaissement =
                                result.data.filter { it.typeCaisse == TYPECAISSE.ENCAISSEMENT && it.devise == "USD" }
                                    .sumOf { it.montant }
                            val totalCDFEncaissement =
                                result.data.filter { it.typeCaisse == TYPECAISSE.ENCAISSEMENT && it.devise == "CDF" }
                                    .sumOf { it.montant }
                            val totalUSDDecaissement =
                                result.data.filter { it.typeCaisse == TYPECAISSE.DECAISSEMENT && it.devise == "USD" }
                                    .sumOf { it.montant }
                            val totalCDFDecaissement =
                                result.data.filter { it.typeCaisse == TYPECAISSE.DECAISSEMENT && it.devise == "CDF" }
                                    .sumOf { it.montant }

                            val soldeCDF = totalCDFEncaissement - totalCDFDecaissement
                            val soldeUSD = totalUSDEncaissement - totalUSDDecaissement

                            uistate.value = uistate.value.copy(
                                totalUSDEncaissement = totalUSDEncaissement,
                                totalCDFEncaissement = totalCDFEncaissement,
                                totalUSDDecaissement = totalUSDDecaissement,
                                totalCDFDecaissement = totalCDFDecaissement,
                                soldeCDF = soldeCDF,
                                soldeUSD = soldeUSD

                                )
                            uistate.value = uistate.value.copy(isTotLoading = false)
                        } else {

                            uistate.value = uistate.value.copy(
                                isTotLoading = false,
                                totErrorMessage = "Pas de données"
                            )
                        }

                    }

                    is Result.Error -> {

                        uistate.value = uistate.value.copy(
                            isTotLoading = false,
                            totErrorMessage = result.e?.message
                        )
                    }
                }
            }
        }
    }

    fun onMenuTotClick() {
        uistate.value = uistate.value.copy(isTotDialogShown = true)
        onMenuDismiss()
    }

    fun onTotDialogDismiss(){
        uistate.value = uistate.value.copy(isTotDialogShown = false)
    }

    fun onGraphicShown() {
        uistate.value = uistate.value.copy(isGraphicShown = true)
    }
    fun onGraphicDismiss() {
        uistate.value = uistate.value.copy(isGraphicShown = false)
    }

}

data class HomeViewModelUiState(
    val title: String = "Recettes",
    val isSearchShown: Boolean = false,
    val isMenuShow: Boolean = false,


    val isTotDialogShown: Boolean = false,
    val totalCDFEncaissement: Double = 0.0,
    val totalUSDEncaissement: Double = 0.0,
    val totalUSDDecaissement: Double = 0.0,
    val totalCDFDecaissement: Double = 0.0,
    val isTotLoading: Boolean = false,
    val totErrorMessage: String? = null,

    val soldeCDF: Double = 0.0,
    val soldeUSD: Double = 0.0,
    val isGraphicShown: Boolean=false,

    )