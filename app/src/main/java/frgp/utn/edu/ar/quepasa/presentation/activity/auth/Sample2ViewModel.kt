package frgp.utn.edu.ar.quepasa.presentation.activity.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import frgp.utn.edu.ar.quepasa.data.dto.ApiResponse
import frgp.utn.edu.ar.quepasa.data.dto.request.LoginRequest
import frgp.utn.edu.ar.quepasa.data.dto.request.SignUpRequest
import frgp.utn.edu.ar.quepasa.domain.repository.AuthRepository
import javax.inject.Inject;

import frgp.utn.edu.ar.quepasa.domain.repository.UserRepository;
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import quepasa.api.validators.users.NameValidator
import quepasa.api.validators.users.PasswordValidator
import quepasa.api.validators.users.UsernameValidator

@HiltViewModel
open class Sample2ViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    private val loginUsernameMutable = MutableStateFlow("bocelli.andrea")
    fun setLoginUsername(x: String) {
        loginUsernameMutable.value = x
    }
    val loginUsername = loginUsernameMutable.asStateFlow()

    private val loginPasswordMutable = MutableStateFlow("Abc.1234\$6789")
    fun setLoginPassword(x: String) { loginPasswordMutable.value = x }
    val loginPassword = loginPasswordMutable.asStateFlow()

    private val snackMutable = MutableSharedFlow<String>()
    val snack = snackMutable.asSharedFlow()

    private val requiresTOTPMutable = MutableStateFlow<Boolean>(false)
    val requiresTotp = requiresTOTPMutable.asStateFlow()

    private val totpCodeMutable = MutableStateFlow("")
    val totpCode = totpCodeMutable.asStateFlow()

    private val totpValidMutable = MutableStateFlow(true)
    fun invalidateTotp() {
        totpValidMutable.value = false
        totpCodeMutable.value = ""
    }
    fun setTotpCode(x: String) {
        totpCodeMutable.value = x
        totpValidMutable.value = true
    }
    val totpValid = totpValidMutable.asStateFlow()

    private val signupNameMutable = MutableStateFlow("Andrew")
    fun setSignupName(x: String) {
        signupNameMutable.value = x
    }
    val signupName = signupNameMutable.asStateFlow()
    private val nameIsValidMutable = MutableStateFlow(false)
    val nameIsValid = nameIsValidMutable.asStateFlow()
    fun setNameValidity(x: Boolean) { nameIsValidMutable.value = x }

    val serverFeedback = MutableStateFlow("")
    fun setServerFeedback(x: String) { serverFeedback.value = x }
    val serverFeedbackField = MutableStateFlow("")
    fun setServerFeedbackField(x: String) { serverFeedbackField.value = x }

    fun clearFeedback() {
        serverFeedbackField.value = ""
        serverFeedback.value = ""
    }

    fun resetLogin() {
        CoroutineScope(IO).launch {
            loginUsernameMutable.emit("")
            loginPasswordMutable.emit("")
            requiresTOTPMutable.emit(false)
            totpCodeMutable.emit("")
            totpValidMutable.emit(true)
        }
    }

    private val signupUsernameMutable = MutableStateFlow("andrew.test")
    fun setSignupUsername(x: String) {
        signupUsernameMutable.value = x
    }
    val signupUsername = signupUsernameMutable.asStateFlow()
    private val usernameIsValidMutable = MutableStateFlow(false)
    val usernameIsValid = usernameIsValidMutable.asStateFlow()
    fun setUsernameValidity(x: Boolean) { usernameIsValidMutable.value = x }

    private val signupPasswordMutable = MutableStateFlow("Andrew$123456789")
    fun setSignupPassword(x: String) {
        signupPasswordMutable.value = x
    }
    val signupPassword = signupPasswordMutable.asStateFlow()
    private val passwordIsValidMutable = MutableStateFlow(false)
    val passwordIsValid = passwordIsValidMutable.asStateFlow()
    fun setPasswordValidity(x: Boolean) { passwordIsValidMutable.value = x }

    private val signupPasswordRepeatableMutable = MutableStateFlow("Andrew$123456789")
    fun setSignupRepeatablePassword(x: String) {
        signupPasswordRepeatableMutable.value = x
    }
    val signupPasswordRepeatable = signupPasswordRepeatableMutable.asStateFlow()
    private val passwordRepeatableIsValidMutable = MutableStateFlow(false)
    val passwordRepeatableIsValid = passwordRepeatableIsValidMutable.asStateFlow()
    fun setRepeatablePasswordValidity(x: Boolean) { passwordRepeatableIsValidMutable.value = x }

    open fun nameValidator(name: String): NameValidator {
        return NameValidator(name)
            .ifNullThen("")
            .isNotBlank()
            .validateCompoundNames()
    }
    open fun usernameValidator(username: String): UsernameValidator {
        return UsernameValidator(username)
            .isAvailable(userRepository)
            .meetsMaximumLength()
            .meetsMinimumLength()
            .neitherStartsNorEndsWithDoubleDotsOrUnderscores()
            .doesntHaveIllegalCharacters()
            .doesntHaveTwoDotsOrUnderscoresInARow()
    }
    open fun passwordValidator(password: String): PasswordValidator {
        return PasswordValidator(password)
            .ifNullThen("")
            .isNotBlank()
            .hasOneDigit()
            .lengthIsEightCharactersOrMore()
            .hasOneLowerCaseLetter()
            .hasOneSpecialCharacter()
            .hasOneUpperCaseLetter()
    }
    open fun passwordRepeatValidator(password: String, original: String): PasswordValidator {
        return PasswordValidator(password)
            .equals(original)
    }

    suspend fun signUp() {
        //if(!usernameIsValid.value || !passwordIsValid.value || !passwordRepeatableIsValid.value)
          //  return
        val req = SignUpRequest(signupName.value, signupUsername.value, signupPassword.value)
        val res = authRepository.signUp(req)
        when(res) {
            is ApiResponse.Success -> {
                snackMutable.emit("¡Éxito! Requiere TOTP: ${res.data?.totpRequired}, token: ${res.data?.token}")
            }
            is ApiResponse.ValidationError -> {
                setServerFeedback(res.details.errors.first())
                setServerFeedbackField(res.details.field)
                snackMutable.emit("ValidationError: [${res.details.field}]: ${res.details.errors.joinToString()}")
            }
            is ApiResponse.Error -> {
                snackMutable.emit("Error: ${res.exception.message}")
            }
        }
    }

    suspend fun login() {
        val req = LoginRequest(loginUsername.value, loginPassword.value)
        val res = if(!requiresTOTPMutable.value) authRepository.login(req)
        else authRepository.loginUsingTotp(totpCode.value)
        when(res) {
            is ApiResponse.Success -> {
                requiresTOTPMutable.value = res.data?.totpRequired ?: false
                snackMutable.emit("Iniciaste sesión correctamente")
            }
            is ApiResponse.ValidationError -> {
                if(!requiresTOTPMutable.value) snackMutable.emit("Error de validación: ${res.details.errors.joinToString()}")
            }
            is ApiResponse.Error -> {
                if(requiresTOTPMutable.value) {
                    invalidateTotp()
                } else snackMutable.emit("Error: ${res.exception.message}")
            }
        }
    }

}
