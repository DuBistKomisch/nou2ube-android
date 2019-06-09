package es.jakebarn.nou2ube.data

import android.content.Context
import es.jakebarn.nou2ube.Session
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor(context: Context): Interceptor {
    private val session = Session.getInstance(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        if (session.signedIn) {
            requestBuilder.header("X-User-Email", session.email!!)
            requestBuilder.header("X-User-Token", session.authenticationToken!!)
        }

        return chain.proceed(requestBuilder.build())
    }
}