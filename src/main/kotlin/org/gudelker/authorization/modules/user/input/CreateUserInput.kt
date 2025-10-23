// src/main/kotlin/org/gudelker/authorization/modules/user/input/CreateUserInput.kt
package org.gudelker.authorization.modules.user.input

import java.util.UUID

data class CreateUserInput(
    val auth0Id: String,
    val email: String?,
    val roleIds: Set<UUID>? = null
)
