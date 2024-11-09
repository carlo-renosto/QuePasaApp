package frgp.utn.edu.ar.quepasa.presentation.ui.components.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.trends.TrendsViewModel

@Composable
fun TrendsScreen() {
    val trendsViewModel: TrendsViewModel = hiltViewModel()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tendencias", modifier = Modifier.padding(bottom = 8.dp))
        TrendsCarousel(trendsViewModel = trendsViewModel)
    }
}
