package com.jionifamily.data.repository

import com.jionifamily.data.local.DataStoreManager
import com.jionifamily.data.remote.api.AuthApi
import com.jionifamily.data.remote.api.UserApi
import com.jionifamily.data.remote.dto.ChangePinRequest
import com.jionifamily.data.remote.dto.LoginRequest
import com.jionifamily.domain.model.User
import com.jionifamily.domain.model.UserRole
import com.jionifamily.domain.repository.AuthRepository
import com.jionifamily.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val dataStore: DataStoreManager,
) : AuthRepository {

    override suspend fun login(avatarKey: String, pin: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(avatarKey, pin))
            if (!response.isSuccessful) {
                return Result.Error("PIN이 올바르지 않아요")
            }
            val tokens = response.body()!!
            dataStore.saveTokens(tokens.accessToken, tokens.refreshToken)

            val meResponse = userApi.getMe()
            if (!meResponse.isSuccessful) {
                return Result.Error("사용자 정보를 불러올 수 없어요")
            }
            val me = meResponse.body()!!
            val user = User(
                id = me.id,
                name = me.name,
                role = UserRole.fromString(me.role),
                avatarKey = me.avatarKey,
                coinBalance = me.coinBalance,
            )
            dataStore.saveUserInfo(user.id, user.name, me.role, user.avatarKey)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("네트워크 오류가 발생했어요")
        }
    }

    override suspend fun autoLogin(): Result<User> {
        return try {
            val token = dataStore.getAccessToken() ?: return Result.Error("로그인이 필요해요")
            val response = userApi.getMe()
            if (!response.isSuccessful) {
                return Result.Error("토큰이 만료되었어요")
            }
            val me = response.body()!!
            val user = User(
                id = me.id,
                name = me.name,
                role = UserRole.fromString(me.role),
                avatarKey = me.avatarKey,
                coinBalance = me.coinBalance,
            )
            dataStore.saveUserInfo(user.id, user.name, me.role, user.avatarKey)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("네트워크 오류가 발생했어요")
        }
    }

    override suspend fun logout() {
        dataStore.clearAll()
    }

    override suspend fun changePin(oldPin: String, newPin: String): Result<Unit> {
        return try {
            val response = authApi.changePin(ChangePinRequest(oldPin, newPin))
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("현재 PIN이 올바르지 않아요")
        } catch (e: Exception) {
            Result.Error("네트워크 오류가 발생했어요")
        }
    }
}
