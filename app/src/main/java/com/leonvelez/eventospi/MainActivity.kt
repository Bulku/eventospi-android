package com.leonvelez.eventospi

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.leonvelez.eventospi.data.TokenManager
import com.leonvelez.eventospi.data.model.EventRequest
import com.leonvelez.eventospi.data.remote.RetrofitInstance
import com.leonvelez.eventospi.ui.theme.EventosPITheme
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import android.provider.OpenableColumns
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.leonvelez.eventospi.data.model.EventResponse
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.leonvelez.eventospi.data.model.EventParticipantResponse
import com.leonvelez.eventospi.data.model.ManageParticipantRequest
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.maps.MapLibreMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventosPITheme {
                MaterialTheme {
                    MapRootScreen()
                }
            }
        }
    }
}

@Composable
fun AppScreen() {
    var showRegisterScreen by remember { mutableStateOf(false) }
    var registeredEmail by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("Ingresa tus datos") }
    var showChangePasswordScreen by remember { mutableStateOf(false) }
    var showHomeScreen by remember { mutableStateOf(false) }
    var showCreateEventScreen by remember { mutableStateOf(false) }
    var showEventsListScreen by remember { mutableStateOf(false) }
    var showUpdateEventScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    if (showRegisterScreen) {
        RegisterScreen(
            onBackToLogin = { showRegisterScreen = false },
            onRegisterSuccess = { email ->
                registeredEmail = email
                loginMessage = "Cuenta creada correctamente. Ahora inicia sesión"
                showRegisterScreen = false
            }
        )
    } else if (showChangePasswordScreen) {
        ChangePasswordScreen(
            onBackToHome = { showChangePasswordScreen = false }
        )
    } else if (showCreateEventScreen) {
        CreateEventScreen(
            onBackToHome = { showCreateEventScreen = false }
        )
    } else if (showEventsListScreen) {
        EventsListScreen(
            onBackToHome = { showEventsListScreen = false }
        )
    } else if (showUpdateEventScreen) {
        UpdateEventScreen(
            onBackToHome = { showUpdateEventScreen = false }
        )
    } else if (showHomeScreen) {
        HomeScreen(
            onGoToCreateEvent = { showCreateEventScreen = true },
            onGoToEventsList = { showEventsListScreen = true },
            onGoToUpdateEvent = { showUpdateEventScreen = true },
            onGoToChangePassword = { showChangePasswordScreen = true },
            onLogout = {
                tokenManager.clearToken()

                showHomeScreen = false
                showRegisterScreen = false
                showChangePasswordScreen = false
                showCreateEventScreen = false
                showEventsListScreen = false
                showUpdateEventScreen = false

                registeredEmail = ""
                loginMessage = "Sesión cerrada"
            }
        )
    } else {
        LoginScreen(
            onGoToRegister = { showRegisterScreen = true },
            onLoginSuccess = { showHomeScreen = true },
            initialEmail = registeredEmail,
            initialMessage = loginMessage
        )
    }
}

