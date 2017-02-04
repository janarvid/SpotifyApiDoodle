package org.veggeberg.spotify;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.AlbumsForArtistRequest;
import com.wrapper.spotify.methods.AlbumsRequest;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.veggeberg.jmtools.domain.TopProgAlbum;
import org.veggeberg.jmtools.rym.TopRymReader;

public class SpotifyApiDoodleJava {
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
		     //System.out.println(album.getName() + " " + album.getAlbumType());
		     ids.add(album.getId());
		     //findTracks(album);
		   }
		   System.out.println("ids = " + ids);
		   final AlbumsRequest albumRequest= api.getAlbums(ids).build();
		   for (Album album : albumRequest.get()) {
			   System.out.println("  " + album.getName() + " " + album.getAlbumType() + " " + album.getReleaseDate() + 
					   " " + album.getGenres());
			   /*
			   Page<SimpleTrack> tracks = album.getTracks();
			   for (SimpleTrack track: tracks.getItems()) {
				   System.out.println("    " + track.getTrackNumber() + " " + track.getName() + 
						   " " + track.getDuration());
			   }
			   */
		   }

		} catch (Exception e) {
		   e.printStackTrace();
		}
	}
	
	private static Artist searchArtist(String... artistNames) {
		Artist ret = null;
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
			   ret = artist;
			   if (i > 0) break;
		   }
		   

		} catch (Exception e) {
		   e.printStackTrace();
		}
		}
		return ret;
	}
	
	static final String USER_CACHE_DIR = System.getProperty("user.home") + "/.jmtools/cache";
	private static List<TopProgAlbum> rymAlbums;
	private static List<TopProgAlbum> getRymAlbums() {
		if (rymAlbums == null) {
			Set<TopProgAlbum> set = db.getHashSet("rymTopAlbumsCache");
			if (set.size() == 0) {
				TopRymReader reader = new TopRymReader();
				List<TopProgAlbum> albums = reader.getAlbums();
				set.addAll(albums);
			}
			db.commit();
			rymAlbums = new ArrayList<TopProgAlbum>();
			rymAlbums.addAll(set);
			rymAlbums.sort(new RymComparator());
		}
		return rymAlbums;
	}
	
	static class RymComparator implements Comparator<TopProgAlbum> {
		public int compare(TopProgAlbum arg0, TopProgAlbum arg1) {
			return (arg0.getQwr() < arg1.getQwr()) ? 1 : -1;
		}
	}
	
	static DB db;
	static void init() {
		final File topAlbumsCacheFile = 
				new File(USER_CACHE_DIR + "/rymTopAlbumsCache");
		 db = DBMaker.newFileDB(topAlbumsCacheFile)
		 .closeOnJvmShutdown()
		 .make();
    }
	
	private static OrientGraph graph;
	
	private static void dumpGraph() {
		for (Vertex vertex : graph.getVertices()) {
			System.out.println(vertex);
			for (Vertex v : vertex.getVertices(Direction.BOTH, "lives")) {
				System.out.println("  " + v);
			}
			for (String key : vertex.getPropertyKeys()) {
				System.out.println("    " + key + " = " + vertex.getProperty(key));
			}
		}
	}
	
	private static void upsertArtist(Artist artist) {
		Vertex v = graph.addVertex("class:SpotifyArtist");
//		v.setProperty("externalUrls", artist.getExternalUrls().getExternalUrls());
		v.setProperty("genres", artist.getGenres());
//		v.setProperty("followers", artist.getFollowers().getTotal());
		v.setProperty("href", artist.getHref());
		v.setProperty("artistId", artist.getId());
//		v.setProperty("images", artist.getImages());
		v.setProperty("name", artist.getName());
		v.setProperty("popularity", artist.getPopularity());
		v.setProperty("type", artist.getType().toString());
		v.setProperty("uri", artist.getUri());
	}

	public static void main(String[] args) {
//		synchronous();
		init();
		graph = new OrientGraph("memory:testdb");
//		TopRymReader reader = new TopRymReader();
//		List<TopProgAlbum> albums = reader.getAlbums();
		List<TopProgAlbum> albums = getRymAlbums();
		Set<String> artists = new LinkedHashSet<String>(); 
		for (TopProgAlbum album : albums.subList(0, 1)) {
			System.out.println(album);
			artists.add(album.getArtist());
		}
		for (String ra : artists) {
			System.out.println(ra);
			Artist artist = searchArtist(ra);
			upsertArtist(artist);
			
		}
		dumpGraph();
		graph.shutdown();
		System.exit(0);
		searchArtist(
//				"Big Big Train",
				"Chevelle"
//				"Genesis",
//				"IQ"
//				"Jethro Tull"
				);
//		searchAlbums();
	}

}
