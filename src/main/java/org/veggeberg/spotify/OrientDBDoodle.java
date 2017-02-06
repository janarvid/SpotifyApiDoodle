package org.veggeberg.spotify;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;

public class OrientDBDoodle {
	private static OrientGraph graph;

	public OrientDBDoodle() {
		// TODO Auto-generated constructor stub
	}
	
	private static Vertex createPersons() {
		Vertex vPerson = graph.addVertex("class:Person");
		vPerson.setProperty("firstName", "John");
		vPerson.setProperty("lastName", "Smith");
		return vPerson;
	}
	
	private static Vertex createAddresses() {
		Vertex vAddress = graph.addVertex("class:Address");
		vAddress.setProperty("street", "Van Ness Ave.");
		vAddress.setProperty("city", "San Francisco");
		vAddress.setProperty("state", "California");
		return vAddress;
	}
	
	private static void createEdges() {
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		OrientGraphFactory factory = new OrientGraphFactory("plocal:/temp/mydb");
//		graph = new OrientGraph("plocal:testdb");
		graph = new OrientGraph("memory:testdb");
		FramedGraphFactory factory = new FramedGraphFactory(new GremlinGroovyModule()); //(1) Factories should be reused for performance and memory conservation.

		FramedGraph<OrientGraph> framedGraph = factory.create(graph); //Frame the graph.
		
		//graph.createVertexType("Person");
		graph.createVertexType("Address");
		Vertex vPerson = createPersons();
		Vertex vAddress = createAddresses();
		Person p = framedGraph.frame(graph.addVertex("class:Person"), Person.class);
		p.setFirstName("Jan Arvid");
		p.setLastName("Veggeberg");
		OrientEdge eLives = graph.addEdge("class:lives", vPerson, vAddress, null);
		for (Vertex vertex : graph.getVertices()) {
			System.out.println(vertex);
			Person person = framedGraph.getVertex(vertex.getId(), Person.class);
			for (Vertex v : vertex.getVertices(Direction.BOTH, "lives")) {
				System.out.println("  " + v + " id = " + v.getId());
			}
			for (String key : vertex.getPropertyKeys()) {
				System.out.println("    " + key + " = " + vertex.getProperty(key));
			}
		}
		graph.shutdown();
	}

}
