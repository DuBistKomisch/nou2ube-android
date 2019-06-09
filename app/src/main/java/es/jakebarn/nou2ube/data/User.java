package es.jakebarn.nou2ube.data;

import com.squareup.moshi.Json;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "user")
public class User extends Resource {
    public String email;
    @Json(name = "authentication-token") public String authenticationToken;
}
