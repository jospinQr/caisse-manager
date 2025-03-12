package com.example.caissemanager.domain.model

data class Compte(


    val code: String = "",
    val designation: String = "",
    val typeCompte: TYPECOMPTE = TYPECOMPTE.RECETTE
)

enum class TYPECOMPTE {

    RECETTE,
    DEPENSE,
    BANK,

}