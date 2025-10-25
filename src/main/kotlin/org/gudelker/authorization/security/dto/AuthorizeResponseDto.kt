package org.gudelker.authorization.security.dto

data class AuthorizeResponseDto(
    val success: Boolean,
    val message: String,
    val permissions: List<String>? = null,
)
