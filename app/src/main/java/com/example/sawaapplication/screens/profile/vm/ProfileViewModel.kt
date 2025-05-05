package com.example.sawaapplication.screens.profile.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> get() = _userEmail

    init {
        getUserData()
    }

    private fun getUserData() {
        val user = firebaseAuth.currentUser
        _userName.value = user?.displayName
        _userEmail.value = user?.email
    }
}