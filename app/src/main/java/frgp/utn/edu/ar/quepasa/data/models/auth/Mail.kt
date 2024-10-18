package frgp.utn.edu.ar.quepasa.data.models.auth


import com.google.type.DateTime
import frgp.utn.edu.ar.quepasa.data.model.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Mail (
    val mail: String,
    @Contextual
    val user: User,
    val hash: String,
    val isVerified: Boolean,
    @Contextual
    val verifiedAt: DateTime,
    @Contextual
    val requestedAt: DateTime
)
