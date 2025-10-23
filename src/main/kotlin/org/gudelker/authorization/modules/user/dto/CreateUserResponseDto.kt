// src/main/kotlin/org/gudelker/authorization/modules/user/CreateUserResponseDto.kt
package org.gudelker.authorization.modules.user

import java.util.UUID

data class CreateUserResponseDto(
    val id: UUID,
    val auth0Id: String,
    val email: String?,
    val roleIds: Set<UUID>
)
