package org.gudelker.authorization.security.input

data class AuthorizeRequestDto(
    val userId: String,
    val permissions: List<String>,
)
