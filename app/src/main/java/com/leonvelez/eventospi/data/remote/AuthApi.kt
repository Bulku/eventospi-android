package com.leonvelez.eventospi.data.remote

import com.leonvelez.eventospi.data.model.LoginResponse
import com.leonvelez.eventospi.data.model.EventRequest
import com.leonvelez.eventospi.data.model.EventResponse
import com.leonvelez.eventospi.data.model.RegistrationRequest
import com.leonvelez.eventospi.data.model.EventParticipantResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.PUT



interface AuthApi {

    @POST("Login")
    suspend fun login(
        @Query("Email") email: String,
        @Query("Password") password: String
    ): Response<LoginResponse>

    @GET("GetUserAuthenticated")
    suspend fun getUserAuthenticated(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("ChangePassword")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Query("currentPassword") currentPassword: String,
        @Query("newPassword") newPassword: String,
        @Query("confirmNewPassword") confirmNewPassword: String
    ): Response<ResponseBody>

    @POST("Register")
    suspend fun register(
        @Query("FirstName") firstName: String,
        @Query("LastName") lastName: String,
        @Query("UserName") userName: String,
        @Query("Email") email: String,
        @Query("Password") password: String,
        @Query("ConfirmPassword") confirmPassword: String
    ): Response<ResponseBody>

    @POST("api/Event/Create")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body event: EventRequest
    ): Response<EventResponse>
    @GET("api/Event/GetEvents")
    suspend fun getEvents(): Response<List<EventResponse>>
    @DELETE("api/Event/Delete/{id}")
    suspend fun deleteEvent(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>
    @PUT("api/Event/Update")
    suspend fun updateEvent(
        @Header("Authorization") token: String,
        @Body event: EventRequest
    ): Response<EventResponse>
    @POST("RegisterToEvent")
    suspend fun registerToEvent(
        @Header("Authorization") token: String,
        @Query("EventId") eventId: Int,
        @Query("CancellationReason") cancellationReason: String = ""
    ): Response<EventParticipantResponse>

    @PUT("CancelRegistration")
    suspend fun cancelRegistration(
        @Header("Authorization") token: String,
        @Query("EventId") eventId: Int,
        @Query("CancellationReason") cancellationReason: String = ""
    ): Response<EventParticipantResponse>

    @GET("GetParticipantsByEventId")
    suspend fun getParticipantsByEventId(
        @Header("Authorization") token: String,
        @Query("eventId") eventId: Int
    ): Response<List<EventParticipantResponse>>
}