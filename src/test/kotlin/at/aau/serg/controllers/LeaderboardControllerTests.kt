package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    // Der Test prüfte ursprünglich die Sortierung nach ID bei gleichem Score. Nach der neuen Logik
    // muss er prüfen, ob bei gleichem Score nach timeInSeconds aufsteigend gereiht wird.
    @Test
    fun test_getLeaderboard_sameScore_correctTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_validRank_returnsFullWindow() {
        val first = GameResult(1, "first", 90, 20.0)
        val second = GameResult(2, "second", 80, 10.0)
        val third = GameResult(3, "third", 70, 15.0)
        val fourth = GameResult(4, "fourth", 60, 12.0)
        val fifth = GameResult(5, "fifth", 50, 20.0)
        val sixth = GameResult(6, "sixth", 40, 15.0)
        val seventh = GameResult(7, "seventh", 30, 10.0)
        val eighth = GameResult(8, "eighth", 20, 15.0)
        val ninth = GameResult(9, "ninth", 10, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(third, fifth, first, fourth, second, seventh, ninth, sixth, eighth))

        val res: List<GameResult> = controller.getLeaderboard(5)

        verify(mockedService).getGameResults()
        assertEquals(7, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(fourth, res[2])
        assertEquals(fifth, res[3])
        assertEquals(sixth, res[4])
        assertEquals(seventh, res[5])
        assertEquals(eighth, res[6])
    }

    @Test
    fun test_getLeaderboard_rankAtBeginning_returnsWindowFromBeginning() {
        val first = GameResult(1, "first", 70, 20.0)
        val second = GameResult(2, "second", 60, 10.0)
        val third = GameResult(3, "third", 50, 15.0)
        val fourth = GameResult(4, "fourth", 40, 12.0)
        val fifth = GameResult(5, "fifth", 30, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(fifth, fourth, third, second, first))

        val res: List<GameResult> = controller.getLeaderboard(1)
        verify(mockedService).getGameResults()
        assertEquals(4, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
        assertEquals(fourth, res[3])
    }

    @Test
    fun test_getLeaderboard_rankAtEnd_returnsWindowFromEnd() {
        val first = GameResult(1, "first", 70, 20.0)
        val second = GameResult(2, "second", 60, 10.0)
        val third = GameResult(3, "third", 50, 15.0)
        val fourth = GameResult(4, "fourth", 40, 12.0)
        val fifth = GameResult(5, "fifth", 30, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(fifth, fourth, third, second, first))

        val res: List<GameResult> = controller.getLeaderboard(5)
        verify(mockedService).getGameResults()
        assertEquals(4, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(fourth, res[2])
        assertEquals(fifth, res[3])
    }

    @Test
    fun test_getLeaderboard_rankTooLarge_throwsBadRequest() {
        val first = GameResult(1, "first", 30, 20.0)
        val second = GameResult(2, "second", 20, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(first, second))

        val exception = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(3)
        }
        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_rankZero_throwsBadRequest() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())
        val exception = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(0)
        }
        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_negativeRank_throwsBadRequest() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())

        val exception = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }
}