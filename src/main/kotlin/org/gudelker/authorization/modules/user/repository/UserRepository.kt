package org.gudelker.authorization.modules.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByAuth0Id(auth0Id: String): User?
    fun existsByAuth0Id(auth0Id: String): Boolean
}
