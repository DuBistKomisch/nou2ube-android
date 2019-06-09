package es.jakebarn.nou2ube.data

import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "subscriptions")
class Subscription : Resource() {
    lateinit var channel: HasOne<Channel>
}
