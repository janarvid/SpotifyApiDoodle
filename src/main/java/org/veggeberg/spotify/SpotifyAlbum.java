package org.veggeberg.spotify;

import java.util.List;

import com.tinkerpop.frames.Property;
import com.wrapper.spotify.models.AlbumType;
import com.wrapper.spotify.models.Copyright;
import com.wrapper.spotify.models.ExternalIds;
import com.wrapper.spotify.models.ExternalUrls;
import com.wrapper.spotify.models.Image;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimpleTrack;
import com.wrapper.spotify.models.SpotifyEntityType;

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

	String getId() {
	    return id;
	  }

	void setId(String id) {
	    this.id = id;
	  }

	List<Image> getImages() {
	    return images;
	  }

	void setImages(List<Image> images) {
	    this.images = images;
	  }

	String getName() {
	    return name;
	  }

	void setName(String name) {
	    this.name = name;
	  }

	int getPopularity() {
	    return popularity;
	  }

	void setPopularity(int popularity) {
	    this.popularity = popularity;
	  }

	Page<SimpleTrack> getTracks() {
	    return tracks;
	  }

	void setTracks(Page<SimpleTrack> tracks) {
	    this.tracks = tracks;
	  }

	SpotifyEntityType getType() {
	    return type;
	  }

	void setType(SpotifyEntityType type) {
	    this.type = type;
	  }

	String getUri() {
	    return uri;
	  }

	void setUri(String uri) {
	    this.uri = uri;
	  }

	String getReleaseDatePrecision() {
	    return releaseDatePrecision;
	  }

	void setReleaseDatePrecision(String releaseDatePrecision) {
	    this.releaseDatePrecision = releaseDatePrecision;
	  }

	String getReleaseDate() {
	    return releaseDate;
	  }

	void setReleaseDate(String releaseDate) {
	    this.releaseDate = releaseDate;
	  }
}
