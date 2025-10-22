package org.gudelker.authorization.security.permission

import org.gudelker.authorization.security.permission.dto.AuthorizeRequestDto
import org.gudelker.authorization.security.permission.dto.AuthorizeResponseDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/permissions")
class PermissionController (private val permissionService: PermissionService) {

    @PostMapping("/authorize")
    fun authorize(
        @RequestBody request: AuthorizeRequestDto
    ): AuthorizeResponseDto {
        return permissionService.authorize(
            request.userId,
            request.snippetId,
            request.permissions
        )
    }

}