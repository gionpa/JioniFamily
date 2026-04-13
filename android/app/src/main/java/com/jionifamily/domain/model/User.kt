package com.jionifamily.domain.model

data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val avatarKey: String,
    val coinBalance: Int = 0,
)

enum class UserRole {
    PARENT, CHILD;

    companion object {
        fun fromString(value: String): UserRole =
            if (value == "child") CHILD else PARENT
    }
}
