package dev.johnoreilly.confetti.auto.bookmarks

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.SectionedItemList
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import dev.johnoreilly.confetti.BookmarksViewModel
import dev.johnoreilly.confetti.R
import dev.johnoreilly.confetti.SessionsViewModelParams
import dev.johnoreilly.confetti.auth.User
import dev.johnoreilly.confetti.auto.sessions.details.SessionDetailsScreen
import dev.johnoreilly.confetti.auto.utils.formatDateTime
import dev.johnoreilly.confetti.fragment.SessionDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime
import org.koin.core.parameter.parametersOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarksScreen(
    carContext: CarContext,
    private val user: User?,
    private val conference: String,
) : Screen(carContext), KoinComponent {

    private val bookmarksViewModel: BookmarksViewModel by inject(
        parameters = { parametersOf(SessionsViewModelParams(conference, user?.uid, user)) }
    )

    private val bookmarksState = bookmarksViewModel.upcomingSessions.onEach {
        invalidate()
    }.stateIn(lifecycleScope, SharingStarted.Eagerly, null)

    override fun onGetTemplate(): Template {
        val bookmarks = bookmarksState.value

        val loading = bookmarks == null

        val listBuilder = createBookmarksList(bookmarks)
        return listBuilder.apply {
            setTitle(carContext.getString(R.string.bookmarks))
            setHeaderAction(Action.BACK)
            setLoading(loading)

            if (bookmarks?.isEmpty() == true) {
                setSingleList(ItemList.Builder().build())
            }
        }.build()
    }

    private fun createBookmarksList(bookmarks: Map<LocalDateTime, List<SessionDetails>>?): ListTemplate.Builder {
        val listTemplate = ListTemplate.Builder()
        bookmarks?.forEach { (startTime, sessions) ->
            val listBuilder = ItemList.Builder()

            sessions.forEach { session ->
                listBuilder.addItem(
                    Row.Builder()
                        .setTitle(session.title)
                        .addText(session.speakers.map { it.speakerDetails.name }.toString())
                        .setOnClickListener { screenManager.push(
                            SessionDetailsScreen(
                                carContext,
                                conference,
                                user,
                                session
                            )
                        ) }
                        .build()
                )
            }

            listTemplate.addSectionedList(
                SectionedItemList.create(
                    listBuilder.build(),
                    formatDateTime(startTime)
                )
            )
        }

        return listTemplate
    }
}