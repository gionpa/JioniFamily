package com.jionifamily.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("avatar_key") val avatarKey: String,
    val pin: String,
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String,
)

data class ChangePinRequest(
    @SerializedName("old_pin") val oldPin: String,
    @SerializedName("new_pin") val newPin: String,
)

data class UserResponse(
    val id: String,
    val name: String,
    val role: String,
    @SerializedName("avatar_key") val avatarKey: String,
    @SerializedName("coin_balance") val coinBalance: Int,
)

data class FamilyMemberResponse(
    val id: String,
    val name: String,
    val role: String,
    @SerializedName("avatar_key") val avatarKey: String,
)
