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
        permissions: List<String>,
    ): AuthorizeResponseDto {
        val permissionTypes =
            permissions.mapNotNull { perm ->
                PermissionType.entries.find { it.value == perm }
            }

        if (permissionTypes.isEmpty()) {
            return AuthorizeResponseDto(
                success = false,
                message = "There were not valid permissions to assign.",
                permissions = null,
            )
        }

        val existing = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)

        val mergedPermissions =
            if (existing != null) {
                (existing.permissions + permissionTypes).distinct()
            } else {
                permissionTypes
            }

        if (existing != null) {
            permissionRepository.delete(existing)
            permissionRepository.flush()
        }

        // New entity with merged permissions
        val newPermission =
            Permission(
                userId = userId,
                snippetId = snippetId,
                permissions = mergedPermissions,
            )

        permissionRepository.save(newPermission)

        return AuthorizeResponseDto(
            success = true,
            message = "Permissions assigned successfully.",
            permissions = permissionTypes.map { it.value },
        )
    }

    fun getPermissionsForSnippet(
        snippetId: String,
        userId: String,
    ): List<PermissionType> {
        val permission = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permission?.permissions?.map { it } ?: emptyList()
    }

    fun authorizeUpdate(
        userId: String,
        snippetId: String,
    ): Boolean {
        val permissions = getPermissionsForSnippet(snippetId, userId)
        return permissions.contains(PermissionType.WRITE) || permissions.contains(PermissionType.OWNER)
    }
}
