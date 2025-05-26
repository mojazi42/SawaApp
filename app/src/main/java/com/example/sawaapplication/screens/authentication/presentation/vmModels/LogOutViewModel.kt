package com.example.sawaapplication.screens.authentication.presentation.vmModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sawaapplication.core.sharedPreferences.AuthPreferences
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.authentication.domain.useCases.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogOutViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val preferenceManager: AuthPreferences,

): ViewModel(){

    private val _logoutState = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val logoutState: StateFlow<LogoutUiState> = _logoutState.asStateFlow()

     fun preformLogOut(navController: NavController){
         viewModelScope.launch{
             _logoutState.value = LogoutUiState.Loading
             try{
                 logOutUseCase.invoke()
                 preferenceManager.clearToken()
                 navController.navigate(Screen.Login.route){
                     popUpTo(0) { inclusive = true } // This clears the whole backstack
                     launchSingleTop = true          // Prevents duplicate LoginScreen
                     _logoutState.value = LogoutUiState.Success
                 }
             } catch(t: Throwable){
                 _logoutState.value = LogoutUiState.Error(
                     t.message ?: "An error occurred during logout" //STRINGS
                 )
             }
         }
    }

}

sealed class LogoutUiState {
    object Idle    : LogoutUiState()
    object Loading : LogoutUiState()
    object Success : LogoutUiState()
    data class Error(val message: String) : LogoutUiState()
}