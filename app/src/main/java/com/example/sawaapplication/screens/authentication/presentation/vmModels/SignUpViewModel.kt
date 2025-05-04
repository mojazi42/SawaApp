package com.example.sawaapplication.screens.authentication.presentation.vmModels

import com.example.sawaapplication.screens.authentication.domain.useCases.SignUpUseCase


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(name: String, email: String, password: String, confirmPassword: String) {

        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _authState.value = AuthState.Error("Fields can't be empty")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords don't match")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = signUpUseCase(name, email, password, confirmPassword)
            _authState.value = result.fold(
                onSuccess = { AuthState.Authenticated },
                onFailure = { AuthState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}

