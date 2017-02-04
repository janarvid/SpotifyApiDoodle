package org.veggeberg.spotify

import com.wrapper.spotify.Api
import java.util.*

/**
 * Created by janni on 08.10.2016.
 */
object ClientAccess {
    internal val clientId = "1dda9d7c4eb34b1a9aef869a99ef8d37"
    internal val clientSecret = "e89b8a20cf1a41a0a5eadaf9213b33e6"
    val redirectURI = "http://localhost:8888/callback"
    // Create an API instance. The default instance connects to https://api.spotify.com/.
    //private val api = Api.DEFAULT_API



    @JvmStatic fun main(args: Array<String>) {
        try {
            val api = Api.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectURI(redirectURI)
                    .build();

/* Create a request object. */
            val request = api.clientCredentialsGrant().build();
            println ("request = " + request)
            val accessToken = request.get().accessToken
            //accessToken.
            println ("accessToken = " + accessToken)
            api.setAccessToken(accessToken);
            /* Set the necessary scopes that the application will need from the user */
            val scopes = Arrays.asList("user-read-private", "user-read-email")

/* Set a state. This is used to prevent cross site request forgeries. */
            val state = "someExpectedStateString"

            val authorizeURL = api.createAuthorizeURL(scopes, state)
            println(api.getPlaylistsForUser("jan.a.veggeberg@gmail.com").build().get().items);
            //println (api.me.build().get().displayName)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}