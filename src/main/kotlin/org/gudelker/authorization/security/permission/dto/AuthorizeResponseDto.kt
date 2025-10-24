package org.gudelker.authorization.security.permission.dto

data class AuthorizeResponseDto(
    val success: Boolean,
    val message: String,
    val permissions: List<String>? = null,
)
