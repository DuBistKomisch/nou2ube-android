package es.jakebarn.nou2ube.data

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "users")
class User : Resource() {
    lateinit var email: String
    @field:Json(name = "authentication-token")
    lateinit var authenticationToken: String
}
