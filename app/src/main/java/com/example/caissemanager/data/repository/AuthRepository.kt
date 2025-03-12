package com.example.caissemanager.data.repository

import com.example.caissemanager.utils.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {


    val currentUser: Flow<Result<FirebaseUser>>

   suspend fun  login(email: String, passWord: String): Flow<Result<Unit>>


}