package com.dreamapps.AppList.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "usuario")
class Usuario(
    @Id
    @Column(name = "user_id", length = 36)
    var userId: String = UuidCreator.getTimeOrderedEpoch().toString(),

    @Column(name = "email", length = 150, unique = true, nullable = false)
    var email: String = "",

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private var username: String = "",

    @JsonIgnore
    @Column(name = "password", length = 255, nullable = true)
    private var password: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 20, nullable = false)
    var authProvider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "provider_id", length = 255, nullable = true)
    var providerId: String? = null,

    @Column(name = "enabled", nullable = false)
    private var enabled: Boolean = true
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getPassword(): String? = password

    fun setPassword(newPassword: String?) {
        this.password = newPassword
    }

    override fun getUsername(): String = username

    fun setUsername(newUsername: String) {
        this.username = newUsername
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled

    fun setEnabled(isEnabled: Boolean) {
        this.enabled = isEnabled
    }
}
