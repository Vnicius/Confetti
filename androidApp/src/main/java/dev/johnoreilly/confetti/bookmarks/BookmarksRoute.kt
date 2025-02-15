package dev.johnoreilly.confetti.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.johnoreilly.confetti.BookmarksViewModel
import dev.johnoreilly.confetti.SessionsViewModelParams
import dev.johnoreilly.confetti.auth.Authentication
import dev.johnoreilly.confetti.sessiondetails.navigation.SessionDetailsKey
import dev.johnoreilly.confetti.ui.ConfettiAppState
import org.koin.androidx.compose.getViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun BookmarksRoute(
    conference: String,
    appState: ConfettiAppState,
    navigateToSession: (SessionDetailsKey) -> Unit,
    onSwitchConference: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
) {
    val user by koinInject<Authentication>().currentUser.collectAsStateWithLifecycle()

    val viewModel = getViewModel<BookmarksViewModel>(parameters = {
        parametersOf(SessionsViewModelParams(conference, user?.uid, user))
    })
    val loading by viewModel
        .loading
        .collectAsStateWithLifecycle(initialValue = true)
    val pastSessions by viewModel
        .pastSessions
        .collectAsStateWithLifecycle(initialValue = emptyMap())
    val upcomingSessions by viewModel
        .upcomingSessions
        .collectAsStateWithLifecycle(initialValue = emptyMap())
    val bookmarks by viewModel
        .bookmarks
        .collectAsStateWithLifecycle(initialValue = emptySet())
    BookmarksView(
        conference = conference,
        appState = appState,
        navigateToSession = navigateToSession,
        onSwitchConference = onSwitchConference,
        onSignIn = onSignIn,
        onSignOut = onSignOut,
        pastSessions = pastSessions,
        upcomingSessions = upcomingSessions,
        bookmarks = bookmarks,
        addBookmark = viewModel::addBookmark,
        removeBookmark = viewModel::removeBookmark,
        loading = loading,
    )
}
