package com.example.caissemanager.utils

import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit


class StaticFunction {


    companion object {

        @JvmStatic
        fun formatTimestampToRelativeTime(firestoreTimestamp: Timestamp): String {
            // Convertir le timestamp Firestore en millisecondes
            val timestampMillis = firestoreTimestamp.toDate().time
            val currentTimeMillis = System.currentTimeMillis()

            // Calculer la différence en millisecondes
            val diffMillis = currentTimeMillis - timestampMillis

            // Convertir en différentes unités
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

            return when {
                minutes < 60 -> "Il y a $minutes m"
                hours < 24 -> "Il y a $hours h"
                days < 7 -> "Il y a $days j"
                days < 30 -> "Il y a ${days / 7} s"
                days < 365 -> "Il y a ${days / 30} mois"
                else -> "Il y a ${days / 365} an${if (days / 365 > 1) "s" else ""}"
            }
        }

    }
}