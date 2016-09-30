package org.veggeberg.spotify

import com.wrapper.spotify.Api
import com.wrapper.spotify.methods.AlbumRequest
import com.wrapper.spotify.methods.AlbumsForArtistRequest
import com.wrapper.spotify.methods.AlbumsRequest
import com.wrapper.spotify.methods.ArtistSearchRequest
import com.wrapper.spotify.models.*

import java.util.ArrayList

object SpotifyApiDoodle {
    internal val clientID = "1dda9d7c4eb34b1a9aef869a99ef8d37"
    internal val clientSecret = "e89b8a20cf1a41a0a5eadaf9213b33e6"
    // Create an API instance. The default instance connects to https://api.spotify.com/.
    private val api = Api.DEFAULT_API

    fun synchronous() {
        // Create a request object for the type of request you want to make
        val request = api.getAlbum("7e0ij2fpWaxOEHv5fUYZjd").build()

        // Retrieve an album
        try {
            val album = request.get()
            println("album = " + album.name)
            println("albumYear = " + album.releaseDate)
            println("artist = " + album.artists[0].name)

            for (track in album.tracks.items) {
                println("  " + track.name)
            }

            // Print the genres of the album
            val genres = album.genres
            for (genre in genres) {
                println(genre)
            }

        } catch (e: Exception) {
            println("Could not get albums.")
        }

    }

    //	public static void findTracks(SimpleAlbum album) {
    //		for (SimpleTrack track : album.get.getItems()) {
    //			System.out.println(track.getName());
    //		}
    //	}

    private fun searchAlbums(artist: Artist) {
        val request = api.getAlbumsForArtist(artist.id).limit(20).build()

        try {
            val albumSearchResult = request.get()

            val ids = ArrayList<String>()
            val albums = albumSearchResult.items
            println("Found " + albums.size + " albums.")
            for (album in albums) {
                //String year = album.get
                println(album.name + " " + album.albumType)
                ids.add(album.id)
                //findTracks(album);
            }
            println("ids = " + ids)
            val albumRequest = api.getAlbums(ids).build()
            for (album in albumRequest.get()) {
                println("  " + album.name + " " + album.albumType + " " + album.releaseDate)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun searchArtist(vararg artistNames: String) {
        for (artistName in artistNames) {
            val request = api.searchArtists(artistName).limit(10)//.market("NO")
                    .build()

            try {
                val artistSearchResult = request.get()
                val artists = artistSearchResult.items

                println("I've found " + artistSearchResult.total + " artists!")

                var i = 0
                for (artist in artists) {
                    println(artist.name + "  genres=" + artist.genres)
                    searchAlbums(artist)
                    i++
                    if (i > 0) break
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @JvmStatic fun main(args: Array<String>) {
        //		synchronous();
        searchArtist(
                "Jethro Tull",
                "Genesis",
                "Big Big Train")
        //		searchAlbums();
    }

}
