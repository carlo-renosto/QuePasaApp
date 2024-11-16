package frgp.utn.edu.ar.quepasa.presentation.ui.components.users.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import frgp.utn.edu.ar.quepasa.presentation.ui.components.editables.OutlinedField
import quepasa.api.validators.commons.StringValidator


@Composable
fun AddressField(
    modifier: Modifier = Modifier,
    value: String = "",
    validator: (String) -> StringValidator,
    onChange: (String, Boolean) -> Unit,
    serverError: String? = null,
    clearServerError: () -> Unit = {}
) {
    OutlinedField<StringValidator, String>(
        modifier = modifier,
        validator = validator,
        valueConverter = { it },
        value = value,
        textConverter = { it },
        clearServerError = clearServerError,
        onChange = onChange,
        serverError = serverError,
        label = "Dirección"
    )
}


@Preview
@Composable
fun AddressFieldPreview() {
    var address by remember { mutableStateOf("") }
    NameField(
        modifier = Modifier,
        validator = { StringValidator(it) },
        onChange = { value, valid -> address = value },
        value = address
    )
}