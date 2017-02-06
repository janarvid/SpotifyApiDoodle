package org.veggeberg.spotify;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
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
	// Create an API instance. The default instance connects to
	// https://api.spotify.com/.
	static private Api api = Api.DEFAULT_API;
	static FramedGraphFactory factory = new FramedGraphFactory(
			new GremlinGroovyModule()); // (1) Factories should be reused for
										// performance and memory conservation.
	static FramedGraph<OrientGraph> framedGraph;

	public static void synchronous() {
		// Create a request object for the type of request you want to make
		AlbumRequest request = api.getAlbum("7e0ij2fpWaxOEHv5fUYZjd").build();

		// Retrieve an album
		try {
			Album album = request.get();
			System.out.println("album = " + album.getName());
			System.out.println("albumYear = " + album.getReleaseDate());
			System.out.println("artist = "
					+ album.getArtists().get(0).getName());

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

	// public static void findTracks(SimpleAlbum album) {
	// for (SimpleTrack track : album.get.getItems()) {
	// System.out.println(track.getName());
	// }
	// }

	private static Album searchAlbums(Artist artist, String albumName) {
		Album ret = null;
		final AlbumsForArtistRequest request = api
				.getAlbumsForArtist(artist.getId()).limit(20).build();

		try {
			final Page<SimpleAlbum> albumSearchResult = request.get();

			List<String> ids = new ArrayList<String>();
			List<SimpleAlbum> albums = albumSearchResult.getItems();
			System.out.println("Found " + albums.size() + " albums.");
			for (SimpleAlbum album : albums) {
				// String year = album.get
				// System.out.println(album.getName() + " " +
				// album.getAlbumType());
				ids.add(album.getId());
				// findTracks(album);
			}
			System.out.println("ids = " + ids);
			final AlbumsRequest albumRequest = api.getAlbums(ids).build();
			for (Album album : albumRequest.get()) {
				System.out.println("  " + album.getName() + " "
						+ album.getAlbumType() + " " + album.getReleaseDate()
						+ " " + album.getGenres());
				/*
				 * Page<SimpleTrack> tracks = album.getTracks(); for
				 * (SimpleTrack track: tracks.getItems()) {
				 * System.out.println("    " + track.getTrackNumber() + " " +
				 * track.getName() + " " + track.getDuration()); }
				 */
				if (ret == null && album.getName().equals(albumName)) {
					ret = album;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static Artist searchArtist(String artistName, String albumName) {
		Artist ret = null;
		final ArtistSearchRequest request = api.searchArtists(artistName)
		// .market("NO")
				.limit(10).build();

		try {
			final Page<Artist> artistSearchResult = request.get();
			final List<Artist> artists = artistSearchResult.getItems();

			System.out.println("I've found " + artistSearchResult.getTotal()
					+ " artists!");

			for (Artist artist : artists) {
				System.out.println(artist.getName() + "  genres="
						+ artist.getGenres());
				if (artistName.equals(artist.getName())) {
					Album album = searchAlbums(artist, albumName);
					ret = artist;
					break;
				} 
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	static final String USER_CACHE_DIR = System.getProperty("user.home")
			+ "/.jmtools/cache";
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
		final File topAlbumsCacheFile = new File(USER_CACHE_DIR
				+ "/rymTopAlbumsCache");
		db = DBMaker.newFileDB(topAlbumsCacheFile).closeOnJvmShutdown().make();
	}

	private static OrientGraph graph;

	private static void dumpGraph() {
		for (Vertex vertex : graph.getVertices()) {
			System.out.println(vertex);
			for (Vertex v : vertex.getVertices(Direction.BOTH, "lives")) {
				System.out.println("  " + v);
			}
			for (String key : vertex.getPropertyKeys()) {
				System.out.println("    " + key + " = "
						+ vertex.getProperty(key));
			}
		}
	}

	private static Artist findArtist(String artist) {
		Artist ret = null;
		for (Vertex vertex : graph.getVertices()) {
			if (artist.equals(vertex.getProperty("name"))) {
				ret = new Artist();
				ret.setName(vertex.getProperty("name").toString());
				break;
			}
		}
		System.out.println("ret = " + ret);
		return ret;
	}

	private static void upsertArtist(Artist artist) {
		SpotifyArtist sa = framedGraph.frame(
				graph.addVertex("class:SpotifyArtist"), SpotifyArtist.class);
		// v.setProperty("externalUrls",
		// artist.getExternalUrls().getExternalUrls());
		sa.setGenres(artist.getGenres());
		// v.setProperty("followers", artist.getFollowers().getTotal());
		sa.setHref(artist.getHref());
		sa.setArtistId(artist.getId());
		// v.setProperty("images", artist.getImages());
		sa.setName(artist.getName());
		sa.setPopularity(artist.getPopularity());
		// sa.setProperty("type", artist.getType().toString());
		sa.setUri(artist.getUri());
	}

	public static void main(String[] args) {
		// synchronous();
		init();
		graph = new OrientGraph("memory:testdb");
		framedGraph = factory.create(graph); // Frame the graph.
		// TopRymReader reader = new TopRymReader();
		// List<TopProgAlbum> albums = reader.getAlbums();
		List<TopProgAlbum> albums = getRymAlbums();
		for (TopProgAlbum album : albums.subList(0, 1)) {
			System.out.println(album.getArtist());
			Artist artist = searchArtist(album.getArtist(), album.getTitle());
			upsertArtist(artist);
			findArtist(album.getArtist());
			upsertArtist(artist);
		}
		dumpGraph();
		graph.shutdown();
	}

}
