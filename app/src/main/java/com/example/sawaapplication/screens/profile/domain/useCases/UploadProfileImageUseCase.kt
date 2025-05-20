package com.example.sawaapplication.screens.profile.domain.useCases

import android.net.Uri
import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val repository: ProfileRepository
)  {
    suspend operator fun invoke(uri: Uri): Boolean {
        return repository.uploadProfileImage(uri)
    }
}
