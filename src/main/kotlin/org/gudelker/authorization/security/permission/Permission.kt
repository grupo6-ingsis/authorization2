package org.gudelker.authorization.security.permission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val userId: String = "",
    @Column(nullable = false)
    val snippetId: String = "",
    @Column(nullable = false)
    var permissions: List<PermissionType> = emptyList(),
)

enum class PermissionType(val value: String) {
    READ("READ"),
    WRITE("WRITE"),
    OWNER("OWNER"),
}
