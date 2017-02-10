package org.veggeberg.spotify;

import java.util.List;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.wrapper.spotify.models.Copyright;
import com.wrapper.spotify.models.Image;

interface SpotifyAlbum {
	@Property("albumType")
	String getAlbumType();
	@Property("albumType")
	void setAlbumType(String albumType);

//	List<SimpleArtist> getArtists();
//	void setArtists(List<SimpleArtist> artists);

	@Property("availableMarkets")
	List<String> getAvailableMarkets();
	@Property("availableMarkets")
	void setAvailableMarkets(List<String> availableMarkets);

	@Property("copyrights")
	List<Copyright> getCopyrights();
	@Property("copyrights")
	void setCopyrights(List<Copyright> copyrights);

//	ExternalIds getExternalIds();
//	void setExternalIds(ExternalIds externalIds);

//	ExternalUrls getExternalUrls();
//	void setExternalUrls(ExternalUrls externalUrls);

	@Property("genres")
	List<String> getGenres();
	@Property("genres")
	void setGenres(List<String> genres);

	@Property("href")
	String getHref();
	@Property("href")
	void setHref(String href);

	@Property("albumId")
	String getAlbumId();
	@Property("albumId")
	void setAlbumId(String albumId);

	List<Image> getImages();
	void setImages(List<Image> images);

	@Property("name")
	String getName();
	@Property("name")
	void setName(String name);

	@Property("popularity")
	int getPopularity();
	@Property("popularity")
	void setPopularity(int popularity);
	
//	@Adjacency(label="artists")
//	public Iterable<SpotifyArtist> getArtists();
//	@Adjacency(label="artists")
//	public void addArtist(final SpotifyArtist artist);

	@Adjacency(label="tracks")
	public Iterable<SpotifyTrack> getTracks();
	@Adjacency(label="tracks")
	public void addTracks(final SpotifyTrack track);

//	SpotifyEntityType getType();
//	void setType(SpotifyEntityType type);

	@Property("uri")
	String getUri();
	@Property("uri")
	void setUri(String uri);

	@Property("releaseDatePrecision")
	String getReleaseDatePrecision();
	@Property("releaseDatePrecision")
	void setReleaseDatePrecision(String releaseDatePrecision);

	@Property("releaseDate")
	String getReleaseDate();
	@Property("releaseDate")
	void setReleaseDate(String releaseDate);
}
