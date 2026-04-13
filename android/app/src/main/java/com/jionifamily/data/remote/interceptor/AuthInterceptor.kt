package com.jionifamily.data.remote.interceptor

import com.jionifamily.data.local.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Skip auth header for login and refresh endpoints
        if (original.url.encodedPath.contains("/auth/login") ||
            original.url.encodedPath.contains("/auth/refresh")
        ) {
            return chain.proceed(original)
        }

        val token = runBlocking { dataStoreManager.getAccessToken() }
        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}
