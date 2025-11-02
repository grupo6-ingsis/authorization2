package org.gudelker.authorization.security.permission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
class Permission(
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    val id: UUID? = null,
    @Column(nullable = false)
    val userId: String = "",
    @Column(nullable = false)
    val snippetId: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var permission: PermissionType,
)

enum class PermissionType(val value: String) {
    READ("READ"),
    WRITE("WRITE"),
    OWNER("OWNER"),
}
