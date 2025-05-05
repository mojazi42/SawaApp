package com.example.sawaapplication.screens.authentication.presentation.vmModels

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class ValidationInputViewModel : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var emailAndPasswordError by mutableStateOf<String?>(null)

    fun validateEmailAndPassword() {
        emailAndPasswordError = when {
            email.isBlank() || password.isBlank() ->
                "Email and password can't be empty"

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Invalid email format"

            else -> null
        }
    }

}