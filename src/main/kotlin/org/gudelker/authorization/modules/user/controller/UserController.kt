package org.gudelker.authorization.modules.user

import org.gudelker.authorization.modules.role.RoleRepository
import org.gudelker.authorization.modules.user.input.CreateUserInput
import org.gudelker.authorization.modules.user.input.UpdateUserInput
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getAllUsers() = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID) = userService.getUserById(id)

    @PostMapping
    fun createUser(@RequestBody input: CreateUserInput): CreateUserResponseDto {
        return userService.createUser(input)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody input: UpdateUserInput): UpdateUserResponse {
        return userService.updateUser(id, input)

    }


    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
