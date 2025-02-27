package frgp.utn.edu.ar.quepasa.presentation.viewmodel.images

import android.net.Uri
import androidx.lifecycle.ViewModel
import frgp.utn.edu.ar.quepasa.data.model.media.EventPicture
import frgp.utn.edu.ar.quepasa.data.model.media.PostPicture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.UUID

class ImageViewModel : ViewModel() {
    private val _selectedUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedUris = _selectedUris.asStateFlow()

    private val _selectedUrls = MutableStateFlow<List<String>>(emptyList())
    val selectedUrls = _selectedUrls.asStateFlow()

    private val _selectedUrlsId = MutableStateFlow<List<UUID>>(emptyList())
    val selectedUrisId = _selectedUrlsId.asStateFlow()

    private var _uriCount = MutableStateFlow(0)
    val uriCount: StateFlow<Int> get() = _uriCount

    fun addImages(uris: List<Uri>) {
        _selectedUris.value += uris
        _uriCount.value = _selectedUris.value.size
    }

    fun clearImages() {
        _selectedUris.value = emptyList()
        _uriCount.value = 0
    }

    fun clearImage(uri: Uri) {
        var uriList = emptyList<Uri>()
        _selectedUris.value.forEach { selUri ->
            if(selUri.path != uri.path) uriList = uriList + selUri
        }
        _selectedUris.value = uriList
        _uriCount.value = _selectedUris.value.size
    }

    fun areUrisEmpty(): Boolean {
        return _selectedUris.value.isEmpty()
    }

    fun loadUrlsFromPostPictures(pictures: List<PostPicture>) {
        val urls: MutableList<String> = mutableListOf()
        val urlsId: MutableList<UUID> = mutableListOf()
        pictures.forEach { picture ->
            val url = "http://canedo.com.ar:8080/api/pictures/" + picture.id + "/view"

            urls.add(url)
            urlsId.add(picture.id)
            println("url $url")
        }

        _selectedUrls.value = urls
        _selectedUrlsId.value = urlsId
    }

    fun loadUrisFromEventPictures(pictures: List<EventPicture>) {
        val uris: MutableList<Uri> = mutableListOf()
        val urls: MutableList<String> = mutableListOf()
        val urlsId: MutableList<UUID> = mutableListOf()
        pictures.forEach { picture ->
            val url = "http://canedo.com.ar:8080/api/event-pictures/" + picture.id + "/view"
            val file =
                Uri.fromFile(File("http://canedo.com.ar:8080/api/event-pictures/" + picture.id + "/view"))

            urls.add(url)
            urlsId.add(picture.id)
            uris.add(file)
            println("url $url")
        }

        urls.forEach { url ->
            _selectedUrls.value += url
        }
        urlsId.forEach { id ->
            _selectedUrlsId.value += id
        }

    }
}