@Composable
fun LoginScreen(
    onGoToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    initialEmail: String,
    initialMessage: String
) {
    var email by remember(initialEmail) { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var resultText by remember(initialMessage) { mutableStateOf(initialMessage) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    resultText = "Completa todos los campos"
                    return@Button
                }

                scope.launch {
                    try {
                        val response = RetrofitInstance.api.login(
                            email = email,
                            password = password
                        )

                        if (response.isSuccessful) {
                            val token = response.body()?.token ?: "Token vacío"
                            tokenManager.saveToken(token)
                            resultText = "Login exitoso"
                            Log.d("LOGIN_OK", token)
                            onLoginSuccess()
                        } else {
                            resultText = "Error: ${response.code()}"
                            Log.e(
                                "LOGIN_ERROR",
                                "Código: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción: ${e.message}"
                        Log.e("LOGIN_EXCEPTION", e.toString())
                    }
                }
            }
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoToRegister,
            modifier = Modifier.width(160.dp)
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay token guardado"
                            return@launch
                        }

                        val response = RetrofitInstance.api.getUserAuthenticated(
                            token = "Bearer $savedToken"
                        )

                        if (response.isSuccessful) {
                            val body = response.body()?.string() ?: "Respuesta vacía"
                            resultText = "Endpoint protegido OK: $body"
                            Log.d("AUTH_OK", body)
                        } else {
                            resultText = "Protegido error: ${response.code()}"
                            Log.e(
                                "AUTH_ERROR",
                                "Código: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción protegido: ${e.message}"
                        Log.e("AUTH_EXCEPTION", e.toString())
                    }
                }
            },
            modifier = Modifier.width(220.dp)
        ) {
            Text("Probar usuario autenticado")
        }
        Text(text = resultText)
    }
}

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Completa tus datos para registrarte") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Nombre de usuario") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (
                    firstName.isBlank() ||
                    lastName.isBlank() ||
                    userName.isBlank() ||
                    email.isBlank() ||
                    password.isBlank() ||
                    confirmPassword.isBlank()
                ) {
                    resultText = "Completa todos los campos"
                    return@Button
                }

                scope.launch {
                    try {
                        val response = RetrofitInstance.api.register(
                            firstName = firstName,
                            lastName = lastName,
                            userName = userName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword
                        )

                        if (response.isSuccessful) {
                            val message = response.body()?.string() ?: "Usuario registrado correctamente"
                            resultText = message
                            Log.d("REGISTER_OK", message)
                            onRegisterSuccess(email)
                        } else {
                            resultText = "Error: ${response.code()}"
                            Log.e(
                                "REGISTER_ERROR",
                                "Código: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción: ${e.message}"
                        Log.e("REGISTER_EXCEPTION", e.toString())
                    }
                }
            }
        ) {
            Text("Crear cuenta")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Volver al login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
    }
}
@Composable
fun ChangePasswordScreen(
    onBackToHome: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Ingresa los datos para cambiar tu contraseña") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cambiar contraseña",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Contraseña actual") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text("Confirmar nueva contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (
                    currentPassword.isBlank() ||
                    newPassword.isBlank() ||
                    confirmNewPassword.isBlank()
                ) {
                    resultText = "Completa todos los campos"
                    return@Button
                }
                if (newPassword != confirmNewPassword) {
                    resultText = "La confirmación no coincide"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val response = RetrofitInstance.api.changePassword(
                            token = "Bearer $savedToken",
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            confirmNewPassword = confirmNewPassword
                        )

                        if (response.isSuccessful) {
                            Log.d("CHANGE_PASSWORD_OK", "Contraseña cambiada correctamente")
                            onBackToHome()
                        } else {
                            resultText = "Error: ${response.code()}"
                            Log.e(
                                "CHANGE_PASSWORD_ERROR",
                                "Código: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción: ${e.message}"
                        Log.e("CHANGE_PASSWORD_EXCEPTION", e.toString())
                    }
                }
            }
        ) {
            Text("Actualizar contraseña")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al inicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
    }
}
@Composable
fun HomeScreen(
    onGoToCreateEvent: () -> Unit,
    onGoToEventsList: () -> Unit,
    onGoToUpdateEvent: () -> Unit,
    onGoToChangePassword: () -> Unit,
    onLogout: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onGoToCreateEvent,
            modifier = Modifier.width(220.dp)
        ) {
            Text("Crear evento")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoToEventsList,
            modifier = Modifier.width(220.dp)
        ) {
            Text("Ver eventos")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoToUpdateEvent,
            modifier = Modifier.width(220.dp)
        ) {
            Text("Actualizar evento")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onGoToChangePassword,
            modifier = Modifier.width(220.dp)
        ) {
            Text("Cambiar contraseña")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.width(220.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}
fun getFileNameFromUri(
    context: android.content.Context,
    uri: Uri
): String {
    var result = "imagen.jpg"

    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex != -1) {
            result = it.getString(nameIndex)
        }
    }

    return result
}

fun createImagePartFromUri(
    context: android.content.Context,
    uri: Uri
): MultipartBody.Part {
    val fileName = getFileNameFromUri(context, uri)
    val mimeType = context.contentResolver.getType(uri) ?: "image/*"

    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("No se pudo abrir la imagen seleccionada")

    val tempFile = File.createTempFile("upload_", fileName, context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "FormFile",
        fileName,
        requestFile
    )
}

