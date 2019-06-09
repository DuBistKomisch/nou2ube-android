package es.jakebarn.nou2ube.data

import android.content.Context
import com.squareup.moshi.Moshi
import es.jakebarn.nou2ube.R
import moe.banana.jsonapi2.JsonApiConverterFactory
import moe.banana.jsonapi2.ResourceAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendService {
    @GET("auth/sign_in")
    fun signIn(@Query("code") code: String): Call<User>

    @GET("auth/restore")
    fun restore(): Call<User>
}

fun BackendService(context: Context): BackendService {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(SessionInterceptor(context))
        .build()

    val adapterFactory = ResourceAdapterFactory.builder()
        .add(User::class.java)
        .build()

    val moshi = Moshi.Builder()
        .add(adapterFactory)
        .build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(context.getString(R.string.server_origin))
        .addConverterFactory(JsonApiConverterFactory.create(moshi))
        .build()

    return retrofit.create(BackendService::class.java)
}