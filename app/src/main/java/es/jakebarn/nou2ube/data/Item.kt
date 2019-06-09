package es.jakebarn.nou2ube.data

import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "items")
class Item : Resource() {
    lateinit var video: HasOne<Video>
    lateinit var state: String
}
