package es.jakebarn.nou2ube.data

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.util.*

@JsonApi(type = "videos")
class Video : Resource() {
    @field:Json(name = "api-id")
    lateinit var apiId: String
    lateinit var title: String
    lateinit var thumbnail: String
    lateinit var duration: Integer
    @field:Json(name = "published-at")
    lateinit var publishedAt: Date
}
