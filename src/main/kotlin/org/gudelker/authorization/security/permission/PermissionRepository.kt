package org.gudelker.authorization.security.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PermissionRepository : JpaRepository<Permission, UUID> {
    fun findByUserIdAndSnippetId(
        userId: String,
        snippetId: String,
    ): Permission?
}
