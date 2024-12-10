package frgp.utn.edu.ar.quepasa.presentation.ui.components.rolerequests.user.requests

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import frgp.utn.edu.ar.quepasa.data.model.enums.RequestStatus
import frgp.utn.edu.ar.quepasa.data.model.request.RoleUpdateRequest
import frgp.utn.edu.ar.quepasa.presentation.ui.components.basic.GradientDivider
import frgp.utn.edu.ar.quepasa.presentation.ui.components.rolerequests.card.RoleRequestCardUser
import java.util.UUID

@Composable
fun RoleRequestCards(
    modifier: Modifier,
    title: String,
    status: RequestStatus,
    requests: List<RoleUpdateRequest>,
    hasDeleteButton: Boolean,
    onDeleteRequest: (UUID) -> Unit
) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 6.dp),
        style = MaterialTheme.typography.titleLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )

    GradientDivider(Modifier.fillMaxWidth())

    if(requests.isNotEmpty()) {
        var count = 0
        requests.forEach { request ->
            if(request.status == status) {
                RoleRequestCardUser(
                    request = request,
                    hasDeleteButton = hasDeleteButton,
                    onDeleteRequest = { id -> onDeleteRequest(id) }
                )
                count++
            }
        }
        if(count == 0) {
            Text(modifier = modifier, text = "Sin solicitudes.")
        }
    }
    else {
        Text(modifier = modifier, text = "Sin solicitudes.")
    }

    Spacer(modifier = Modifier.padding(vertical = 16.dp))
}

@Preview
@Composable
fun RoleRequestsPendingPreview() {
    RoleRequestCards(
        modifier = Modifier,
        title = "",
        status = RequestStatus.WAITING,
        requests = emptyList(),
        hasDeleteButton = true,
        onDeleteRequest = {}
    )
}