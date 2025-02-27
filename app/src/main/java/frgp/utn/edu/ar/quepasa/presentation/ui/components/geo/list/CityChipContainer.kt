package frgp.utn.edu.ar.quepasa.presentation.ui.components.geo.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import frgp.utn.edu.ar.quepasa.data.model.geo.City
import frgp.utn.edu.ar.quepasa.data.model.geo.Neighbourhood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityChipContainer(
    data: Set<City>,
    onUnselectRequest: (City) -> Unit,
    maxVisibleItems: Int? = 2,
    maxFullyVisibleItems: Int? = 1
) {
    val lazyListState = rememberLazyListState()
    var showingModal by remember { mutableStateOf(false) }
    val visibleItems = if(data.size > (maxVisibleItems ?: 2)) (maxFullyVisibleItems ?: 1) else data.size
    val totalItemsCount = data.size
    val hiddenItemsCount = totalItemsCount - visibleItems
    LazyRow(state = lazyListState) {
        items(items = data.take(visibleItems), key = { it.id }) { city ->
            InputChip(
                selected = true,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                onClick = { onUnselectRequest(city) },
                label = { Text(city.name) }
            )
        }

        if (hiddenItemsCount > 0) {
            item {
                InputChip(
                    selected = false,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    onClick = { showingModal = true },
                    label = { Text("+ $hiddenItemsCount") }
                )
            }
        }
    }
    if(showingModal) ModalBottomSheet(
        onDismissRequest = { showingModal = false }
    ) {
        Text(
            text = "Ciudades seleccionadas",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        )
        if(!data.isEmpty()) Text(
            text = "Hacé click para deshacer la selección. ",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        )
        if(data.isEmpty()) Text(
            text = "Cuando seleccionés ciudades, van a aparecer acá. ",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 48.dp)
                .fillMaxWidth()
        ) else Column(modifier = Modifier.fillMaxWidth().padding(bottom = 36.dp)) {
            CityList(
                items = data.toList(),
                selectable = false,
                onClick = onUnselectRequest,
                loadable = false,
                showGeographicalContext = true
            )
        }
    }

}