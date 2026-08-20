package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.AuthProvider
import com.dreamapps.AppList.entity.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UsuarioRepository : JpaRepository<Usuario, String> {
    fun findByUsername(username: String): Optional<Usuario>
    fun findByEmail(email: String): Optional<Usuario>
    fun findByUsernameOrEmail(username: String, email: String): Optional<Usuario>
    fun findByProviderIdAndAuthProvider(providerId: String, authProvider: AuthProvider): Optional<Usuario>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
