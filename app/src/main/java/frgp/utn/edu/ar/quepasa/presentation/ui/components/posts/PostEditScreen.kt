package frgp.utn.edu.ar.quepasa.presentation.ui.components.posts

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import frgp.utn.edu.ar.quepasa.data.model.User
import frgp.utn.edu.ar.quepasa.data.model.enums.Role
import frgp.utn.edu.ar.quepasa.presentation.ui.components.BaseComponent
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.AudienceField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.DescriptionField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.ImageField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.NeighbourhoodField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.TagField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.TagValue
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.TitleField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.TypeField
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.fields.TypeSubtypeField
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.images.ImageViewModel
import frgp.utn.edu.ar.quepasa.presentation.viewmodel.posts.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import quepasa.api.validators.commons.StringValidator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostEditScreen(navController: NavHostController, user: User?) {
    val viewModel: PostViewModel = hiltViewModel()
    val imageViewModel = ImageViewModel()

    BaseComponent(navController, user, "Modificar publicación", true) {
        LaunchedEffect(Unit) {
            viewModel.getPostById(1)
        }

        val pickMultipleMediaLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if(uris.isNotEmpty()) {
                imageViewModel.addImages(uris)
                Log.d("ImageSelector", "Number of items selected: ${uris.size}")
            }
            else {
                Log.d("ImageSelector", "No media selected")
            }
        }

        val post by viewModel.post.collectAsState()

        if(post != null) {
            post?.let { safePost ->
                var title by remember { mutableStateOf(safePost.title) }
                var audience by remember { mutableStateOf(safePost.audience.name) }
                var description by remember { mutableStateOf(safePost.description) }
                var neighbourhood by remember { mutableLongStateOf(safePost.neighbourhood!!.id) }
                var type by remember { mutableIntStateOf(safePost.subtype.type.id) }
                var subtype by remember { mutableIntStateOf(safePost.subtype.id) }
                var tag by remember { mutableStateOf("") }
                val tags by viewModel.tags.collectAsState()

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TypeField(
                            modifier = Modifier.weight(1f),
                            subtype = subtype,
                            loadBySubtype = true,
                            onItemSelected = {
                                type = it
                                subtype = 0
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TypeSubtypeField(
                            modifier = Modifier.weight(1f),
                            type,
                            onItemSelected = { subtype = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        AudienceField(
                            modifier = Modifier.weight(1f),
                            audience = audience,
                            onItemSelected = { audience = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        NeighbourhoodField(
                            modifier = Modifier.weight(1f),
                            audience = audience,
                            onItemSelected = { neighbourhood = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    TagField(
                        modifier = Modifier.padding(2.dp),
                        value = tag,
                        validator = {
                            StringValidator(title)
                                .isNotBlank()
                                .hasMaximumLength(15)
                                .hasMinimumLength(4)
                        },
                        onChange = { newTags ->
                            tag = newTags
                        },
                        onValidityChange = { status -> viewModel.toggleTagValidationField(status) },
                        onAdded = { tag = "" },
                        viewModel
                    )

                    if (tags.isNotEmpty()) {
                        FlowRow(modifier = Modifier.fillMaxWidth()) {
                            tags.forEachIndexed { index, tag ->
                                TagValue(
                                    modifier = Modifier
                                        .weight(1f),
                                    value = tag,
                                    viewModel = viewModel
                                )

                                if (index < tags.size - 1) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TitleField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = title,
                        validator = {
                            StringValidator(title)
                                .isNotBlank()
                                .hasMaximumLength(100)
                                .hasMinimumLength(1)
                        },
                        onChange = { newTitle -> title = newTitle },
                        onValidityChange = { status -> viewModel.toggleValidationField(0, status) }
                    )
                    DescriptionField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = description,
                        validator = {
                            StringValidator(title)
                                .isNotBlank()
                                .hasMaximumLength(256)
                                .hasMinimumLength(4)
                        },
                        onChange = { newDesc -> description = newDesc },
                        onValidityChange = { status -> viewModel.toggleValidationField(1, status) }
                    )
                    ImageField(modifier = Modifier.fillMaxWidth(), pickMultipleMedia = pickMultipleMediaLauncher)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            CoroutineScope(IO).launch {
                                // TODO: Not implemented yet
                            }
                        }) {
                            Text("Modificar")
                        }
                    }
                }
            }
        }
        else {
            //navController.navigate("home")
        }
    }
}

@Preview
@Composable
fun PostEditScreenPreview() {
    val navController = rememberNavController()
    val user = User(1, "", "", emptySet(), "", null, null, emptySet(), Role.USER, true)
    lateinit var pickMultipleMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    PostEditScreen(navController = navController, user = user)
}