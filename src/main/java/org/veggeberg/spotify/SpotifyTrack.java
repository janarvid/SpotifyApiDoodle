package org.veggeberg.spotify;

import java.util.List;

import com.tinkerpop.frames.Property;

interface SpotifyTrack {
	@Property("availableMarkets")
	List<String> getAvailableMarkets();
	@Property("availableMarkets")
	void setAvailableMarkets(List<String> availableMarkets);

	@Property("discNumber")
	int getDiscNumber();
	@Property("discNumber")
	void setDiscNumber(int discNumber);

	@Property("duration")
	int getDuration();
	@Property("duration")
	void setDuration(int duration);

	@Property("explicit")
	boolean isExplicit();
	@Property("explicit")
	void setExplicit(boolean explicit);

//	ExternalIds getExternalIds();
//	void setExternalIds(ExternalIds externalIds);

//	ExternalUrls getExternalUrls();
//	void setExternalUrls(ExternalUrls externalUrls);

	@Property("href")
	String getHref();
	@Property("href")
	void setHref(String href);

	@Property("trackId")
	String getTrackId();
	@Property("trackId")
	void setTrackId(String id);

	@Property("name")
	String getName();
	@Property("name")
	void setName(String name);

	@Property("popularity")
	int getPopularity();
	@Property("popularity")
	void setPopularity(int popularity);

	@Property("previewUrl")
	String getPreviewUrl();
	@Property("previewUrl")
	void setPreviewUrl(String previewUrl);

	@Property("trackNumber")
	int getTrackNumber();
	@Property("trackNumber")
	void setTrackNumber(int trackNumber);

	@Property("uri")
	String getUri();
	@Property("uri")
	void setUri(String uri);
}
