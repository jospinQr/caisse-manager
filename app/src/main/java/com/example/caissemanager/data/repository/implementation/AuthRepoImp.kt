package com.example.caissemanager.data.repository.implementation

import android.nfc.Tag
import android.util.Log
import com.example.caissemanager.data.repository.AuthRepository
import com.example.caissemanager.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepoImp @Inject constructor(private val auth: FirebaseAuth) : AuthRepository {


    val Tag = "AuthRepoImp"
    override val currentUser: Flow<Result<FirebaseUser>>
        get() = flow {
            emit(Result.Loading)
            try {
                val user = auth.currentUser
                emit(Result.Succes(user))
            } catch (ex: FirebaseAuthException) {
                emit(Result.Error(ex))
            } catch (ex: Exception) {
                emit(Result.Error(ex))
            }

        }

    override suspend fun login(email: String, passWord: String): Flow<Result<Unit>> = flow {

        emit(Result.Loading)
        try {
            auth.signInWithEmailAndPassword(email, passWord).await()
            emit(Result.Succes(Unit))
            Log.i(Tag, "login: $email ")
        } catch (ex: FirebaseAuthException) {

            emit(Result.Error(ex))
            Log.e(Tag, " Firebase ex $ex")
        } catch (ex: Exception) {
            emit(Result.Error(ex))
            Log.e(Tag, " Generic ex: $ex ")
        }
    }

}