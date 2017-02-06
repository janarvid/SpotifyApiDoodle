package org.veggeberg.spotify;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;

public interface Person {
	@Property("firstName")
	public String getFirstName();
	@Property("firstName")
	public void setFirstName(String name);
	@Property("lastName")
	public String getLastName();
	@Property("lastName")
	public void setLastName(String name);

	@Adjacency(label = "knows")
	public Iterable<Person> getKnowsPeople();

	@Adjacency(label = "knows")
	public void addKnowsPerson(final Person person);

	@GremlinGroovy("it.out('knows').out('knows').dedup")
	// Make sure you use the GremlinGroovy module! #1
	public Iterable<Person> getFriendsOfAFriend();
}
