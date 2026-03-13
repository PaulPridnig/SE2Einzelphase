package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {
    /*
2.2.1: Hier wird das GameResult geholt und nach Regeln sortiert. Die Regel soll geändert werden.
Alt: {-it.score }, { it.id }
Neu: {-it.score }, { it.timeInSeconds }, also Spielzeit als 2. Faktor statt Spieler ID
2.2.2: Zusätzlich rank als Query Parameter hinzufügen (optional) und GameResult als val.
*/
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
            val sortedResults =
                gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

            if (rank == null) {
                return sortedResults
            }

            if (rank <= 0 || rank > sortedResults.size) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
            }

            val targetIndex = rank - 1
            val startIndex = maxOf(0, targetIndex - 3)
            val endIndexExclusive = minOf(sortedResults.size, targetIndex + 4)

            return sortedResults.subList(startIndex, endIndexExclusive)
        }

}