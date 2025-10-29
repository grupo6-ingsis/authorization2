package org.gudelker.authorization.security.permission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Transient
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
    @Column(nullable = false, name = "permissions")
    private var permissionsString: String = "",
) {
    @get:Transient
    var permissions: List<PermissionType>
        get() =
            if (permissionsString.isBlank()) {
                emptyList()
            } else {
                permissionsString.split(",").mapNotNull { str ->
                    PermissionType.entries.find { it.value == str.trim() }
                }
            }
        set(value) {
            permissionsString = value.joinToString(",") { it.value }
        }

    // Constructor secundario
    constructor(
        userId: String,
        snippetId: String,
        permissions: List<PermissionType>,
    ) : this(
        id = UUID.randomUUID(),
        userId = userId,
        snippetId = snippetId,
        permissionsString = permissions.joinToString(",") { it.value },
    )
}

enum class PermissionType(val value: String) {
    READ("READ"),
    WRITE("WRITE"),
    OWNER("OWNER"),
}
