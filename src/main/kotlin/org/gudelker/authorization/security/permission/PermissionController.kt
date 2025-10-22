package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.permission.dto.AuthorizeRequestDto
import org.gudelker.authorization.security.permission.dto.AuthorizeResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/permissions")
class PermissionController (private val permissionService: PermissionService) {

    @PostMapping("/authorize/{snippetId}")
    fun authorize(
        @PathVariable snippetId: String,
        @RequestBody request: AuthorizeRequestDto
    ): AuthorizeResponseDto {
        return permissionService.authorize(
            request.userId,
            snippetId,
            request.permissions
        )
    }

    @GetMapping("/{snippetId}")
    fun getPermissionsForSnippet(
        @PathVariable snippetId: String,
        userId: String
    ): List<PermissionType> {
        return permissionService.getPermissionsForSnippet(userId, snippetId)
    }

}