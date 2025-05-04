package com.example.sawaapplication.screens.authentication.presentation.vmModels

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class ValidationInputViewModel : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    private var confirmPassword by mutableStateOf("")
    var emailAndPasswordError by mutableStateOf<String?>(null)
    var fieldsError by mutableStateOf<String?>(null)

    private val passwordPattern = Regex(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    )

    fun validateEmailAndPassword() {
        emailAndPasswordError = when {
            email.isBlank() || password.isBlank() ->
                "Email and password can't be empty"

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Invalid email format"

            else -> null
        }
    }

    private fun validateFields() {
        fieldsError = when {
            name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                "All fields are required"

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Invalid email format"

            !passwordPattern.matches(password) ->
                "Password must be 8+ chars with upper, lower, digit & special char"

            password != confirmPassword ->
                "Passwords do not match"

            else -> null
        }
    }

    fun isFormValid(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        this.name = name
        this.email = email
        this.password = password
        this.confirmPassword = confirmPassword

        validateFields()
        return fieldsError == null
    }
}