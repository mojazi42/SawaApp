package com.example.sawaapplication.screens.authentication.presentation.vmModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.authentication.domain.useCases.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val forgotPasswordUseCase: ForgotPasswordUseCase) :
    ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    var email = MutableStateFlow("")

    fun forgotPassword() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = forgotPasswordUseCase(email.value.trim())
                result.onSuccess {
                    _authState.value = AuthState.Authenticated
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Reset Password Failed.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unexpected error")
            }

        }
    }
}
