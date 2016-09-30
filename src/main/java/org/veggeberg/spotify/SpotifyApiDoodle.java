package org.veggeberg.spotify;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.AlbumsForArtistRequest;
import com.wrapper.spotify.methods.AlbumsRequest;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.models.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifyApiDoodle {
	static final String clientID = "1dda9d7c4eb34b1a9aef869a99ef8d37";
	static final String clientSecret = "e89b8a20cf1a41a0a5eadaf9213b33e6";
	// Create an API instance. The default instance connects to https://api.spotify.com/.
	static private Api api = Api.DEFAULT_API;

	public static void synchronous() {
		// Create a request object for the type of request you want to make
		AlbumRequest request = api.getAlbum("7e0ij2fpWaxOEHv5fUYZjd").build();

		// Retrieve an album
		try {
		  Album album = request.get();
		  System.out.println("album = " + album.getName());
		  System.out.println("albumYear = " + album.getReleaseDate());
		  System.out.println("artist = " + album.getArtists().get(0).getName());
		  
		  for (SimpleTrack track : album.getTracks().getItems()) {
			  System.out.println("  " + track.getName());
		  }

		  // Print the genres of the album
		  List<String> genres = album.getGenres(); 
		  for (String genre : genres) {
		    System.out.println(genre);
		  }

		} catch (Exception e) {
		  System.out.println("Could not get albums.");
		}
	}
	
//	public static void findTracks(SimpleAlbum album) {
//		for (SimpleTrack track : album.get.getItems()) {
//			System.out.println(track.getName());
//		}
//	}
	
	private static void searchAlbums(Artist artist) {
		final AlbumsForArtistRequest request = api.getAlbumsForArtist(artist.getId()).limit(20).build();

		try {
		   final Page<SimpleAlbum> albumSearchResult = request.get();

		   List<String> ids = new ArrayList<String>();
		   List<SimpleAlbum> albums = albumSearchResult.getItems();
		   System.out.println("Found " + albums.size() + " albums.");
		   for (SimpleAlbum album : albums) {
			   //String year = album.get
		     System.out.println(album.getName() + " " + album.getAlbumType());
		     ids.add(album.getId());
		     //findTracks(album);
		   }
		   System.out.println("ids = " + ids);
		   final AlbumsRequest albumRequest= api.getAlbums(ids).build();
		   for (Album album : albumRequest.get()) {
			   System.out.println("  " + album.getName() + " " + album.getAlbumType() + " " + album.getReleaseDate());
		   }

		} catch (Exception e) {
		   e.printStackTrace();
		}
	}
	
	private static void searchArtist(String... artistNames) {
		for (String artistName : artistNames) {
		final ArtistSearchRequest request = api.searchArtists(artistName)
				//.market("NO")
				.limit(10)
				.build();

		try {
		   final Page<Artist> artistSearchResult = request.get();
		   final List<Artist> artists = artistSearchResult.getItems();

		   System.out.println("I've found " + artistSearchResult.getTotal() + " artists!");

		   int i = 0;
		   for (Artist artist : artists) {
			   System.out.println(artist.getName() + "  genres="+artist.getGenres());
			   searchAlbums(artist);
			   i++;
			   if (i > 0) break;
		   }
		   

		} catch (Exception e) {
		   e.printStackTrace();
		}
		}
	}

	public static void main(String[] args) {
//		synchronous();
		searchArtist(
				"Jethro Tull",
				"Genesis",
				"Big Big Train");
//		searchAlbums();
	}

}
