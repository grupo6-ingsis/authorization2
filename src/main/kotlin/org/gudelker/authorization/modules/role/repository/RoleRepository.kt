package org.gudelker.authorization.modules.role

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByRole(role: String): Role?
}
