package org.gudelker.authorization.security.input

import org.gudelker.authorization.security.permission.PermissionType

data class AuthorizeRequestDto(
    val userId: String,
    val permission: PermissionType,
)
