package com.example.sawaapplication.screens.authentication.domain.useCases

import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val authRepository: AuthRepository){

      operator fun invoke() = authRepository.logOut()

}