package dev.johnoreilly.confetti.wear.sessiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.confetti.ConfettiRepository
import dev.johnoreilly.confetti.fragment.SessionDetails
import dev.johnoreilly.confetti.navigation.SessionDetailsKey
import dev.johnoreilly.confetti.utils.DateService
import dev.johnoreilly.confetti.wear.sessiondetails.navigation.SessionDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

class SessionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    repository: ConfettiRepository,
    val formatter: DateService
) : ViewModel() {
    val timeZone: TimeZone = repository.timeZone
    private val sessionId: SessionDetailsKey =
        SessionDetailsDestination.fromNavArgs(savedStateHandle)

    val session: StateFlow<SessionDetails?> = flow {
        val resultsFlow = repository.sessionDetails(sessionId.conference, sessionId.sessionId)

        emitAll(resultsFlow.map {  it?.data?.session?.sessionDetails })
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}