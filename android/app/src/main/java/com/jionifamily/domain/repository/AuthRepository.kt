package com.jionifamily.domain.repository

import com.jionifamily.domain.model.User
import com.jionifamily.util.Result

interface AuthRepository {
    suspend fun login(avatarKey: String, pin: String): Result<User>
    suspend fun autoLogin(): Result<User>
    suspend fun logout()
    suspend fun changePin(oldPin: String, newPin: String): Result<Unit>
}
