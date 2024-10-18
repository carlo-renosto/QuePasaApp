package frgp.utn.edu.ar.quepasa.data.models.auth


import com.google.type.DateTime
import frgp.utn.edu.ar.quepasa.data.models.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Phone (
    var phone: String? = null,
    var user: User? = null,
    var hash: String? = null,
    var isVerified: Boolean = false,
    @Contextual
    var verifiedAt: DateTime? = null,
    @Contextual
    var requestedAt: DateTime? = null,
    )
