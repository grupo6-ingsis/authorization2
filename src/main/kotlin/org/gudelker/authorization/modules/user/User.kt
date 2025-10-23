package org.gudelker.authorization.modules.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.gudelker.authorization.modules.role.Role
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(unique = true, nullable = false)
    val auth0Id: String,

    @Column(nullable = false)
    var email: String? = null,

    @Column(nullable = false)
    var username: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
)
