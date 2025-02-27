package frgp.utn.edu.ar.quepasa.presentation.ui.components.main

import BaseComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import frgp.utn.edu.ar.quepasa.data.model.enums.Role
import frgp.utn.edu.ar.quepasa.domain.context.user.LocalAuth
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.PostScreen
import frgp.utn.edu.ar.quepasa.presentation.ui.components.rolerequests.fields.WarningMessage

@Composable
fun MainPage(navController: NavHostController) { // TODO: Change to User (non-nullable) after login is implemented
    val user by LocalAuth.current.collectAsState()

    BaseComponent(navController, "¿Qué pasa?", false, "home") {
        Column(
            modifier = Modifier
        ) {
            if (user.user?.role == Role.USER ) {
                WarningMessage("Tu cuenta se encuentra sin verificar. Para acceder a todas las funcionalidades, verifica tu cuenta en el apartado de usuarios.")
            }
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = "Bienvenido, ${if (user.ok) user.name else "Usuario"}")
            }

            PostScreen(navController, selectedTag = null, user)
        }

        Column {
            val role: Role? = user.user?.role
            if (role != null && role != Role.USER) {
                Spacer(modifier = Modifier.weight(1f))
                CreateContentDropdown(navController = navController)
            }
        }
    }
}
