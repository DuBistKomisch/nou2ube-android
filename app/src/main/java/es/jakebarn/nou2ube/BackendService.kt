package es.jakebarn.nou2ube

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendService {
    @GET("auth/sign_in")
    fun signIn(@Query("code") code: String): Call<User>
}