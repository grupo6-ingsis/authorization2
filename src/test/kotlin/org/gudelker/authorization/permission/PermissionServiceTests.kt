package org.gudelker.authorization.permission

import org.gudelker.authorization.security.input.AuthorizeRequestDto
import org.gudelker.authorization.security.permission.AccessType
import org.gudelker.authorization.security.permission.Permission
import org.gudelker.authorization.security.permission.PermissionRepository
import org.gudelker.authorization.security.permission.PermissionService
import org.gudelker.authorization.security.permission.PermissionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PermissionServiceTests {
    private val permissionRepository: PermissionRepository = mock()
    private lateinit var permissionService: PermissionService

    @BeforeEach
    fun setUp() {
        permissionService = PermissionService(permissionRepository)
    }

    @Nested
    inner class AuthorizeTests {
        @Test
        fun `should assign permission when not exists`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(null)
            whenever(
                permissionRepository.save(any<Permission>()),
            ).thenReturn(Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ))

            val response = permissionService.authorize("user1", "snippet1", PermissionType.READ)

            assertTrue(response.success)
            assertEquals("Permission assigned successfully.", response.message)
            assertEquals("READ", response.permission)
        }

        @Test
        fun `should throw when permission already exists`() {
            val existing = Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ)
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(existing)

            val ex =
                assertThrows<IllegalStateException> {
                    permissionService.authorize("user1", "snippet1", PermissionType.READ)
                }
            assertEquals("Permission already exists for this user and snippet", ex.message)
        }
    }

    @Nested
    inner class GetPermissionForSnippetTests {
        @Test
        fun `should return permission type if exists`() {
            val perm = Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.WRITE)
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(perm)

            val result = permissionService.getPermissionForSnippet("snippet1", "user1")
            assertEquals(PermissionType.WRITE, result)
        }

        @Test
        fun `should return null if permission does not exist`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(null)

            val result = permissionService.getPermissionForSnippet("snippet1", "user1")
            org.junit.jupiter.api.Assertions.assertNull(result)
        }
    }

    @Nested
    inner class GetUsersWithPermissionForSnippetTests {
        @Test
        fun `should return users with permission for snippet`() {
            val perms =
                listOf(
                    Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ),
                    Permission(UUID.randomUUID(), "user2", "snippet1", PermissionType.WRITE),
                    Permission(UUID.randomUUID(), "user1", "snippet2", PermissionType.READ),
                )
            whenever(permissionRepository.findAll()).thenReturn(perms)

            val result = permissionService.getUsersWithPermissionForSnippet("snippet1")
            assertTrue(result.containsAll(listOf("user1", "user2")))
            assertFalse(result.contains("user3"))
        }

        @Test
        fun `should return empty list if no permissions for snippet`() {
            whenever(permissionRepository.findAll()).thenReturn(emptyList())

            val result = permissionService.getUsersWithPermissionForSnippet("snippet1")
            assertTrue(result.isEmpty())
        }
    }

    @Nested
    inner class AuthorizeUpdateTests {
        @Test
        fun `should return true if user has WRITE permission`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1"))
                .thenReturn(Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.WRITE))

            assertTrue(permissionService.authorizeUpdate("user1", "snippet1"))
        }

        @Test
        fun `should return false if user does not have WRITE permission`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1"))
                .thenReturn(Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ))

            assertFalse(permissionService.authorizeUpdate("user1", "snippet1"))
        }

        @Test
        fun `should return false if permission does not exist`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(null)

            assertFalse(permissionService.authorizeUpdate("user1", "snippet1"))
        }
    }

    @Nested
    inner class CanUserWriteSnippetTests {
        @Test
        fun `should return true if user has WRITE permission`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1"))
                .thenReturn(Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.WRITE))

            assertTrue(permissionService.canUserWriteSnippet("snippet1", "user1"))
        }

        @Test
        fun `should return false if user does not have WRITE permission`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1"))
                .thenReturn(Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ))

            assertFalse(permissionService.canUserWriteSnippet("snippet1", "user1"))
        }

        @Test
        fun `should return false if permission does not exist`() {
            whenever(permissionRepository.findByUserIdAndSnippetId("user1", "snippet1")).thenReturn(null)

            assertFalse(permissionService.canUserWriteSnippet("snippet1", "user1"))
        }
    }

    @Nested
    inner class GetSnippetsByAccessTypeTests {
        @Test
        fun `should return all snippets for user when access type is ALL`() {
            val perms =
                listOf(
                    Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ),
                    Permission(UUID.randomUUID(), "user1", "snippet2", PermissionType.WRITE),
                    Permission(UUID.randomUUID(), "user2", "snippet3", PermissionType.READ),
                )
            whenever(permissionRepository.findAll()).thenReturn(perms)

            val result = permissionService.getSnippetsByAccessType("user1", AccessType.ALL)
            assertTrue(result.containsAll(listOf("snippet1", "snippet2")))
            assertFalse(result.contains("snippet3"))
        }

        @Test
        fun `should return only WRITE snippets for user when access type is OWNER`() {
            val perms =
                listOf(
                    Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ),
                    Permission(UUID.randomUUID(), "user1", "snippet2", PermissionType.WRITE),
                )
            whenever(permissionRepository.findAll()).thenReturn(perms)

            val result = permissionService.getSnippetsByAccessType("user1", AccessType.OWNER)
            assertEquals(listOf("snippet2"), result)
        }

        @Test
        fun `should return only READ snippets for user when access type is SHARED`() {
            val perms =
                listOf(
                    Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ),
                    Permission(UUID.randomUUID(), "user1", "snippet2", PermissionType.WRITE),
                )
            whenever(permissionRepository.findAll()).thenReturn(perms)

            val result = permissionService.getSnippetsByAccessType("user1", AccessType.SHARED)
            assertEquals(listOf("snippet1"), result)
        }

        @Test
        fun `should return empty list if user has no permissions`() {
            whenever(permissionRepository.findAll()).thenReturn(emptyList())

            val result = permissionService.getSnippetsByAccessType("user1", AccessType.ALL)
            assertTrue(result.isEmpty())
        }
    }

    @Nested
    inner class AuthorizeRequestDtoUsageTests {
        @Test
        fun `should create valid AuthorizeRequestDto and use in service`() {
            val dto = AuthorizeRequestDto(userId = "userX", permission = PermissionType.WRITE)
            whenever(permissionRepository.findByUserIdAndSnippetId("userX", "snippetY")).thenReturn(null)
            whenever(
                permissionRepository.save(any<Permission>()),
            ).thenReturn(Permission(UUID.randomUUID(), "userX", "snippetY", PermissionType.WRITE))

            val service = PermissionService(permissionRepository)
            val response = service.authorize(dto.userId, "snippetY", dto.permission)
            assertTrue(response.success)
            assertEquals("WRITE", response.permission)
        }

        @Test
        fun `should throw if AuthorizeRequestDto user already has permission`() {
            val dto = AuthorizeRequestDto(userId = "userZ", permission = PermissionType.READ)
            whenever(
                permissionRepository.findByUserIdAndSnippetId("userZ", "snippetA"),
            ).thenReturn(Permission(UUID.randomUUID(), "userZ", "snippetA", PermissionType.READ))

            val service = PermissionService(permissionRepository)
            assertThrows<IllegalStateException> {
                service.authorize(dto.userId, "snippetA", dto.permission)
            }
        }
    }

    @Nested
    inner class ComplexPermissionServiceCases {
        @Test
        fun `should handle multiple permissions for different users and snippets`() {
            val perms =
                listOf(
                    Permission(UUID.randomUUID(), "user1", "snippet1", PermissionType.READ),
                    Permission(UUID.randomUUID(), "user2", "snippet1", PermissionType.WRITE),
                    Permission(UUID.randomUUID(), "user1", "snippet2", PermissionType.WRITE),
                    Permission(UUID.randomUUID(), "user3", "snippet3", PermissionType.READ),
                )
            whenever(permissionRepository.findAll()).thenReturn(perms)

            val service = PermissionService(permissionRepository)
            val ownerSnippets = service.getSnippetsByAccessType("user1", AccessType.OWNER)
            val sharedSnippets = service.getSnippetsByAccessType("user1", AccessType.SHARED)
            val allSnippets = service.getSnippetsByAccessType("user1", AccessType.ALL)

            assertEquals(listOf("snippet2"), ownerSnippets)
            assertEquals(listOf("snippet1"), sharedSnippets)
            assertTrue(allSnippets.containsAll(listOf("snippet1", "snippet2")))
        }

        @Test
        fun `should return empty for user with no permissions`() {
            whenever(permissionRepository.findAll()).thenReturn(emptyList())
            val service = PermissionService(permissionRepository)
            val result = service.getSnippetsByAccessType("nouser", AccessType.ALL)
            assertTrue(result.isEmpty())
        }
    }
}
