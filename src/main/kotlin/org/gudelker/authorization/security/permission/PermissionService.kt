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
        permission: String,
    ): AuthorizeResponseDto {
        val permissionType = PermissionType.entries.find { it.value == permission }

        if (permissionType == null) {
            return AuthorizeResponseDto(
                success = false,
                message = "There was not a valid permission to assign.",
                permission = null,
            )
        }

        val existing = permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        if (existing != null) {
            permissionRepository.delete(existing)
            permissionRepository.flush()
        }

        val newPermission =
            Permission(
                userId = userId,
                snippetId = snippetId,
                permission = permissionType,
            )
        permissionRepository.save(newPermission)

        return AuthorizeResponseDto(
            success = true,
            message = "Permission assigned successfully.",
            permission = permissionType.value,
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
        return permission == PermissionType.WRITE || permission == PermissionType.OWNER
    }

    fun canUserWriteSnippet(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = getPermissionForSnippet(snippetId, userId)
        return permission == PermissionType.WRITE || permission == PermissionType.OWNER
    }
}
