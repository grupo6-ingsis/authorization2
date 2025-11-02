package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.dto.AuthorizeResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PermissionService(private val permissionRepository: PermissionRepository) {
    @Transactional
    fun authorize(
        userId: String,
        snippetId: String,
        permission: PermissionType,
    ): AuthorizeResponseDto {
        val existing = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)

        val savedPermission =
            if (existing != null) {
                // MODIFICA la entidad existente (mismo id)
                existing.permission = permission
                permissionRepository.save(existing) // Hace UPDATE, no INSERT
            } else {
                // Crea nueva entidad con nuevo id
                val newPermission =
                    Permission(
                        userId = userId,
                        snippetId = snippetId,
                        permission = permission,
                    )
                permissionRepository.save(newPermission) // Hace INSERT
            }

        return AuthorizeResponseDto(
            success = true,
            message = "Permission assigned successfully.",
            permission = savedPermission.permission.value,
        )
    }

    fun getPermissionForSnippet(
        snippetId: String,
        userId: String,
    ): PermissionType? {
        val permission = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permission?.permission
    }

    fun authorizeUpdate(
        userId: String,
        snippetId: String,
    ): Boolean {
        val permission = getPermissionForSnippet(snippetId, userId)
        return permission == PermissionType.WRITE
    }

    fun canUserWriteSnippet(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = getPermissionForSnippet(snippetId, userId)
        return permission == PermissionType.WRITE
    }
}
