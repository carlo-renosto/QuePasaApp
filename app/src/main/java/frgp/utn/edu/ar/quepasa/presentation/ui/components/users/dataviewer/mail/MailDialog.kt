package frgp.utn.edu.ar.quepasa.presentation.ui.components.users.dataviewer.mail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import frgp.utn.edu.ar.quepasa.data.model.User
import frgp.utn.edu.ar.quepasa.data.model.auth.Mail
import frgp.utn.edu.ar.quepasa.data.model.enums.Role
import frgp.utn.edu.ar.quepasa.data.model.media.Picture
import frgp.utn.edu.ar.quepasa.domain.context.feedback.LocalFeedback
import frgp.utn.edu.ar.quepasa.presentation.ui.components.users.fields.OtpTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.ZoneId.*
import java.time.format.DateTimeFormatter
import java.util.UUID

fun convertTimestampToFormattedString(timestamp: Timestamp): String {
    val dateTime = timestamp.toInstant()
        .atZone(systemDefault())
        .toLocalDateTime()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return dateTime.format(formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailDialog(
    mail: Mail,
    onDismissRequest: () -> Unit,
    onDeleteRequest: suspend (Mail) -> Unit,
    onValidateRequest: suspend (Mail, String) -> Boolean
) {
    var showDeleteDialog by remember { mutableStateOf<Boolean>(false) }
    var vrs by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val feedback by LocalFeedback.current.collectAsState()
    var mutableFeedback = LocalFeedback.current
    val errorOtp by remember { derivedStateOf<Boolean> { feedback?.field == "mailCodeVerification" } }
    if(mail.verified && mail.verifiedAt != null) {
        vrs = "Verificado el " + convertTimestampToFormattedString(mail.verifiedAt)
    } else vrs = "Agregado el " + convertTimestampToFormattedString(mail.requestedAt)
    ModalBottomSheet(sheetState = rememberModalBottomSheetState(true), onDismissRequest = { onDismissRequest() }) {
        ListItem(
            headlineContent = {
                Text(text = mail.mail, textAlign = Center)
            },
            modifier = Modifier.padding(0.dp),
            supportingContent = {
                Text(text = vrs, textAlign = Center)
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )
        if(!mail.verified) {
            Text(text = "Verificá este correo", textAlign = Center, modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .fillMaxWidth())
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                OtpTextField(modifier = Modifier.padding(vertical = 12.dp),
                    otpText = otp,
                    isError = errorOtp,
                    onClearError = {
                        mutableFeedback.update { null }
                    },
                    onOtpTextChange = { value, ready ->
                        otp = value
                        mutableFeedback.update { null }
                        if(ready) {
                            CoroutineScope(IO).launch {
                                if(onValidateRequest(mail, otp)) {
                                    onDismissRequest()
                                } else {
                                    otp = ""
                                }
                            }
                        }
                    }
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    showDeleteDialog = true
                   //
                },
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text("Eliminar")
            }
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text("Volver")
            }
        }
    }
    if(showDeleteDialog) AlertDialog(
        title = {
            Text("¿Estás seguro?")
        },
        text = {
            Text("Estás a punto de desvincular esta dirección de correo electrónico de tu cuenta de QuePasa. ")
        },
        onDismissRequest = {
            showDeleteDialog = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showDeleteDialog = false
                    CoroutineScope(IO).launch { onDeleteRequest(mail) }
                }
            ) {
                Text("Eliminar")
            }
        }
    )
}

@Preview @Composable
fun MailDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    val user = User(
        name = "Máximo Canedo",
        picture = Picture(
            id = UUID.fromString("c7d6a327-12f4-4b8d-a81f-2059dd340fe7"),
            description = "",
            mediaType = "image/jpeg",
            uploadedAt = Timestamp(1000000),
            owner = null
        ),
        id = 338,
        username = "root",
        active = true,
        address = "",
        role = Role.ADMIN,
        neighbourhood = null,
        email = emptySet(),
        phone = emptySet()
    )
    if(showDialog) MailDialog(mail = Mail(
        mail = "parravicini@gmail.com",
        user = user,
        requestedAt = Timestamp(System.currentTimeMillis() - 102410241024),
        verified = true,
        verifiedAt = Timestamp(System.currentTimeMillis())
    ), { showDialog = false }, {}, { mail, code -> true })
}