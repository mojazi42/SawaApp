package com.example.sawaapplication.screens.authentication.domain.repository

interface AuthRepository {
    suspend fun signUp(name: String, email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String)
    fun logOut()
}