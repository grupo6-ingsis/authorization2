package org.gudelker.authorization.security.dto

data class AuthorizeResponseDto(
    val success: Boolean,
    val message: String,
    val permission: String? = null,
)
