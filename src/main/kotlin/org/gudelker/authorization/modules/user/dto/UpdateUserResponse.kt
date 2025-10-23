package org.gudelker.authorization.modules.user

import java.util.UUID

data class UpdateUserResponse(
    val id: UUID,
    val auth0Id: String,
    val email: String?,
    val username: String?,
    val roleIds: Set<UUID>
)
