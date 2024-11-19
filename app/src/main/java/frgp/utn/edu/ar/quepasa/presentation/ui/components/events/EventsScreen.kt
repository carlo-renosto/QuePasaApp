package frgp.utn.edu.ar.quepasa.presentation.ui.components.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import frgp.utn.edu.ar.quepasa.data.model.enums.EventCategory
import frgp.utn.edu.ar.quepasa.domain.context.user.LocalAuth
import frgp.utn.edu.ar.quepasa.presentation.ui.components.BaseComponent
import frgp.utn.edu.ar.quepasa.presentation.ui.components.events.card.EventCard
import frgp.utn.edu.ar.quepasa.presentation.ui.components.events.fields.EventCategoryField
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.events.EventViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.media.EventPictureViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.media.PictureViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavHostController) {
    val user by LocalAuth.current.collectAsState()
    val viewModel: EventViewModel = hiltViewModel()
    val eventPictureViewModel: EventPictureViewModel = hiltViewModel()
    val pictureViewModel: PictureViewModel = hiltViewModel()
    val eventState = viewModel.events.collectAsStateWithLifecycle()

    val events by viewModel.events.collectAsState()
    val pictures by eventPictureViewModel.eventPictures.collectAsState()
    val eventPictureDTO by pictureViewModel.eventPictureDTO.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()

    var currentPage by remember { mutableStateOf(0) }

    var category by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<UUID?>(null) }

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sortEventsByVotes()
    }

    LaunchedEffect(Unit, events) {
        viewModel.viewModelScope.launch {
            events.content.forEach { event ->
                if (pictures.find { it.event?.id == event.id } == null) {
                    eventPictureViewModel.setEventsPicture(event.id!!)
                }
            }
        }
    }

    LaunchedEffect(pictures) {
        pictureViewModel.viewModelScope.launch {
            pictures.forEach { picture ->
                picture.event?.id?.let { uuid ->
                    if (eventPictureDTO.find { it?.eventId == picture.event?.id } == null) {
                        pictureViewModel.setPictureEvents(
                            picture.id,
                            uuid,
                        )
                    }
                }
            }
        }
    }

    BaseComponent(navController, user.user, "Listado Eventos", false) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refreshEvents()
                    currentPage = 0
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = search,
                        onValueChange = {
                            search = it
                            viewModel.viewModelScope.launch {
                                viewModel.getEvents(search)
                                category = ""
                            }
                        },
                        label = {
                            Text("Buscar")
                        },
                        placeholder = {
                            Text("Curso de ...")
                        }
                    )
                }
                Row {
                    EventCategoryField(
                        modifier = Modifier.fillMaxWidth(),
                        category = category,
                        onItemSelected = {
                            category = it
                            viewModel.viewModelScope.launch {
                                viewModel.getEventsByCategory(EventCategory.valueOf(it))
                                search = ""
                            }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true,
                ) {
                    items(events.content) { event ->
                        key(event.id) {
                            EventCard(
                                eventPictureDTO.find { it?.eventId == event.id }?.bitmap,
                                navController,
                                event,
                                user.user,
                                onAssistanceClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.rsvpEvent(event.id!!)
                                        resetEvents(viewModel, category, search)
                                    }
                                },
                                onRemoveClick = {
                                    eventToDelete = event.id
                                    showDialog = true
                                },
                                onUpvoteClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.upVote(event.id!!)
                                        resetEvents(viewModel, category, search)
                                    }
                                },
                                onDownvoteClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.downVote(event.id!!)
                                        resetEvents(viewModel, category, search)
                                    }
                                }
                            )
                        }
                    }

                    if (currentPage < eventState.value.totalPages - 1) {
                        item {
                            if (isLoadingMore) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(16.dp)
                                )
                            } else {
                                Button(
                                    onClick = {
                                        if (currentPage < eventState.value.totalPages - 1) {
                                            currentPage++
                                            viewModel.loadMoreEvents()
                                        } else {
                                            showSnackbar = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("Cargar más")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Ya estás al día con los eventos.")
            showSnackbar = false
        }
    }
    SnackbarHost(
        hostState = snackbarHostState
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text(text = "¿Estás seguro de que deseas eliminar este evento?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            eventToDelete?.let { event ->
                                viewModel.deleteEvent(event)
                                resetEvents(viewModel, category, search)
                            }
                            eventToDelete = null
                            showDialog = false
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}

fun resetEvents(
    viewModel: EventViewModel,
    category: String,
    search: String
) {
    viewModel.viewModelScope.launch {
        if (category.isNotBlank()) {
            viewModel.getEventsByCategory(EventCategory.valueOf(category))
            viewModel.sortEventsByVotes()
        } else if (search.isNotBlank()) {
            viewModel.getEvents(search)
            viewModel.sortEventsByVotes()
        } else {
            viewModel.getEvents()
            viewModel.sortEventsByVotes()
        }
    }
}
