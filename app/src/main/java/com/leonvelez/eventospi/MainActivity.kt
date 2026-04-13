package com.leonvelez.eventospi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import com.leonvelez.eventospi.data.remote.RetrofitInstance
import com.leonvelez.eventospi.ui.theme.EventosPITheme
import androidx.compose.ui.platform.LocalContext
import com.leonvelez.eventospi.data.TokenManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.OutlinedTextField
import com.leonvelez.eventospi.data.model.EventRequest
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventosPITheme {
                MaterialTheme {
                    AppScreen()
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
            onGoToChangePassword = { showChangePasswordScreen = true },
            onGoToEventsList = { showEventsListScreen = true },
            onGoToUpdateEvent = { showUpdateEventScreen = true },
            onLogout = {
                showHomeScreen = false
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
@Composable
fun CreateEventScreen(
    onBackToHome: () -> Unit
) {
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
    var imageUrl by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Completa los datos del evento") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
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
            label = { Text("Fecha inicio (YYYY-MM-DDTHH:MM:SS)") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("Fecha fin (YYYY-MM-DDTHH:MM:SS)") }
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
            label = { Text("Categoría (número)") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio opcional") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("ImageUrl opcional") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (
                    name.isBlank() ||
                    description.isBlank() ||
                    startDate.isBlank() ||
                    endDate.isBlank() ||
                    latitude.isBlank() ||
                    longitude.isBlank() ||
                    address.isBlank() ||
                    maxParticipants.isBlank() ||
                    category.isBlank()
                ) {
                    resultText = "Completa los campos obligatorios"
                    return@Button
                }

                scope.launch {
                    try {
                        val savedToken = tokenManager.getToken()

                        if (savedToken.isNullOrBlank()) {
                            resultText = "No hay sesión activa"
                            return@launch
                        }

                        val event = EventRequest(
                            name = name,
                            description = description,
                            startDate = "${startDate}Z",
                            endDate = "${endDate}Z",
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            address = address,
                            maxParticipants = maxParticipants.toInt(),
                            isPublic = true,
                            category = category.toInt(),
                            price = if (price.isBlank()) null else price.toDouble(),
                            imageUrl = if (imageUrl.isBlank()) null else imageUrl
                        )

                        val response = RetrofitInstance.api.createEvent(
                            token = "Bearer $savedToken",
                            event = event
                        )

                        if (response.isSuccessful) {
                            resultText = "Evento creado correctamente"
                            onBackToHome()
                        } else {
                            resultText = "Error: ${response.code()}"
                            Log.e(
                                "CREATE_EVENT_ERROR",
                                "Código: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción: ${e.message}"
                        Log.e("CREATE_EVENT_EXCEPTION", e.toString())
                    }
                }
            }
        ) {
            Text("Crear evento")
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
fun EventsListScreen(
    onBackToHome: () -> Unit
) {
    var resultText by remember { mutableStateOf("Cargando eventos...") }
    val scope = rememberCoroutineScope()
    var deleteId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

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
    var imageUrl by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Completa los datos del evento a actualizar") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Actualizar evento",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Fecha inicio (YYYY-MM-DDTHH:MM:SS)") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Fecha fin (YYYY-MM-DDTHH:MM:SS)") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitud") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitud") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = maxParticipants, onValueChange = { maxParticipants = it }, label = { Text("Máximo participantes") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría (número)") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio opcional") })
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("ImageUrl opcional") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (
                    id.isBlank() ||
                    name.isBlank() ||
                    description.isBlank() ||
                    startDate.isBlank() ||
                    endDate.isBlank() ||
                    latitude.isBlank() ||
                    longitude.isBlank() ||
                    address.isBlank() ||
                    maxParticipants.isBlank() ||
                    category.isBlank()
                ) {
                    resultText = "Completa los campos obligatorios"
                    return@Button
                }

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
                            startDate = "${startDate}Z",
                            endDate = "${endDate}Z",
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            address = address,
                            maxParticipants = maxParticipants.toInt(),
                            isPublic = true,
                            category = category.toInt(),
                            price = if (price.isBlank()) null else price.toDouble(),
                            imageUrl = if (imageUrl.isBlank()) null else imageUrl
                        )

                        val response = RetrofitInstance.api.updateEvent(
                            token = "Bearer $savedToken",
                            event = event
                        )

                        if (response.isSuccessful) {
                            resultText = "Evento actualizado correctamente"
                            onBackToHome()
                        } else {
                            resultText = "Error al actualizar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        resultText = "Excepción al actualizar: ${e.message}"
                    }
                }
            }
        ) {
            Text("Actualizar evento")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToHome) {
            Text("Volver al inicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText)
    }
}