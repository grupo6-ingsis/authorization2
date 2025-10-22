package org.gudelker.authorization.security.permission.dto

data class AuthorizeRequestDto(
    val userId: String,
    val snippetId: String,
    val permissions: List<String>
)
