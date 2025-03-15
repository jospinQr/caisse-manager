package com.example.caissemanager.data.repository.implementation

import android.util.Log
import com.example.caissemanager.data.repository.CaisseRepository
import com.example.caissemanager.domain.model.Caisse
import com.example.caissemanager.domain.model.TYPECAISSE
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

class CaisseRepositoryImpl @Inject constructor(val db: FirebaseFirestore) : CaisseRepository {

    val TAG = "CaisseRepository"


    override suspend fun save(caisse: Caisse): Flow<Result<Unit>> = flow {


        emit(Result.Loading)

        try {

            val tempCodeCaisse = "${System.currentTimeMillis()}"
            val caisseWithTempCode = caisse.copy(codeCaisse = tempCodeCaisse)
            db.collection("caisses").add(caisseWithTempCode)

            emit(Result.Succes(Unit))
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, ex.message.toString())
        }

    }

    override suspend fun update(caisse: Caisse): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            // Rechercher le document par un champ unique (ex: codeCaisse)
            val querySnapshot = db.collection("caisses")
                .whereEqualTo("codeCaisse", caisse.codeCaisse)
                .get()
                .await()

            // Vérifier si un document correspond
            if (querySnapshot.isEmpty) {
                emit(Result.Error(Exception("Aucun document trouvé avec codeCaisse = ${caisse.codeCaisse}")))
                return@flow
            }

            // Récupérer la référence du document (ID auto-généré)
            val docRef = querySnapshot.documents[0].reference
            docRef.set(caisse).await()

            emit(Result.Succes(Unit))
        } catch (ex: FirebaseFirestoreException) {
            emit(Result.Error(ex))
            Log.e(TAG, "FirebaseFirestoreException: ${ex.message}", ex)
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, "Exception: ${ex.message}", ex)
        }

    }

    override suspend fun delete(caisse: Caisse): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {

            if (caisse.codeCaisse.isBlank()) {
                emit(Result.Error(IllegalArgumentException("Code caisse n'est peut pas etre vide")))
                return@flow
            }

            val querySnapshot = db.collection("caisses")
                .whereEqualTo("codeCaisse", caisse.codeCaisse)
                .get(Source.DEFAULT) // Use DEFAULT to allow cached data in offline mode
                .await()

            if (querySnapshot.isEmpty) {
                emit(Result.Error(Exception("Aucun document trouvé avec codeCaisse = ${caisse.codeCaisse}")))
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

    override suspend fun getCaisseByCode(codeCaisse: String): Flow<Result<Caisse>> = flow {


        emit(Result.Loading)
        try {


            val docRef = db.collection("caisses").document(codeCaisse)
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                val caisse = snapshot.toObject(Caisse::class.java)
                emit(Result.Succes(caisse))
            } else {
                val ex = Exception("Ce document n'existe pas")
                emit(Result.Error(ex))
                Log.e(TAG, ex.message.toString())

            }


        } catch (ex: FirebaseFirestoreException) {
            emit(Result.Error(ex))
            Log.e(TAG, ex.message.toString())

        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, ex.message.toString())
        }


    }

    override suspend fun getAllCaisse(): Flow<Result<List<Caisse>>> = callbackFlow {

        trySend(Result.Loading)
        val listener = db.collection("caisses")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val caisses = snapshot.toObjects(Caisse::class.java)
                    val caisseSortedByDate = caisses.sortedByDescending { it.date }
                    trySend(Result.Succes(caisseSortedByDate))
                }


            }
        awaitClose { listener.remove() }
    }

    override suspend fun getCaisseByCompte(codeCompte: String): Flow<Result<List<Caisse>>> = flow {


        emit(Result.Loading)

        try {

            val snapshot = db.collection("caisses").whereEqualTo("compte", codeCompte).get().await()
            val caisses = snapshot.toObjects(Caisse::class.java)
            emit(Result.Succes(caisses))

        } catch (ex: FirebaseFirestoreException) {

            emit(Result.Error(ex))
            Log.e(TAG, ex.message.toString())
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(TAG, ex.message.toString())
        }

    }

    override suspend fun getCaisseByType(typeCaisse: TYPECAISSE): Flow<Result<List<Caisse>>> =
        callbackFlow {

            trySend(Result.Loading)
            val listener = db.collection("caisses")
                .whereEqualTo("typeCaisse", typeCaisse).addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        trySend(Result.Error(error))
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val caisses = snapshot.toObjects(Caisse::class.java)
                        val caisseSortedByDate = caisses.sortedByDescending { it.date }
                        trySend(Result.Succes(caisseSortedByDate))
                    }


                }
            awaitClose { listener.remove() }


        }

    override suspend fun getCaisseByDate(date: String): Flow<Result<List<Caisse>>> = flow {}

    override suspend fun getCaisseByMontant(montant: Double): Flow<Result<List<Caisse>>> = flow {}
    override suspend fun saveTot(total: Double): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        try {

            db.collection("caisses_tot").document("")


        } catch (e: Exception) {

        }

    }

    override suspend fun getTot(devises: String): Flow<Result<Double>> {
        TODO("Not yet implemented")
    }
}