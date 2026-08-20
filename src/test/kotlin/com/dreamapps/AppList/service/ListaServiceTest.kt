package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.AuthProvider
import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.repository.ListaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.Optional

class ListaServiceTest {

    private lateinit var listaRepository: ListaRepository
    private lateinit var listaService: ListaService

    private val user1 = Usuario(
        userId = "user-1",
        email = "user1@example.com",
        authProvider = AuthProvider.LOCAL
    ).apply { setUsername("user1") }

    private val user2 = Usuario(
        userId = "user-2",
        email = "user2@example.com",
        authProvider = AuthProvider.LOCAL
    ).apply { setUsername("user2") }

    @BeforeEach
    fun setUp() {
        listaRepository = mock(ListaRepository::class.java)
        listaService = ListaService(listaRepository)
    }

    @Test
    fun `obtenerTodasLasListas should only return lists for given user`() {
        val listaUser1 = Lista(listCod = "list-1", listName = "Lista 1", user = user1)
        `when`(listaRepository.findByUserAndListActiveTrueOrderByListOrderAsc(user1))
            .thenReturn(listOf(listaUser1))

        val result = listaService.obtenerTodasLasListas(user1)

        assertEquals(1, result.size)
        assertEquals("Lista 1", result[0].listName)
        verify(listaRepository).findByUserAndListActiveTrueOrderByListOrderAsc(user1)
    }

    @Test
    fun `crearLista should assign authenticated user to list`() {
        val nuevaLista = Lista(listName = "Mi nueva lista")
        `when`(listaRepository.save(nuevaLista)).thenReturn(nuevaLista)

        val result = listaService.crearLista(user1, nuevaLista)

        assertEquals(user1, result.user)
        assertTrue(result.listActive)
        verify(listaRepository).save(nuevaLista)
    }

    @Test
    fun `actualizarLista should throw exception if list does not belong to user`() {
        val listaActualizada = Lista(listName = "Nombre modificado")
        `when`(listaRepository.findByListCodAndUser("list-1", user2))
            .thenReturn(Optional.empty())

        val exception = assertThrows(IllegalArgumentException::class.java) {
            listaService.actualizarLista(user2, "list-1", listaActualizada)
        }

        assertTrue(exception.message!!.contains("no pertenece al usuario"))
    }
}
