package com.example.sawaapplication.screens.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.authentication.data.remote.FirebaseAuthDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> get() = _userEmail

    private val _aboutMe = MutableStateFlow<String?>(null)
    val aboutMe: StateFlow<String?> get() = _aboutMe

    init {
        getUserData()
    }

    private fun getUserData() {
        val user = firebaseAuth.currentUser
        _userName.value = user?.displayName
        _userEmail.value = user?.email
        user?.let {
            fetchAboutMe(it.uid)
        }
    }

    private fun fetchAboutMe(userId : String){
        val userRef = FirebaseFirestore.getInstance().collection("User").document(userId)
        userRef.get().addOnSuccessListener{ document ->
            if(document != null && document.exists()) {
                _aboutMe.value = document.getString("aboutMe") ?: ""
            }
        }
    }

     fun updateAboutMe(newAboutMe: String){
        viewModelScope.launch {
            firebaseAuthDataSource.updateUserInfo(newAboutMe)
            _aboutMe.value = newAboutMe
        }
    }
}