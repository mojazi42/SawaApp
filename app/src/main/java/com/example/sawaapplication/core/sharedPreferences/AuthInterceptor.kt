package com.example.sawaapplication.core.sharedPreferences

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenProvider.getToken()
        }

        var request = chain.request()
        request = request.newBuilder()
            .addHeader("authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}