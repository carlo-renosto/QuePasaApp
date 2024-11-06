package frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import frgp.utn.edu.ar.quepasa.data.model.enums.Audience
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.neighbourhood.NeighbourhoodViewModel

@Composable
fun NeighbourhoodField(
    modifier: Modifier,
    audience: String,
    onItemSelected: (Long) -> Unit
) {
    val viewModel: NeighbourhoodViewModel = hiltViewModel()
    val neighbourhoods by viewModel.neighbourhoods.collectAsState()
    val items = neighbourhoods.map { it.name }
    val itemsId = neighbourhoods.map { it.id }

    var selectedItem by remember { mutableStateOf(items.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }

    LaunchedEffect(audience) {
        if(audience.isNotBlank()) {
            enabled = Audience.valueOf(audience) == Audience.NEIGHBORHOOD
        }
    }
    LaunchedEffect(enabled) {
        selectedItem = if(enabled) items.firstOrNull() ?: "" else ""
    }

    println("$items $selectedItem")

    Box(modifier = modifier) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector =  Icons.Filled.Place,
                contentDescription = null,
            )

            OutlinedTextField(
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            )

            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedItem = item
                            onItemSelected(itemsId[index])
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}