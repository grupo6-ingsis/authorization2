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
        val newPermission =
            Permission(
                userId = userId,
                snippetId = snippetId,
                permission = permission,
            )
        permissionRepository.save(newPermission)
        return AuthorizeResponseDto(
            success = true,
            message = "Permission assigned successfully.",
            permission = newPermission.permission.value,
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

    fun getSnippetsByAccessType(
        userId: String,
        accessType: PermissionType,
    ): List<String> {
        val permissions = permissionRepository.findAll()
        return permissions.filter {
            it.userId == userId && it.permission == accessType
        }.map { it.snippetId }
    }
}