fun createProfileImagePartFromUri(
    context: android.content.Context,
    uri: Uri
): MultipartBody.Part {
    val fileName = getFileNameFromUri(context, uri)
    val mimeType = context.contentResolver.getType(uri) ?: "image/*"

    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("No se pudo abrir la imagen seleccionada")

    val tempFile = File.createTempFile("profile_", fileName, context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "file",
        fileName,
        requestFile
    )
}
fun openDatePicker(
    context: android.content.Context,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mm = (month + 1).toString().padStart(2, '0')
            val dd = dayOfMonth.toString().padStart(2, '0')
            onDateSelected("$year-$mm-$dd")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun openTimePicker(
    context: android.content.Context,
    onTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val hh = hourOfDay.toString().padStart(2, '0')
            val mm = minute.toString().padStart(2, '0')
            onTimeSelected("$hh:$mm:00")
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}
@Composable
fun PickerLikeField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}
@Composable
fun CreateEventScreen(
    onBackToHome: () -> Unit,
    initialLatitude: Double? = null,
    initialLongitude: Double? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var eventDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    var isMultiDay by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf("") }

    var latitude by remember(initialLatitude) {
        mutableStateOf(initialLatitude?.toString().orEmpty())
    }
    var longitude by remember(initialLongitude) {
        mutableStateOf(initialLongitude?.toString().orEmpty())
    }

    var address by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultText by remember { mutableStateOf("Completa los datos del evento") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear evento",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PickerLikeField(
            label = "Fecha del evento",
            value = eventDate,
            placeholder = "Selecciona la fecha",
            onClick = {
                openDatePicker(context) { selectedDate ->
                    eventDate = selectedDate
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PickerLikeField(
            label = "Hora de inicio",
            value = startTime,
            placeholder = "Selecciona la hora de inicio",
            onClick = {
                openTimePicker(context) { selectedTime ->
                    startTime = selectedTime
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PickerLikeField(
            label = "Hora de finalización",
            value = endTime,
            placeholder = "Selecciona la hora de finalización",
            onClick = {
                openTimePicker(context) { selectedTime ->
                    endTime = selectedTime
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isMultiDay,
                onCheckedChange = { checked ->
                    isMultiDay = checked
                    if (!checked) {
                        endDate = ""
                    }
                }
            )
            Text("El evento dura más de un día")
        }

        if (isMultiDay) {
            Spacer(modifier = Modifier.height(12.dp))

            PickerLikeField(
                label = "Fecha de finalización",
                value = endDate,
                placeholder = "Selecciona la fecha final",
                onClick = {
                    openDatePicker(context) { selectedDate ->
                        endDate = selectedDate
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PickerLikeField(
            label = "Imagen del evento",
            value = if (selectedImageUri == null) "" else "Imagen seleccionada",
            placeholder = "Seleccionar imagen",
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitud") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitud") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = maxParticipants,
            onValueChange = { maxParticipants = it },
            label = { Text("Máximo participantes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría (número)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (
                    name.isBlank() ||
                    description.isBlank() ||
                    eventDate.isBlank() ||
                    startTime.isBlank() ||
                    endTime.isBlank() ||
                    latitude.isBlank() ||
                    longitude.isBlank() ||
                    address.isBlank() ||
                    maxParticipants.isBlank() ||
                    category.isBlank()
                ) {
                    resultText = "Completa los campos obligatorios"
                    return@Button
                }

                if (isMultiDay && endDate.isBlank()) {
                    resultText = "Selecciona la fecha de finalización"
                    return@Button
                }

                val startDateTime = "${eventDate}T${startTime}"
                val finalDateForEnd = if (isMultiDay) endDate else eventDate
                val endDateTime = "${finalDateForEnd}T${endTime}"

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val event = EventRequest(
                            id = 0,
                            name = name,
                            description = description,
                            startDate = "${startDateTime}Z",
                            endDate = "${endDateTime}Z",
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            address = address,
                            maxParticipants = maxParticipants.toInt(),
                            isPublic = true,
                            category = category.toInt(),
                            price = if (price.isBlank()) null else price.toDouble(),
                            imageUrl = null
                        )

                        val response = RetrofitInstance.api.createEvent(
                            token = "Bearer $savedToken",
                            event = event
                        )

                        if (response.isSuccessful) {
                            val createdEvent = response.body()

                            if (createdEvent == null) {
                                resultText = "Evento creado, pero sin respuesta del servidor"
                                return@launch
                            }

                            if (selectedImageUri != null) {
                                try {
                                    val imagePart = createImagePartFromUri(
                                        context = context,
                                        uri = selectedImageUri!!
                                    )

                                    val eventIdPart = createdEvent.id
                                        .toString()
                                        .toRequestBody("text/plain".toMediaTypeOrNull())

                                    val uploadResponse = RetrofitInstance.api.uploadEventImage(
                                        token = "Bearer $savedToken",
                                        eventId = eventIdPart,
                                        formFile = imagePart
                                    )

                                    if (uploadResponse.isSuccessful) {
                                        resultText = "Evento e imagen cargados correctamente"
                                        onBackToHome()
                                    } else {
                                        val uploadError = uploadResponse.errorBody()?.string().orEmpty()
                                        resultText = "Evento creado, pero falló la imagen: ${uploadResponse.code()} - $uploadError"
                                    }
                                } catch (e: Exception) {
                                    resultText = "Evento creado, pero error subiendo imagen: ${e.message}"
                                }
                            } else {
                                resultText = "Evento creado correctamente"
                                onBackToHome()
                            }
                        } else {
                            val createError = response.errorBody()?.string().orEmpty()
                            resultText = "Error al crear evento: ${response.code()} - $createError"
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción al crear evento: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear evento")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al mapa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
    }
}
@Composable
fun EventsListScreen(
    onBackToHome: () -> Unit
) {
    var resultText by remember { mutableStateOf("Cargando eventos...") }
    val scope = rememberCoroutineScope()
    var deleteId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    var registerEventId by remember { mutableStateOf("") }
    var cancelEventId by remember { mutableStateOf("") }
    var cancellationReason by remember { mutableStateOf("") }
    var participantsEventId by remember { mutableStateOf("") }
    var participantsText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getEvents()

                if (response.isSuccessful) {
                    val events = response.body().orEmpty()

                    resultText = if (events.isEmpty()) {
                        "No hay eventos disponibles"
                    } else {
                        events.joinToString("\n\n") {
                            "ID: ${it.id}\nNombre: ${it.name}\nDirección: ${it.address}\nCreado por: ${it.createdByUserName}"
                        }
                    }
                } else {
                    resultText = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                resultText = "Excepción: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lista de eventos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = registerEventId,
            onValueChange = { registerEventId = it },
            label = { Text("ID del evento para inscribirse") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (registerEventId.isBlank()) {
                    resultText = "Ingresa un ID para inscribirte"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val response = RetrofitInstance.api.registerToEvent(
                            token = "Bearer $savedToken",
                            eventId = registerEventId.toInt(),
                            cancellationReason = ""
                        )

                        if (response.isSuccessful) {
                            onBackToHome()
                        } else {
                            val errorText = response.errorBody()?.string().orEmpty()

                            resultText = when {
                                errorText.contains("Ya estás registrado en este evento", ignoreCase = true) ->
                                    "Ya estás inscrito en este evento"

                                errorText.contains("No puedes registrarte a tu propio evento", ignoreCase = true) ->
                                    "No puedes inscribirte a tu propio evento"

                                errorText.contains("El evento ya finalizó", ignoreCase = true) ->
                                    "El evento ya finalizó"

                                errorText.contains("El evento ya está lleno", ignoreCase = true) ->
                                    "El evento ya está lleno"

                                errorText.contains("Evento no encontrado", ignoreCase = true) ->
                                    "Evento no encontrado"

                                else ->
                                    "Error al inscribirse: ${response.code()}"
                            }
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción al inscribirse: ${e.message}"
                    }
                }
            }
        ) {
            Text("Inscribirse")
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cancelEventId,
            onValueChange = { cancelEventId = it },
            label = { Text("ID del evento para cancelar inscripción") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = cancellationReason,
            onValueChange = { cancellationReason = it },
            label = { Text("Motivo de cancelación") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (cancelEventId.isBlank()) {
                    resultText = "Ingresa un ID para cancelar la inscripción"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val response = RetrofitInstance.api.cancelRegistration(
                            token = "Bearer $savedToken",
                            eventId = cancelEventId.toInt(),
                            cancellationReason = cancellationReason
                        )

                        if (response.isSuccessful) {
                            onBackToHome()
                        } else {
                            val errorText = response.errorBody()?.string().orEmpty()

                            resultText = when {
                                errorText.contains("No estás registrado en este evento", ignoreCase = true) ->
                                    "No estás inscrito en este evento"

                                errorText.contains("Evento no encontrado", ignoreCase = true) ->
                                    "Evento no encontrado"

                                else ->
                                    "Error al cancelar inscripción: ${response.code()}"
                            }
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción al cancelar inscripción: ${e.message}"
                    }
                }
            }
        ) {
            Text("Cancelar inscripción")
        }
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = participantsEventId,
            onValueChange = { participantsEventId = it },
            label = { Text("ID del evento para ver participantes") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (participantsEventId.isBlank()) {
                    participantsText = "Ingresa un ID de evento"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            participantsText = "No hay sesión activa"
                            return@launch
                        }

                        val response = RetrofitInstance.api.getParticipantsByEventId(
                            token = "Bearer $savedToken",
                            eventId = participantsEventId.toInt()
                        )

                        if (response.isSuccessful) {
                            val participants = response.body().orEmpty()

                            participantsText = if (participants.isEmpty()) {
                                "No hay participantes aprobados para este evento"
                            } else {
                                participants.joinToString("\n\n") {
                                    "Usuario: ${it.userName}\n" +
                                            "Nombre: ${it.userFirstName} ${it.userLastName}\n" +
                                            "Estado: ${when (it.status) {
                                                0 -> "Pendiente"
                                                1 -> "Aprobado"
                                                2 -> "Rechazado"
                                                3 -> "Cancelado"
                                                4 -> "Asistió"
                                                else -> "Desconocido"
                                            }}"
                                }
                            }
                        } else {
                            participantsText = "Error al obtener participantes: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        participantsText = "Excepción al obtener participantes: ${e.message}"
                    }
                }
            }
        ) {
            Text("Ver participantes")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = participantsText)

        OutlinedTextField(
            value = deleteId,
            onValueChange = { deleteId = it },
            label = { Text("ID del evento a eliminar") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (deleteId.isBlank()) {
                    resultText = "Ingresa un ID para eliminar"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val response = RetrofitInstance.api.deleteEvent(
                            token = "Bearer $savedToken",
                            id = deleteId.toInt()
                        )

                        if (response.isSuccessful) {
                            resultText = "Evento eliminado correctamente"
                            onBackToHome()
                        } else {
                            resultText = "Error al eliminar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción al eliminar: ${e.message}"
                    }
                }
            }
        ) {
            Text("Eliminar evento")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al inicio")
        }
    }
}
@Composable
fun UpdateEventScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var events by remember { mutableStateOf<List<EventResponse>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<EventResponse?>(null) }
    var resultText by remember { mutableStateOf("Cargando eventos...") }

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getEvents()
            if (response.isSuccessful) {
                events = response.body().orEmpty()
                resultText = if (events.isEmpty()) {
                    "No hay eventos disponibles"
                } else {
                    ""
                }
            } else {
                resultText = "Error cargando eventos: ${response.code()}"
            }
        } catch (e: Exception) {
            resultText = "Excepción cargando eventos: ${e.message}"
        }
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            id = event.id.toString()
            name = event.name
            description = event.description
            startDate = event.startDate
            endDate = event.endDate
            latitude = event.latitude.toString()
            longitude = event.longitude.toString()
            address = event.address
            maxParticipants = event.maxParticipants.toString()
            category = event.category.toString()
            price = event.price?.toString().orEmpty()
        }
    }

    if (selectedEvent == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selecciona un evento para actualizar",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty()) {
                Text(resultText)
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(events) { event ->
                        EventListCard(
                            event = event,
                            onClick = { selectedEvent = event }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToHome) {
                Text("Volver al mapa")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actualizar evento",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = id,
                onValueChange = {},
                label = { Text("ID") },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Fecha inicio") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("Fecha fin") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitud") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitud") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = maxParticipants,
                onValueChange = { maxParticipants = it },
                label = { Text("Máximo participantes") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio opcional") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val savedToken = tokenManager.getToken()

                            if (savedToken.isNullOrBlank()) {
                                resultText = "No hay sesión activa"
                                return@launch
                            }

                            val event = EventRequest(
                                id = id.toInt(),
                                name = name,
                                description = description,
                                startDate = startDate,
                                endDate = endDate,
                                latitude = latitude.toDouble(),
                                longitude = longitude.toDouble(),
                                address = address,
                                maxParticipants = maxParticipants.toInt(),
                                isPublic = true,
                                category = category.toInt(),
                                price = if (price.isBlank()) null else price.toDouble(),
                                imageUrl = null
                            )

                            val response = RetrofitInstance.api.updateEvent(
                                token = "Bearer $savedToken",
                                event = event
                            )

                            if (response.isSuccessful) {
                                resultText = "Evento actualizado correctamente"
                                onBackToHome()
                            } else {
                                resultText = "Error al actualizar evento: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            resultText = "Excepción al actualizar evento: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { selectedEvent = null }
            ) {
                Text("Volver a la lista")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackToHome
            ) {
                Text("Volver al mapa")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(resultText)
        }
    }
}
@Composable
fun MenuActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(text)
    }
}

@Composable
fun InfoPill(text: String) {
    Surface(
        color = Color(0xFFEAF2FF),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EventListCard(
    event: EventResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("ID: ${event.id}")
            Text("Dirección: ${event.address}")
            Text("Creado por: ${event.createdByUserName}")

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoPill("Categoría ${event.category}")
                InfoPill("Cupo ${event.maxParticipants}")
            }
        }
    }
}

@Composable
fun PendingParticipantCard(
    participant: EventParticipantResponse,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = participant.userName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Nombre: ${participant.userFirstName} ${participant.userLastName}")
            Text("Estado: ${participantStatusLabel(participant.status)}")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Aprobar")
                }

                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}
@Composable
fun EventImageFromUrl(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = imageUrl) {
        value = null

        if (imageUrl.isNullOrBlank()) return@produceState

        value = try {
            withContext(Dispatchers.IO) {
                URL(imageUrl).openStream().use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFFF1F1F1), RoundedCornerShape(16.dp))
            .clickable(enabled = bitmapState.value != null && onClick != null) {
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl.isNullOrBlank() -> {
                Text("Sin imagen")
            }

            bitmapState.value == null -> {
                CircularProgressIndicator()
            }

            else -> {
                Image(
                    bitmap = bitmapState.value!!.asImageBitmap(),
                    contentDescription = "Imagen del evento",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
@Composable
fun FullScreenEventImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = imageUrl) {
        value = try {
            withContext(Dispatchers.IO) {
                URL(imageUrl).openStream().use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            if (bitmapState.value == null) {
                CircularProgressIndicator()
            } else {
                Image(
                    bitmap = bitmapState.value!!.asImageBitmap(),
                    contentDescription = "Imagen del evento en pantalla completa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
@Composable
fun MapEventCard(
    event: EventResponse,
    isLoggedIn: Boolean,
    message: String,
    onRegisterClick: () -> Unit,
    onClose: () -> Unit,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Dirección: ${event.address}")
                Text("Creado por: ${event.createdByUserName}")
                Text("ID: ${event.id}")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoPill("Categoría ${event.category}")
                    InfoPill("Cupo ${event.maxParticipants}")
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (message.isNotBlank()) {
                    Text(message)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoggedIn) {
                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Inscribirme")
                    }
                } else {
                    Text("Inicia sesión para inscribirte")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onClose) {
                    Text("Cerrar")
                }
            }

            EventImageFromUrl(
                imageUrl = event.imageUrl,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(18.dp)),
                onClick = onImageClick
            )
        }
    }
}
@Composable
fun MapTestScreen(
    events: List<EventResponse>,
    onMapLongPress: (LatLng) -> Unit = {},
    onMarkerClick: (EventResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapReady by remember { mutableStateOf(false) }
    val markerEventMap = remember { mutableMapOf<Long, EventResponse>() }

    val mapView = remember {
        MapLibre.getInstance(context)

        val mapOptions = MapLibreMapOptions()
            .textureMode(true)

        MapView(context, mapOptions).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            onCreate(Bundle())

            getMapAsync { map ->
                mapLibreMapRef = map

                map.setOnMarkerClickListener { marker ->
                    val event = markerEventMap[marker.id]
                    if (event != null) {
                        onMarkerClick(event)
                        true
                    } else {
                        false
                    }
                }

                map.addOnMapLongClickListener { point ->
                    onMapLongPress(point)
                    true
                }

                map.setStyle(
                    Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright")
                ) {
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(6.2442, -75.5812))
                        .zoom(10.5)
                        .build()

                    mapReady = true
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        update = {
            val map = mapLibreMapRef ?: return@AndroidView
            if (!mapReady) return@AndroidView

            map.removeAnnotations()
            markerEventMap.clear()

            events.forEach { event ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(event.latitude, event.longitude))
                        .title(event.name)
                        .snippet(event.address)
                )
                markerEventMap[marker.id] = event
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
@Composable
fun ProfileAvatarButton(
    profileImageUri: Uri?,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(profileImageUri) {
        try {
            if (profileImageUri == null) {
                null
            } else {
                context.contentResolver.openInputStream(profileImageUri)?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFFECECEC))
            .border(1.dp, Color.LightGray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                tint = Color.DarkGray
            )
        }
    }
}
@Composable
fun ProfileImageScreen(
    onBackToHome: () -> Unit,
    onImageUploaded: (Uri) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultText by remember { mutableStateOf("Selecciona una imagen para tu perfil") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Imagen de perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        PickerLikeField(
            label = "Imagen de perfil",
            value = if (selectedImageUri == null) "" else "Imagen seleccionada",
            placeholder = "Seleccionar imagen",
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedImageUri == null) {
                    resultText = "Selecciona una imagen primero"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val imagePart = createProfileImagePartFromUri(
                            context = context,
                            uri = selectedImageUri!!
                        )

                        val response = RetrofitInstance.api.uploadProfileImage(
                            token = "Bearer $savedToken",
                            file = imagePart
                        )

                        if (response.isSuccessful) {
                            onImageUploaded(selectedImageUri!!)
                            resultText = "Imagen de perfil cargada correctamente"
                            onBackToHome()
                        } else {
                            val errorText = response.errorBody()?.string().orEmpty()
                            resultText = "Error subiendo imagen: ${response.code()} - $errorText"
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción subiendo imagen: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Subir imagen")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al mapa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
    }
}
@Composable
fun MapShellScreen(
    isLoggedIn: Boolean,
    profileImageUri: Uri?,
    mapEvents: List<EventResponse>,
    selectedMapEvent: EventResponse?,
    mapMessage: String,
    onOpenLogin: () -> Unit,
    onOpenRegister: () -> Unit,
    onOpenCreateEvent: () -> Unit,
    onOpenEventsList: () -> Unit,
    onOpenRegisteredEvents: () -> Unit,
    onOpenPendingRequests: () -> Unit,
    onOpenUpdateEvent: () -> Unit,
    onOpenProfileImage: () -> Unit,
    onOpenChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onMapLongPress: (Double, Double) -> Unit,
    onMarkerClick: (EventResponse) -> Unit,
    onDismissSelectedEvent: () -> Unit,
    onRegisterToSelectedEvent: () -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var showFullScreenImage by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        MapTestScreen(
            events = mapEvents,
            onMapLongPress = { point ->
                onMapLongPress(point.latitude, point.longitude)
            },
            onMarkerClick = onMarkerClick
        )

        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.18f))
                    .clickable { isMenuOpen = false }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 56.dp, end = 20.dp),
            horizontalAlignment = Alignment.End
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { isMenuOpen = !isMenuOpen },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir menú"
                    )
                }
            }

            if (isMenuOpen) {
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.width(280.dp),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Menú",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f)
                            )

                            if (isLoggedIn) {
                                ProfileAvatarButton(
                                    profileImageUri = profileImageUri,
                                    onClick = {
                                        isMenuOpen = false
                                        onOpenProfileImage()
                                    }
                                )
                            }
                        }

                        if (!isLoggedIn) {
                            MenuActionButton("Iniciar sesión") {
                                isMenuOpen = false
                                onOpenLogin()
                            }

                            MenuActionButton("Registrarse") {
                                isMenuOpen = false
                                onOpenRegister()
                            }
                        } else {
                            MenuActionButton("Crear evento") {
                                isMenuOpen = false
                                onOpenCreateEvent()
                            }

                            MenuActionButton("Ver eventos") {
                                isMenuOpen = false
                                onOpenEventsList()
                            }

                            MenuActionButton("Mis inscripciones") {
                                isMenuOpen = false
                                onOpenRegisteredEvents()
                            }

                            MenuActionButton("Solicitudes pendientes") {
                                isMenuOpen = false
                                onOpenPendingRequests()
                            }

                            MenuActionButton("Actualizar evento") {
                                isMenuOpen = false
                                onOpenUpdateEvent()
                            }

                            MenuActionButton("Imagen de perfil") {
                                isMenuOpen = false
                                onOpenProfileImage()
                            }

                            MenuActionButton("Cambiar contraseña") {
                                isMenuOpen = false
                                onOpenChangePassword()
                            }

                            MenuActionButton("Cerrar sesión") {
                                isMenuOpen = false
                                onLogout()
                            }
                        }
                    }
                }
            }
        }

        if (selectedMapEvent != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                MapEventCard(
                    event = selectedMapEvent,
                    isLoggedIn = isLoggedIn,
                    message = mapMessage,
                    onRegisterClick = onRegisterToSelectedEvent,
                    onClose = {
                        showFullScreenImage = false
                        onDismissSelectedEvent()
                    },
                    onImageClick = {
                        if (!selectedMapEvent.imageUrl.isNullOrBlank()) {
                            showFullScreenImage = true
                        }
                    }
                )
            }
        }

        if (
            showFullScreenImage &&
            selectedMapEvent != null &&
            !selectedMapEvent.imageUrl.isNullOrBlank()
        ) {
            FullScreenEventImageDialog(
                imageUrl = selectedMapEvent.imageUrl!!,
                onDismiss = { showFullScreenImage = false }
            )
        }

        if (selectedMapEvent == null && mapMessage.isNotBlank()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = mapMessage,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
@Composable
fun MapRootScreen() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("map") }
    var registeredEmail by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("Ingresa tus datos") }
    var isLoggedIn by remember { mutableStateOf(!tokenManager.getToken().isNullOrBlank()) }
    var selectedLatitude by remember { mutableStateOf<Double?>(null) }
    var selectedLongitude by remember { mutableStateOf<Double?>(null) }

    var mapEvents by remember { mutableStateOf<List<EventResponse>>(emptyList()) }
    var selectedMapEvent by remember { mutableStateOf<EventResponse?>(null) }
    var mapMessage by remember { mutableStateOf("") }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    fun reloadMapEvents() {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getEvents()
                if (response.isSuccessful) {
                    mapEvents = response.body().orEmpty()
                } else {
                    mapMessage = "Error cargando eventos del mapa: ${response.code()}"
                }
            } catch (e: Exception) {
                mapMessage = "Excepción cargando eventos del mapa: ${e.message}"
            }
        }
    }

    fun registerToSelectedEvent() {
        val event = selectedMapEvent ?: return

        scope.launch {
            try {
                val savedToken = tokenManager.getToken()

                if (savedToken.isNullOrBlank()) {
                    selectedMapEvent = null
                    loginMessage = "Inicia sesión para inscribirte"
                    currentScreen = "login"
                    return@launch
                }

                val response = RetrofitInstance.api.registerToEvent(
                    token = "Bearer $savedToken",
                    eventId = event.id,
                    cancellationReason = ""
                )

                if (response.isSuccessful) {
                    selectedMapEvent = null
                    mapMessage = "Inscripción exitosa"
                } else {
                    val errorText = response.errorBody()?.string().orEmpty()

                    mapMessage = when {
                        errorText.contains("Ya estás registrado en este evento", ignoreCase = true) ->
                            "Ya estás inscrito en este evento"

                        errorText.contains("No puedes registrarte a tu propio evento", ignoreCase = true) ->
                            "No puedes inscribirte a tu propio evento"

                        errorText.contains("El evento ya finalizó", ignoreCase = true) ->
                            "El evento ya finalizó"

                        errorText.contains("El evento ya está lleno", ignoreCase = true) ->
                            "El evento ya está lleno"

                        errorText.contains("Evento no encontrado", ignoreCase = true) ->
                            "Evento no encontrado"

                        else ->
                            "Error al inscribirse: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                mapMessage = "Excepción al inscribirse: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        reloadMapEvents()
    }

    when (currentScreen) {
        "map" -> {
            MapShellScreen(
                isLoggedIn = isLoggedIn,
                profileImageUri = profileImageUri,
                mapEvents = mapEvents,
                selectedMapEvent = selectedMapEvent,
                mapMessage = mapMessage,
                onOpenLogin = { currentScreen = "login" },
                onOpenRegister = { currentScreen = "register" },
                onOpenCreateEvent = { currentScreen = "createEvent" },
                onOpenEventsList = { currentScreen = "eventsList" },
                onOpenRegisteredEvents = { currentScreen = "registeredEvents" },
                onOpenPendingRequests = { currentScreen = "pendingRequests" },
                onOpenUpdateEvent = { currentScreen = "updateEvent" },
                onOpenProfileImage = { currentScreen = "profileImage" },
                onOpenChangePassword = { currentScreen = "changePassword" },
                onLogout = {
                    tokenManager.clearToken()
                    isLoggedIn = false
                    registeredEmail = ""
                    loginMessage = "Sesión cerrada"
                    selectedMapEvent = null
                    mapMessage = ""
                    currentScreen = "map"
                },
                onMapLongPress = { lat, lng ->
                    if (isLoggedIn) {
                        selectedLatitude = lat
                        selectedLongitude = lng
                        currentScreen = "createEvent"
                    } else {
                        loginMessage = "Inicia sesión para crear un evento desde el mapa"
                        currentScreen = "login"
                    }
                },
                onMarkerClick = { event ->
                    selectedMapEvent = event
                    mapMessage = ""
                },
                onDismissSelectedEvent = {
                    selectedMapEvent = null
                    mapMessage = ""
                },
                onRegisterToSelectedEvent = {
                    registerToSelectedEvent()
                }
            )
        }

        "login" -> {
            LoginScreen(
                onGoToRegister = { currentScreen = "register" },
                onLoginSuccess = {
                    isLoggedIn = true
                    currentScreen = "map"
                    reloadMapEvents()
                },
                initialEmail = registeredEmail,
                initialMessage = loginMessage
            )
        }

        "register" -> {
            RegisterScreen(
                onBackToLogin = { currentScreen = "login" },
                onRegisterSuccess = { email ->
                    registeredEmail = email
                    loginMessage = "Cuenta creada correctamente. Ahora inicia sesión"
                    currentScreen = "login"
                }
            )
        }

        "createEvent" -> {
            CreateEventScreen(
                onBackToHome = {
                    reloadMapEvents()
                    currentScreen = "map"
                },
                initialLatitude = selectedLatitude,
                initialLongitude = selectedLongitude
            )
        }

        "eventsList" -> {
            EventsListScreen(
                onBackToHome = {
                    reloadMapEvents()
                    currentScreen = "map"
                }
            )
        }

        "registeredEvents" -> {
            RegisteredEventsScreen(
                onBackToHome = { currentScreen = "map" }
            )
        }

        "pendingRequests" -> {
            PendingRequestsScreen(
                onBackToHome = { currentScreen = "map" }
            )
        }

        "updateEvent" -> {
            UpdateEventScreen(
                onBackToHome = {
                    reloadMapEvents()
                    currentScreen = "map"
                }
            )
        }

        "profileImage" -> {
            ProfileImageScreen(
                onBackToHome = { currentScreen = "map" },
                onImageUploaded = { uri ->
                    profileImageUri = uri
                }
            )
        }

        "changePassword" -> {
            ChangePasswordScreen(
                onBackToHome = { currentScreen = "map" }
            )
        }
    }
}
fun participantStatusLabel(status: Int): String {
    return when (status) {
        0 -> "Pendiente"
        1 -> "Aprobado"
        2 -> "Rechazado"
        3 -> "Cancelado"
        4 -> "Asistió"
        else -> "Desconocido"
    }
}
@Composable
fun RegisteredEventsScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var events by remember { mutableStateOf<List<EventResponse>>(emptyList()) }
    var resultText by remember { mutableStateOf("Cargando eventos donde estoy inscrito...") }

    LaunchedEffect(Unit) {
        try {
            val savedToken = tokenManager.getToken()

            if (savedToken.isNullOrBlank()) {
                resultText = "No hay sesión activa"
                return@LaunchedEffect
            }

            val response = RetrofitInstance.api.getEventsIAmRegistered(
                token = "Bearer $savedToken"
            )

            if (response.isSuccessful) {
                events = response.body().orEmpty()
                resultText = if (events.isEmpty()) {
                    "No estás inscrito en ningún evento"
                } else {
                    ""
                }
            } else {
                resultText = "Error al cargar mis eventos: ${response.code()}"
            }
        } catch (e: Exception) {
            resultText = "Excepción al cargar mis eventos: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Mis eventos inscritos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty()) {
            Text(resultText)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(events) { event ->
                    EventListCard(
                        event = event,
                        onClick = { }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al mapa")
        }
    }
}
@Composable
fun PendingRequestsScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var events by remember { mutableStateOf<List<EventResponse>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<EventResponse?>(null) }
    var pendingParticipants by remember { mutableStateOf<List<EventParticipantResponse>>(emptyList()) }
    var resultText by remember { mutableStateOf("Cargando eventos...") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getEvents()

            if (response.isSuccessful) {
                events = response.body().orEmpty()
                resultText = if (events.isEmpty()) {
                    "No hay eventos disponibles"
                } else {
                    ""
                }
            } else {
                resultText = "Error cargando eventos: ${response.code()}"
            }
        } catch (e: Exception) {
            resultText = "Excepción cargando eventos: ${e.message}"
        }
    }

    LaunchedEffect(selectedEvent) {
        if (selectedEvent != null) {
            try {
                val savedToken = tokenManager.getToken()

                if (savedToken.isNullOrBlank()) {
                    resultText = "No hay sesión activa"
                    return@LaunchedEffect
                }

                val response = RetrofitInstance.api.getPendingRequestsAsync(
                    token = "Bearer $savedToken",
                    eventId = selectedEvent!!.id
                )

                if (response.isSuccessful) {
                    pendingParticipants = response.body().orEmpty()
                    resultText = if (pendingParticipants.isEmpty()) {
                        "No hay solicitudes pendientes para este evento"
                    } else {
                        ""
                    }
                } else {
                    resultText = "Error cargando solicitudes: ${response.code()}"
                }
            } catch (e: Exception) {
                resultText = "Excepción cargando solicitudes: ${e.message}"
            }
        }
    }

    if (selectedEvent == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Selecciona un evento",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty()) {
                Text(resultText)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(events) { event ->
                        EventListCard(
                            event = event,
                            onClick = { selectedEvent = event }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToHome) {
                Text("Volver al mapa")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Solicitudes pendientes",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Evento: ${selectedEvent!!.name}")
                    Text("ID: ${selectedEvent!!.id}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pendingParticipants.isEmpty()) {
                Text(resultText)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pendingParticipants) { participant ->
                        PendingParticipantCard(
                            participant = participant,
                            onApprove = {
                                scope.launch {
                                    try {
                                        val savedToken = tokenManager.getToken()

                                        if (savedToken.isNullOrBlank()) {
                                            resultText = "No hay sesión activa"
                                            return@launch
                                        }

                                        val response = RetrofitInstance.api.approveOrRejectParticipant(
                                            token = "Bearer $savedToken",
                                            request = ManageParticipantRequest(
                                                eventId = selectedEvent!!.id,
                                                userId = participant.userId,
                                                approve = true
                                            )
                                        )

                                        if (response.isSuccessful) {
                                            pendingParticipants =
                                                pendingParticipants.filterNot { it.userId == participant.userId }
                                            resultText = "Participante aprobado"
                                        } else {
                                            resultText = "Error al aprobar: ${response.code()}"
                                        }
                                    } catch (e: Exception) {
                                        resultText = "Excepción al aprobar: ${e.message}"
                                    }
                                }
                            },
                            onReject = {
                                scope.launch {
                                    try {
                                        val savedToken = tokenManager.getToken()

                                        if (savedToken.isNullOrBlank()) {
                                            resultText = "No hay sesión activa"
                                            return@launch
                                        }

                                        val response = RetrofitInstance.api.approveOrRejectParticipant(
                                            token = "Bearer $savedToken",
                                            request = ManageParticipantRequest(
                                                eventId = selectedEvent!!.id,
                                                userId = participant.userId,
                                                approve = false
                                            )
                                        )

                                        if (response.isSuccessful) {
                                            pendingParticipants =
                                                pendingParticipants.filterNot { it.userId == participant.userId }
                                            resultText = "Participante rechazado"
                                        } else {
                                            resultText = "Error al rechazar: ${response.code()}"
                                        }
                                    } catch (e: Exception) {
                                        resultText = "Excepción al rechazar: ${e.message}"
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (resultText.isNotBlank()) {
                Text(resultText)
                Spacer(modifier = Modifier.height(12.dp))
            }

            TextButton(onClick = { selectedEvent = null }) {
                Text("Volver a la lista")
            }

            TextButton(onClick = onBackToHome) {
                Text("Volver al mapa")
            }
        }
    }
}
