package org.veggeberg.spotify;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.AlbumsForArtistRequest;
import com.wrapper.spotify.methods.AlbumsRequest;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.TracksRequest;
import com.wrapper.spotify.models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

	private static boolean fuzzyAlbumEquals(String searchForAlbum,
			String albumName) {
		if (searchForAlbum.equals(albumName))
			return true;
		String sfa = searchForAlbum.toLowerCase();
		String an = albumName.toLowerCase();
		if (sfa.equals(an))
			return true;
		an = an.replaceFirst("\\(remastered\\)", "").trim();
		if (sfa.equals(an))
			return true;
		return false;
	}

	private static List<Track> getTracksForAlbum(Album album)
			throws IOException, WebApiException {
		List<Track> ret = new ArrayList<Track>();
		Page<SimpleTrack> tracks = album.getTracks();
		List<String> ids = new ArrayList<String>();
		for (SimpleTrack track : tracks.getItems()) {
			ids.add(track.getId());
		}
		final TracksRequest tracksRequest = api.getTracks(ids).build();
		for (Track track : tracksRequest.get()) {
			System.out.println("     " + track.getName() + " " + track.getId()
					+ " " + track.getDuration() + " " + album.getPopularity()
					+ " " + album.getGenres());
			ret.add(track);
		}
		return ret;
	}

	private static String genAlbumKey(Album album) {
		return album.getName() + album.getReleaseDate() + album.getAlbumType();
	}

	private static Collection<Album> getAlbumsForArtist(Artist artist)
			throws IOException, WebApiException {
		Map<String, Album> ret = new LinkedHashMap<String, Album>();
		final int lim = 20;
		for (int ioff = 0; ioff < 100; ioff += lim) {
			final AlbumsForArtistRequest request = api
					.getAlbumsForArtist(artist.getId()).limit(lim).offset(ioff)
					.build();
			System.out.println("request = " + request);
			final Page<SimpleAlbum> albumSearchResult = request.get();
			List<String> ids = new ArrayList<String>();
			List<SimpleAlbum> albums = albumSearchResult.getItems();
			System.out.println("Found " + albums.size() + " albums.");
			if (albums.size() <= 0)
				break;
			for (SimpleAlbum album : albums) {
				ids.add(album.getId());
			}
			System.out.println("ids = " + ids);
			final AlbumsRequest albumRequest = api.getAlbums(ids).build();
			for (Album album : albumRequest.get()) {
				String key = genAlbumKey(album);
				if (!ret.containsKey(key)) {
					ret.put(key, album);
				}
			}
		}
		return ret.values();
	}

	private static void printAlbum(Album album) {
		System.out.println("  " + album.getName() + " " + album.getId() + " "
				+ album.getAlbumType() + " " + album.getReleaseDate() + " "
				+ album.getGenres());
	}

	private static Artist searchArtist(String artistName) throws IOException,
			WebApiException {
		Artist ret = null;
		final ArtistSearchRequest request = api.searchArtists(artistName)
		// .market("NO")
				.limit(10).build();

		final Page<Artist> artistSearchResult = request.get();
		final List<Artist> artists = artistSearchResult.getItems();

		System.out.println("I've found " + artistSearchResult.getTotal()
				+ " artists!");

		for (Artist artist : artists) {
			System.out.println(artist.getName() + "  genres="
					+ artist.getGenres());
			if (artistName.equals(artist.getName())) {
				ret = artist;
				break;
			}
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
		int nv = 0;
		for (Vertex vertex : graph.getVertices()) {
			System.out.println(vertex);
			for (String key : vertex.getPropertyKeys()) {
				System.out.println("    " + key + " = "
						+ vertex.getProperty(key));
			}
			nv++;
			// dumpVertex(vertex, 0);
		}
		int ne = 0;
		for (Edge edge : graph.getEdges()) {
			System.out.println("edge = " + edge);
			ne++;
		}
		System.out.println("DB has " + nv + " Vertices and " + ne + " edges.");
	}

	static final String SPACES = "                                                  ";

	private static void dumpVertex(Vertex vertex, int indent) {
		String strIndent = SPACES.substring(0, indent);
		System.out.println(strIndent + vertex);
		for (String key : vertex.getPropertyKeys()) {
			System.out.println(strIndent + "  > " + key + " = "
					+ vertex.getProperty(key));
		}
		for (Edge edge : vertex.getEdges(Direction.OUT)) {
			System.out.println(strIndent + "  " + edge);
			Vertex v = edge.getVertex(Direction.IN);
			dumpVertex(v, indent + 2);
		}
	}

	private static SpotifyArtist findArtist(String artistName) {
		SpotifyArtist ret = null;
		for (Vertex vertex : graph.getVerticesOfClass("SpotifyArtist")) {
			SpotifyArtist sa = framedGraph.frame(vertex, SpotifyArtist.class);
			// System.out.println(sa.getName());
			if (artistName.equals(sa.getName())) {
				ret = sa;
				break;
			}
		}
		;
		// System.out.println("ret = " + ret);
		return ret;
	}

	private static SpotifyArtist upsertArtist(Artist artist) {
		SpotifyArtist sa = findArtist(artist.getName());
		if (sa == null) {
			sa = framedGraph.frame(graph.addVertex("class:SpotifyArtist"),
					SpotifyArtist.class);
		}
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
		return sa;
	}

	private static SpotifyAlbum upsertAlbum(Album album) {
		SpotifyAlbum sa = framedGraph.frame(
				graph.addVertex("class:SpotifyAlbum"), SpotifyAlbum.class);
		sa.setAlbumType(album.getAlbumType().toString());
		sa.setAvailableMarkets(album.getAvailableMarkets());
		// sa.setCopyrights(album.getCopyrights());
		// void setExternalIds(ExternalIds externalIds);
		// void setExternalUrls(ExternalUrls externalUrls);
		sa.setGenres(album.getGenres());
		sa.setHref(album.getHref());
		sa.setAlbumId(album.getId());
		// sa.setImages(List<Image> images);
		sa.setName(album.getName());
		sa.setPopularity(album.getPopularity());
		// void setTracks(Page<SimpleTrack> tracks);
		// void setType(SpotifyEntityType type);
		sa.setUri(album.getUri());
		sa.setReleaseDatePrecision(album.getReleaseDatePrecision());
		sa.setReleaseDate(album.getReleaseDate());
		return sa;
	}

	private static SpotifyTrack upsertTrack(Track track) {
		SpotifyTrack st = framedGraph.frame(
				graph.addVertex("class:SpotifyTrack"), SpotifyTrack.class);
		st.setAvailableMarkets(track.getAvailableMarkets());
		// void setExternalIds(ExternalIds externalIds);
		// void setExternalUrls(ExternalUrls externalUrls);
		st.setHref(track.getHref());
		st.setDiscNumber(track.getDiscNumber());
		st.setTrackNumber(track.getTrackNumber());
		st.setDuration(track.getDuration());
		st.setExplicit(track.isExplicit());
		st.setTrackId(track.getId());
		st.setName(track.getName());
		st.setPopularity(track.getPopularity());
		st.setUri(track.getUri());
		st.setPreviewUrl(track.getPreviewUrl());
		return st;
	}

	public static void main(String[] args) {
		// synchronous();
		try {
			init();
			graph = new OrientGraph("memory:testdb");
			framedGraph = factory.create(graph); // Frame the graph.
			graph.createVertexType("SpotifyArtist");
			// TopRymReader reader = new TopRymReader();
			// List<TopProgAlbum> albums = reader.getAlbums();
			List<TopProgAlbum> albums = getRymAlbums();
			try {
				for (TopProgAlbum topAlbum : albums.subList(0, 6)) {
					System.out.println(topAlbum.getArtist());
					Artist artist = searchArtist(topAlbum.getArtist());
					SpotifyArtist spotifyArtist = upsertArtist(artist);
					Collection<Album> spAlbums = getAlbumsForArtist(artist);
					System.out.println("Found " + spAlbums.size()
							+ " unique albums in Spotify");
					for (Album album : spAlbums) {
						printAlbum(album);
						SpotifyAlbum spotifyAlbum = upsertAlbum(album);
						spotifyArtist.addAlbum(spotifyAlbum);
						List<Track> spTracks = getTracksForAlbum(album);
						for (Track spTrack : spTracks) {
							SpotifyTrack st = upsertTrack(spTrack);
							spotifyAlbum.addTracks(st);
						}
					}
					graph.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// dumpGraph();

			graph.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
