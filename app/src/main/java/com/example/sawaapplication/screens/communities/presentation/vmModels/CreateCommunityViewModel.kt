//package com.example.sawaapplication.screens.communities.presentation.vmModels
//
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import android.content.Context
//import android.widget.Toast
//import com.example.sawaapplication.screens.communities.domain.useCases.CreateCommunityUseCase
//
//sealed class AuthState {
//    data object Unauthenticated : AuthState()
//    data object Authenticated : AuthState()
//    data object Loading : AuthState()
//    data class Error(val message: String) : AuthState()
//}
//
//@HiltViewModel
//class CreateCommunityViewModel @Inject constructor(
//    private val createCommunityUseCase: CreateCommunityUseCase
//) : ViewModel() {
//
//    var name by mutableStateOf("")
//    var description by mutableStateOf("")
//    var img by mutableStateOf("")
//
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
//    val authState: StateFlow<AuthState> = _authState
//
//    fun createCommunity(name: String, description: String, img: String) {
//
//        if (name.isBlank() || description.isBlank() || img.isBlank()) {
//            _authState.value = AuthState.Error("Fields can't be empty")
//            return
//        }
//
//        _authState.value = AuthState.Loading
//
//        viewModelScope.launch {
//            val result = createCommunityUseCase(name, description, img)
//            _authState.value = result.fold(
//                onSuccess = {
//                    AuthState.Authenticated
//                },
//                onFailure = {
//                    AuthState.Error(it.message ?: "Something went wrong")
//                }
//            )
//
//        }
//    }
//
//    fun handleAuthStateCreateCommunity(
//        authState: AuthState,
//        context: Context,
//        onSuccess: () -> Unit
//    ) {
//        when (authState) {
//            is AuthState.Authenticated -> {
//                Toast.makeText(context, "Created Community Successfully", Toast.LENGTH_SHORT).show()
//                onSuccess()
//            }
//
//            is AuthState.Error -> {
//                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
//            }
//
//            else -> Unit
//        }
//    }
//
//}