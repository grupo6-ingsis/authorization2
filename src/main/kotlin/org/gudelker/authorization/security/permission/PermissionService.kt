package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.permission.dto.AuthorizeResponseDto
import org.springframework.stereotype.Service

@Service
class PermissionService (private val permissionRepository: PermissionRepository) {
    fun authorize(
        userId: String,
        snippetId: String,
        permissions: List<String>
    ): AuthorizeResponseDto {
        val permissionTypes = permissions.mapNotNull { perm ->
            PermissionType.entries.find { it.value == perm }
        }

        if (permissionTypes.isEmpty()) {
            return AuthorizeResponseDto(
                success = false,
                message = "There were not valid permissions to assign.",
                permissions = null
            )
        }

        val existingPermission = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)

        val permission = if (existingPermission != null) {
            existingPermission.permissions = permissionTypes
            existingPermission
        } else {
            Permission(
                userId = userId,
                snippetId = snippetId,
                permissions = permissionTypes
            )
        }

        permissionRepository.save(permission)

        return AuthorizeResponseDto(
            success = true,
            message = "Permissions assigned successfully.",
            permissions = permissionTypes.map { it.value }
        )
    }

    fun getPermissionsForSnippet(snippetId: String, userId: String): List<PermissionType> {
        val permission = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permission?.permissions?.map { it } ?: emptyList()
    }
}