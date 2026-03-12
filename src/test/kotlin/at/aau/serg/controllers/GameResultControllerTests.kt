package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when` as whenever
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setUp() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getAllGameResults() {
        val first = GameResult(1, "first", 20, 10.0)
        val second = GameResult(2, "second", 10, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(first, second))

        val res = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(2, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
    }

    @Test
    fun test_getGameResult_returnsCorrectResult() {
        val result = GameResult(1, "first", 50, 10.0)

        whenever(mockedService.getGameResult(1)).thenReturn(result)

        val res = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(result, res)
    }

    @Test
    fun test_addGameResult_callsService() {
        val result = GameResult(1, "first", 20, 10.0)

        controller.addGameResult(result)

        verify(mockedService).addGameResult(result)
    }

    @Test
    fun test_deleteGameResult_callsService() {
        controller.deleteGameResult(1)

        verify(mockedService).deleteGameResult(1)
    }
}