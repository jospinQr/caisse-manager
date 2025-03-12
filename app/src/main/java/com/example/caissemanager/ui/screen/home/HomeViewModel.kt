package com.example.caissemanager.ui.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {


    val uistate = mutableStateOf(HomeViewModelUiState())

    fun onChangingMenu(int: Int) {

        if (int == 0)
            uistate.value = uistate.value.copy(
                title = "Recettes"

            ) else if (int == 1) {
            uistate.value = uistate.value.copy(
                title = "Depenses"
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

}

data class HomeViewModelUiState(
    val title: String = "Recettes",
    val isSearchShown: Boolean = false,
    val isMenuShow: Boolean = false,
)