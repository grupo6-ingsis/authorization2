package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.dto.AuthorizeResponseDto
import org.gudelker.authorization.security.input.AuthorizeRequestDto
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(PermissionController::class.java)

    @PostMapping("/authorize/{snippetId}")
    fun authorize(
        @PathVariable snippetId: String,
        @RequestBody request: AuthorizeRequestDto,
        @AuthenticationPrincipal jwt: Jwt,
    ): AuthorizeResponseDto {
        logger.info(
            "Authorization request received. UserId: {}, SnippetId: {}, Permission: {}",
            request.userId,
            snippetId,
            request.permission,
        )

        val response =
            permissionService.authorize(
                request.userId,
                snippetId,
                request.permission,
            )

        logger.info(
            "Authorization completed. Permission: {}, UserId: {}, SnippetId: {}",
            response.permission,
            request.userId,
            snippetId,
        )
        return response
    }

    @GetMapping("/{snippetId}")
    fun getPermissionForSnippet(
        @PathVariable snippetId: String,
        @RequestParam userId: String,
    ): PermissionType? {
        logger.info("Fetching permission for User: {}, Snippet: {}", userId, snippetId)

        val permission = permissionService.getPermissionForSnippet(userId, snippetId)

        if (permission != null) {
            logger.info("Permission found: {} for User: {}, Snippet: {}", permission, userId, snippetId)
        } else {
            logger.warn("No permission found for User: {}, Snippet: {}", userId, snippetId)
        }

        return permission
    }

    @GetMapping("/authorize-update/{snippetId}")
    fun authorizeUpdate(
        @PathVariable snippetId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): Boolean {
        logger.info("Authorizing update for JwtId: {}, Snippet: {}", jwt.id, snippetId)
        val authorized = permissionService.authorizeUpdate(jwt.id, snippetId)
        logger.info(
            "Update authorization result: {} for JwtId: {}, Snippet: {}",
            authorized,
            jwt.id,
            snippetId,
        )

        return authorized
    }

    @PostMapping("/can-write/{snippetId}")
    fun canUserWriteSnippet(
        @PathVariable snippetId: String,
        @RequestHeader("X-User-Id") userId: String,
    ): Boolean {
        logger.info("Checking write permission for User: {}, Snippet: {}", userId, snippetId)
        val canWrite = permissionService.canUserWriteSnippet(snippetId, userId)
        logger.info(
            "Write permission check result: {} for User: {}, Snippet: {}",
            canWrite,
            userId,
            snippetId,
        )
        return canWrite
    }

    @GetMapping("/snippetsByAccessType")
    fun getSnippetsByAccessType(
        @RequestParam userId: String,
        @RequestParam accessType: AccessType,
    ): List<String> {
        logger.info("Fetching snippets by access type. User: {}, AccessType: {}", userId, accessType)
        val snippets = permissionService.getSnippetsByAccessType(userId, accessType)
        logger.info(
            "Found {} snippets for User: {}, AccessType: {}",
            snippets.size,
            userId,
            accessType,
        )
        return snippets
    }

    @GetMapping("/users-with-access/{snippetId}")
    fun getUsersWithAccess(
        @PathVariable snippetId: String,
    ): List<String> {
        logger.info("Fetching users with access to Snippet: {}", snippetId)
        val users = permissionService.getUsersWithPermissionForSnippet(snippetId)
        logger.info("Found {} users with access to Snippet: {}", users.size, snippetId)
        return users
    }
}
