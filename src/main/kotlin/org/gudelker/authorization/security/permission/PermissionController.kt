package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.dto.AuthorizeResponseDto
import org.gudelker.authorization.security.input.AuthorizeRequestDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/permissions")
class PermissionController(private val permissionService: PermissionService) {
    @PostMapping("/authorize/{snippetId}")
    fun authorize(
        @PathVariable snippetId: String,
        @RequestBody request: AuthorizeRequestDto,
        @AuthenticationPrincipal jwt: Jwt,
    ): AuthorizeResponseDto {
        return permissionService.authorize(
            request.userId,
            snippetId,
            request.permission,
        )
    }

    @GetMapping("/{snippetId}")
    fun getPermissionForSnippet(
        @PathVariable snippetId: String,
        @RequestParam userId: String,
    ): PermissionType? {
        return permissionService.getPermissionForSnippet(snippetId, userId)
    }

    @GetMapping("/authorize-update/{snippetId}")
    fun authorizeUpdate(
        @PathVariable snippetId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): Boolean {
        return permissionService.authorizeUpdate(jwt.id, snippetId)
    }

    @PostMapping("/can-write/{snippetId}")
    fun canUserWriteSnippet(
        @PathVariable snippetId: String,
        @RequestHeader("X-User-Id") userId: String,
    ): Boolean {
        return permissionService.canUserWriteSnippet(snippetId, userId)
    }

    @GetMapping("/snippetsByAccessType")
    fun getSnippetsByAccessType(
        @RequestParam userId: String,
        @RequestParam accessType: PermissionType,
    ): List<String> {
        return permissionService.getSnippetsByAccessType(userId, accessType)
    }
}
