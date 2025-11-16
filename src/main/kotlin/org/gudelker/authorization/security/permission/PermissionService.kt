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
        val existingPermission =
            permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        if (existingPermission != null) {
            throw IllegalStateException("Permission already exists for this user and snippet")
        }
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
        accessType: AccessType,
    ): List<String> {
        val permissions = permissionRepository.findAll()
        val userSnippets = permissions.filter { it.userId == userId }
        return when (accessType) {
            AccessType.ALL -> userSnippets.map { it.snippetId }
            AccessType.OWNER ->
                userSnippets.filter { it.permission == PermissionType.WRITE }
                    .map { it.snippetId }
            AccessType.SHARED ->
                userSnippets.filter { it.permission == PermissionType.READ }
                    .map { it.snippetId }
        }
    }
}
