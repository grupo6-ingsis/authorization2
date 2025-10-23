package org.gudelker.authorization.modules.user

import org.gudelker.authorization.modules.role.Role
import org.gudelker.authorization.modules.role.RoleRepository
import org.gudelker.authorization.modules.user.input.CreateUserInput
import org.gudelker.authorization.modules.user.input.UpdateUserInput
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: UUID): User =
        userRepository.findById(id).orElseThrow { RuntimeException("User not found") }

    fun getUserByAuth0Id(auth0Id: String): User =
        userRepository.findByAuth0Id(auth0Id) ?: throw RuntimeException("User not found")

    @Transactional
    fun createUser(input: CreateUserInput): CreateUserResponseDto {
        if (userRepository.existsByAuth0Id(input.auth0Id)) {
            throw RuntimeException("User with auth0Id already exists")
        }
        val assignedRoles = resolveAssignedRoles(input.roleIds)
        val user = User(
            auth0Id = input.auth0Id,
            email = input.email,
            roles = assignedRoles.toMutableSet()
        )
        val savedUser = userRepository.save(user)
        return toCreateUserResponseDto(savedUser)
    }

    private fun resolveAssignedRoles(roleIds: Set<UUID>?): Set<Role> =
        if (roleIds.isNullOrEmpty()) {
            setOf(roleRepository.findByRole("DEVELOPER") ?: throw RuntimeException("Default role not found"))
        } else {
            roleRepository.findAllById(roleIds).toSet()
        }

    private fun toCreateUserResponseDto(user: User): CreateUserResponseDto =
        CreateUserResponseDto(
            id = user.id!!,
            auth0Id = user.auth0Id,
            email = user.email,
            roleIds = user.roles.mapNotNull { it.id }.toSet()
        )



    @Transactional
    fun updateUser(id: UUID, input: UpdateUserInput): UpdateUserResponse {
        val user = getUserById(id)
        user.username = input.username
        userRepository.save(user)
        return toUpdateUserResponse(user)
    }

    private fun toUpdateUserResponse(user: User): UpdateUserResponse =
        UpdateUserResponse(
            id = user.id!!,
            auth0Id = user.auth0Id,
            email = user.email,
            username = user.username,
            roleIds = user.roles.mapNotNull { it.id }.toSet()
        )



    @Transactional
    fun deleteUser(id: UUID) {
        val user = getUserById(id)
        userRepository.delete(user)
    }
}
