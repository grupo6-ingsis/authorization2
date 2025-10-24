package org.gudelker.authorization.security.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByUserIdAndSnippetId(
        userId: String,
        snippetId: String,
    ): Permission?
}
