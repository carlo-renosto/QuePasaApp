package frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.dropdown.AudienceField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.dropdown.NeighbourhoodField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.dropdown.TypeField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.dropdown.TypeSubtypeField
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.neighbourhood.NeighbourhoodViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.posts.PostFormViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.posts.PostSubtypeViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.posts.PostTypeViewModel
import frgp.utn.edu.ar.quepasa.utils.audience.audiencesToSpanishSelectedFirst

@Composable
fun PostDropdownSection(viewModel: PostFormViewModel) {
    val typeViewModel: PostTypeViewModel = hiltViewModel()
    val subtypeViewModel: PostSubtypeViewModel = hiltViewModel()
    val neighViewModel: NeighbourhoodViewModel = hiltViewModel()

    val types = typeViewModel.postTypes.collectAsState()
    val subtypes = subtypeViewModel.postSubtypes.collectAsState()
    val audiences: List<String> = audiencesToSpanishSelectedFirst(viewModel.getAudience())
    val neighbourhoods = neighViewModel.neighbourhoods.collectAsState()

    var selectedType by remember { mutableIntStateOf(viewModel.getType()) }
    var selectedSubtype by remember { mutableIntStateOf(viewModel.getSubtype()) }

    var selectedAudience by remember { mutableStateOf(viewModel.getAudience()) }
    var selectedNeighbourhood by remember { mutableLongStateOf(viewModel.getNeighbourhood()) }

    LaunchedEffect(Unit, selectedType) {
        typeViewModel.getTypesBySubtype(selectedSubtype, 0, 10)
        subtypeViewModel.getSubtypesBySelectedFirst(selectedType, selectedSubtype, 0, 10)
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        TypeField(
            modifier = Modifier.weight(1f),
            types = types.value.content,
            onItemSelected = { selectedType = it }
        )
        VerticalDivider(thickness = 4.dp)
        TypeSubtypeField(
            modifier = Modifier.weight(1f),
            subtypes = subtypes.value.content,
            onItemSelected = {
                selectedSubtype = it
                viewModel.updateSubtype(it)
            }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        AudienceField(
            modifier = Modifier.weight(1f),
            audiences = audiences,
            onItemSelected = {
                selectedAudience = it
                viewModel.updateAudience(it)
            }
        )
        VerticalDivider(thickness = 4.dp)
        NeighbourhoodField(
            modifier = Modifier.weight(1f),
            neighbourhoods = neighbourhoods.value.content,
            onItemSelected = {
                selectedNeighbourhood = it
                viewModel.updateNeighbourhood(it)
            }
        )
    }
}