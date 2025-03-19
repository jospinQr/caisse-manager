package com.example.caissemanager.data.repository

import com.example.caissemanager.domain.model.Caisse
import com.example.caissemanager.domain.model.TYPECAISSE
import com.example.caissemanager.utils.Result
import kotlinx.coroutines.flow.Flow

interface CaisseRepository {


    suspend fun save(caisse: Caisse): Flow<Result<Unit>>

    suspend fun update(newCaisse: Caisse): Flow<Result<Unit>>

    suspend fun delete(caisse: Caisse): Flow<Result<Unit>>

    suspend fun getCaisseByCode(codeCaisse: String): Flow<Result<Caisse>>

    suspend fun getAllCaisse(): Flow<Result<List<Caisse>>>

    suspend fun getCaisseByCompte(codeCompte: String): Flow<Result<List<Caisse>>>

    suspend fun getCaisseByType(typeCaisse: TYPECAISSE): Flow<Result<List<Caisse>>>

    suspend fun getCaisseByDate(date: String): Flow<Result<List<Caisse>>>

    suspend fun getCaisseByMontant(montant: Double): Flow<Result<List<Caisse>>>

    suspend fun saveTot(total: Double): Flow<Result<Unit>>

    suspend fun getTot(devises: String): Flow<Result<Double>>


}