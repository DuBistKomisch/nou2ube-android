package es.jakebarn.nou2ube.data

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "channels")
class Channel : Resource() {
    @field:Json(name = "api-id")
    lateinit var apiId: String
    lateinit var title: String
    lateinit var thumbnail: String
}
