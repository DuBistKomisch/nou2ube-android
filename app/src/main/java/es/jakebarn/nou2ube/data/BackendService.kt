package es.jakebarn.nou2ube.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import es.jakebarn.nou2ube.R
import moe.banana.jsonapi2.JsonApiConverterFactory
import moe.banana.jsonapi2.ResourceAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface BackendService {
    @GET("auth/sign_in")
    suspend fun signIn(@Query("code") code: String): User

    @GET("auth/restore")
    suspend fun restore(): User

    @GET("subscriptions")
    suspend fun getSubscriptions(): List<Subscription>

    @GET("items")
    suspend fun getItems(): List<Item>
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
        .add(Subscription::class.java)
        .add(Channel::class.java)
        .add(Item::class.java)
        .add(Video::class.java)
        .build()

    val moshi = Moshi.Builder()
        .add(adapterFactory)
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(context.getString(R.string.server_origin))
        .addConverterFactory(JsonApiConverterFactory.create(moshi))
        .build()

    return retrofit.create(BackendService::class.java)
}