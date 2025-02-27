package frgp.utn.edu.ar.quepasa.presentation.ui.components.events.card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import frgp.utn.edu.ar.quepasa.R
import frgp.utn.edu.ar.quepasa.data.dto.response.VoteCount
import frgp.utn.edu.ar.quepasa.data.model.Event
import frgp.utn.edu.ar.quepasa.data.model.User

@Composable
fun CardButtonsBar(
    event: Event,
    user: User?,
    voteCount: VoteCount,
    navController: NavHostController,
    assists: Boolean,
    onAssistanceClick: () -> Unit = {},
    onEventAddToCalendar: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
    onUpvoteClick: () -> Unit = {},
    onDownvoteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            CardButton(
                description = "asistir",
                icon = R.drawable.baseline_back_hand_24,
                onClick = {
                    onAssistanceClick()
                },
                tint = if (assists) {
                    MaterialTheme.colorScheme.inversePrimary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        }
        Column {
            CardButton(
                description = "agregar a calendario",
                icon = R.drawable.baseline_calendar_month_24,
                onClick = {
                    onEventAddToCalendar()
                }
            )
        }
        if (user?.username == event.owner?.username || user?.role.toString().contains("ADMIN")) {
            Column {
            CardButton(
                description = "eliminar",
                icon = R.drawable.baseline_delete_24,
                onClick = {
                    onRemoveClick()
                }
            )
        }
            Column {
                CardButton(
                    description = "editar",
                    icon = R.drawable.baseline_edit_document_24,
                    onClick = {
                        navController.navigate("eventEdit/${event.id}")
                    }
                )
            }
        }
        Column {
            UpVoteButton(
                voteCount.votes.toString(),
                onClick = {
                    onUpvoteClick()
                }
            )
        }
        Column {
            CardButton(
                description = "downvote",
                icon = R.drawable.baseline_arrow_downward_24,
                onClick = {
                    onDownvoteClick()
                }
            )
        }
    }
}