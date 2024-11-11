package frgp.utn.edu.ar.quepasa.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import frgp.utn.edu.ar.quepasa.presentation.activity.auth.AuthenticatedActivity
import frgp.utn.edu.ar.quepasa.presentation.ui.components.main.NavigationMainHost
import frgp.utn.edu.ar.quepasa.presentation.ui.theme.TP4Theme

class MainNavigationActivity : AuthenticatedActivity() {
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