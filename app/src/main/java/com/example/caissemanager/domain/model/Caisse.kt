package com.example.caissemanager.domain.model

import com.google.firebase.Timestamp
import com.google.type.Date

data class Caisse(
    val codeCaisse: String = "",
    val descriptionCaisse: String = "",
    val montant: Double = 0.0,
    val typeCaisse: TYPECAISSE = TYPECAISSE.ENCAISSEMENT,
    val compte: String = "",
    val date: Timestamp = Timestamp.now(),
    val devise: String = ""
)


enum class TYPECAISSE {

    ENCAISSEMENT,
    DECAISSEMENT,
}