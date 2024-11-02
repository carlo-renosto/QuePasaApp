package frgp.utn.edu.ar.quepasa

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import frgp.utn.edu.ar.quepasa.presentation.activity.auth.AuthenticatedActivity
import frgp.utn.edu.ar.quepasa.presentation.ui.components.main.NavigationMainHost
import frgp.utn.edu.ar.quepasa.presentation.ui.theme.TP4Theme

class MainActivity : AuthenticatedActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            TP4Theme {
                NavigationMainHost(navController = navController, user = getCurrentUser())
            }
        }
    }
}