package org.veggeberg.spotify;

import java.util.List;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;

interface SpotifyArtist {
//	 Followers getFollowers();
	// void setFollowers(Followers followers);

	// ExternalUrls getExternalUrls();
	// void setExternalUrls(ExternalUrls externalUrls);

	@Property("genres")
	List<String> getGenres();
	@Property("genres")
	void setGenres(List<String> genres);

	@Property("href")
	String getHref();
	@Property("href")
	void setHref(String href);

	@Property("artistId")
	String getArtistId();
	@Property("artistId")
	void setArtistId(String id);
	
	@Adjacency(label="albums")
	public Iterable<SpotifyAlbum> getAlbums();
	@Adjacency(label="albums")
	public void addAlbum(final SpotifyAlbum album);

//	List<Image> getImages();
//	void setImages(List<Image> images);

	@Property("name")
	String getName();
	@Property("name")
	void setName(String name);

	@Property("popularity")
	int getPopularity();
	@Property("popularity")
	void setPopularity(int popularity);

//	SpotifyEntityType getType();
//	void setType(SpotifyEntityType type);

	@Property("uri")
	String getUri();
	@Property("uri")
	void setUri(String uri);
}
