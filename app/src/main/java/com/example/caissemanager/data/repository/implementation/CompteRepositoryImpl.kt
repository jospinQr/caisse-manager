package com.example.caissemanager.data.repository.implementation

import android.util.Log
import com.example.caissemanager.data.repository.CompteRepository
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CompteRepositoryImpl @Inject constructor(val db: FirebaseFirestore) : CompteRepository {

    val TAG = "CompteRepositoryImpl"

    override suspend fun save(compte: Compte): Flow<Result<Unit>> = flow {

        emit(Result.Loading)
        try {

            val tempCodeCaisse = "TMP_${System.currentTimeMillis()}"
            val compteWithTempCode = compte.copy(code = tempCodeCaisse)
            db.collection("comptes").add(compteWithTempCode)
            emit(Result.Succes(Unit))

        } catch (ex: FirebaseFirestoreException) {
            emit(Result.Error(ex))
        } catch (ex: Exception) {
            emit(Result.Error(ex))
        }


    }

    override suspend fun delete(compteId: String): Flow<Result<Unit>> = flow { }


    override suspend fun update(compteI: String): Flow<Result<Unit>> = flow { }

    override suspend fun getCompte(compteId: String): Flow<Result<Compte>> = flow { }
    override suspend fun getComptesByType(): Flow<Result<List<Compte>>> {
        TODO("Not yet implemented")
    }

    override val comptes: Flow<Result<List<Compte>>> = callbackFlow {

        trySend(Result.Loading)
        val listener = db.collection("comptes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val comptes = snapshot.toObjects(Compte::class.java)
                    trySend(Result.Succes(comptes))
                }
            }

        awaitClose { listener.remove() }
    }


}


