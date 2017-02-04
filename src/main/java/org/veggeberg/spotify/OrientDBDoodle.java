package org.veggeberg.spotify;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

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
		//graph.createVertexType("Person");
		graph.createVertexType("Address");
		Vertex vPerson = createPersons();
		Vertex vAddress = createAddresses();
		OrientEdge eLives = graph.addEdge("class:lives", vPerson, vAddress, null);
		for (Vertex vertex : graph.getVertices()) {
			System.out.println(vertex);
			for (Vertex v : vertex.getVertices(Direction.BOTH, "lives")) {
				System.out.println("  " + v);
			}
			for (String key : vertex.getPropertyKeys()) {
				System.out.println("    " + key + " = " + vertex.getProperty(key));
			}
		}
		graph.shutdown();
	}

}
