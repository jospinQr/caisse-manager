package com.example.caissemanager.data.repository.implementation

import android.util.Log
import com.example.caissemanager.data.repository.CompteRepository
import com.example.caissemanager.domain.model.Compte
import com.example.caissemanager.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun delete(compte: Compte): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {

            if (compte.code.isBlank()) {
                emit(Result.Error(IllegalArgumentException("Code compte n'est peut pas etre vide")))
                return@flow
            }

            val querySnapshot = db.collection("comptes")
                .whereEqualTo("code", compte.code)
                .get(Source.DEFAULT).await()// Use DEFAULT to allow cached data in offline mode


            if (querySnapshot.isEmpty) {
                emit(Result.Error(Exception("Aucun document trouvé avec codeCaisse = ${compte.typeCompte}")))
                return@flow
            }


            val docRef = querySnapshot.documents[0].reference
            docRef.delete() // No await() needed for offline support
                .addOnSuccessListener {
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Delete failed: ${ex.message}", ex)
                }
            emit(Result.Succes(Unit))

        } catch (ex: FirebaseFirestoreException) {
            emit(Result.Error(ex))
            Log.e(TAG, "FirebaseFirestoreException: ${ex.message}", ex)
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, "Exception: ${ex.message}", ex)
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun update(newCompte: Compte): Flow<Result<Unit>> = flow {

        emit(Result.Loading)
        try {

            if (newCompte.code.isBlank()) {
                emit(Result.Error(IllegalArgumentException("Code compte n'est peut pas etre vide")))
                return@flow
            }

            val querySnapshot = db.collection("comptes")
                .whereEqualTo("code", newCompte.code)
                .get(Source.DEFAULT)
                .await()

            if (querySnapshot.isEmpty) {
                emit(Result.Error(Exception("Aucun document trouvé avec code = ${newCompte.typeCompte}")))
                return@flow
            }


            val docRef = querySnapshot.documents[0].reference
            val newCompte = mapOf(
                "designation" to newCompte.designation,
                "typeCompte" to newCompte.typeCompte
            )

            docRef.update(newCompte)
                .addOnSuccessListener {
                    Log.i(TAG, "Update succus")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Update failed: ${ex.message}", ex)
                }
            emit(Result.Succes(Unit))

        } catch (ex: FirebaseFirestoreException) {
            emit(Result.Error(ex))
            Log.e(TAG, "FirebaseFirestoreException: ${ex.message}", ex)
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, "Exception: ${ex.message}", ex)
        }
    }.flowOn(Dispatchers.IO)

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


