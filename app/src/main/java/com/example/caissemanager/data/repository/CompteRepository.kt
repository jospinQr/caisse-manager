package com.example.caissemanager.data.repository

import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.utils.Result
import kotlinx.coroutines.flow.Flow

interface CompteRepository {


    val comptes : Flow<Result<List<Compte>>>

    suspend fun save(compte: Compte): Flow<Result<Unit>>

    suspend fun delete(compte: Compte): Flow<Result<Unit>>

    suspend fun update(compte : Compte): Flow<Result<Unit>>

    suspend fun getCompte(compteId: String): Flow<Result<Compte>>

    suspend fun getComptesByType(): Flow<Result<List<Compte>>>

}