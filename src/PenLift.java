import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Node implements Comparable<Node> {

	private Vertex v;
	private int nodeId;
	private List<Integer> adjacent;
	private boolean visited;
	
	public Node(Vertex v, int nodeId) {
		this.v = v;
		this.nodeId = nodeId;
		this.adjacent = new ArrayList<Integer>();
	}

	public int compareTo(Node n) {
		return this.getV().compareTo(n.getV());
	}
	
	public boolean equals(Node n) {
		return this.getV().equals(n.getV());
	}

	public Vertex getV() {
		return v;
	}

	public void setV(Vertex v) {
		this.v = v;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public List<Integer> getAdjacent() {
		return adjacent;
	}

	public void setAdjacent(List<Integer> adjacent) {
		this.adjacent = adjacent;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	
}


class Edge {
	private Vertex startPoint;
	private Vertex endPoint;
	private boolean horizontal;

	public Edge(int x1, int y1, int x2, int y2) {
		this.startPoint = new Vertex(Math.min(x1, x2), Math.min(y1, y2));
		this.endPoint = new Vertex(Math.max(x1, x2), Math.max(y1, y2));
		if (startPoint.getY() == endPoint.getY())
			horizontal = true;
		else
			horizontal = false;
	}
	
	public Vertex getStartPoint() {
		return startPoint;
	}
	
	public Vertex getEndPoint() {
		return endPoint;
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}
	
	public Edge(Vertex startPoint, Vertex endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	public boolean overlapsWith(Edge edge) {
		if ((this.isHorizontal() && edge.isHorizontal()) && 					// both edges are horizontal
				(this.getStartPoint().getY() == edge.getStartPoint().getY())) {	// both edges start at same point on x-axis
			if ((Math.max(this.getEndPoint().getX(), edge.getEndPoint().getX()) 
					- Math.min(this.getStartPoint().getX(), edge.getStartPoint().getX())) <= 
					((this.getEndPoint().getX() - this.getStartPoint().getX())
					+ (edge.getEndPoint().getX() - edge.getStartPoint().getX())))
				return true;
		}
		else if (!this.isHorizontal() && !edge.isHorizontal() && 				// both edges are vertical
				(this.getStartPoint().getX() == edge.getStartPoint().getX())) {	// both edges start at same point on y-axis
			if ((Math.max(this.getEndPoint().getY(), edge.getEndPoint().getY()) 
					- Math.min(this.getStartPoint().getY(), edge.getStartPoint().getY())) <= 
					((this.getEndPoint().getY() - this.getStartPoint().getY())
					+ (edge.getEndPoint().getY() - edge.getStartPoint().getY())))
				return true;
		}
		return false;															// one is horizontal, one is vertical
	}
	
	public Edge combineWith(Edge edge) {
		return new Edge(Math.min(this.getStartPoint().getX(), edge.getStartPoint().getX()),
				Math.min(this.getStartPoint().getY(), edge.getStartPoint().getY()),
				Math.max(this.getEndPoint().getX(), edge.getEndPoint().getX()),
				Math.max(this.getEndPoint().getY(), edge.getEndPoint().getY()));
	}
	
	public Vertex intersectsAt(Edge edge) {
		if (this.isHorizontal() && edge.isHorizontal()) return null;
		
		int x=0, y=0;
		
		if(this.isHorizontal()) {
			x=edge.getStartPoint().getX();
			y=this.getStartPoint().getY();
		} else {
			x=this.getStartPoint().getX();
			y=edge.getStartPoint().getY();
		}
		
		Vertex v = new Vertex(x, y);
		
		if (this.contains(v) && edge.contains(v)) {
			return v;
		}
		
		return null;
	}
	
	public boolean contains(Vertex v) {
		if (v.getX() >= this.getStartPoint().getX()
				&& v.getX() <= this.getEndPoint().getX()
				&& v.getY() >= this.getStartPoint().getY()
				&& v.getY() <= this.getEndPoint().getY())
			return true;
		
		return false;
	}

	public String toString() {
		return this.getStartPoint().getX() + "," + this.getStartPoint().getY() + ","
				+ this.getEndPoint().getX() + "," + this.getEndPoint().getY();
	}
}

class Vertex implements Comparable<Vertex> {
	private int x;
	private int y;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Vertex v) {
		if ((this.getX() == v.getX()) && (this.getY() == v.getY()))
			return true;
		return false;
	}

	public int compareTo(Vertex v) {
		if (this.getX() != v.getX())
			return this.getX() - v.getX();
		else
			return this.getY() - v.getY();
	}
}


public class PenLift {
	
	private List<Node> nodes;
	
	public int numTimes(String[] segments, int n) {
		List<Edge> edges = new ArrayList<Edge>();
		
		for (String s : segments) {
			String[] lineCoOrds = s.split(" ");
			Edge edge = new Edge(Integer.parseInt(lineCoOrds[0]), Integer.parseInt(lineCoOrds[1]), 
								 Integer.parseInt(lineCoOrds[2]), Integer.parseInt(lineCoOrds[3]));
			edges.add(edge);
		}
		
		for (int i=0; i<edges.size(); i++) {
			for (int j=i+1; j<edges.size(); j++) {
				if (edges.get(i).overlapsWith(edges.get(j))) {
					edges.set(i, edges.get(i).combineWith(edges.get(j)));
					edges.remove(j);
				}			
			}
		}
		
		List<Vertex> vertices = new ArrayList<Vertex>();
		for (int i=0; i<edges.size(); i++) {
			Edge e1 = edges.get(i);
			vertices.add(e1.getStartPoint());
			vertices.add(e1.getEndPoint());
			
			for (int j=0; j<i; j++) {
				Edge e2 = edges.get(j);
				Vertex intersection = e1.intersectsAt(e2);
				if (intersection != null)
					vertices.add(intersection);
			}
		}
		
		Collections.sort(vertices);
		nodes = new ArrayList<Node>();
		nodes.add(new Node(vertices.get(0), nodes.size()));
		
		for (int i=1; i<vertices.size(); i++) {
			if (!vertices.get(i).equals(vertices.get(i-1)))
				nodes.add(new Node(vertices.get(i), nodes.size()));
		}
		
		// Create graph
		for (int i=0; i<edges.size(); i++) {
			Edge e = edges.get(i);
			List<Node> graph = new ArrayList<Node>();
			
			for (int j=0; j<nodes.size(); j++) {
				if (e.contains(nodes.get(j).getV()))
					graph.add(nodes.get(j));
			}
			
			Collections.sort(graph);
			
			for (int j=0; j+1 < graph.size(); j++) {
				// Set both nodes adjacent to each other
				graph.get(j).getAdjacent().add(graph.get(j+1).getNodeId());
				graph.get(j+1).getAdjacent().add(graph.get(j).getNodeId());
			}
			
		}
		
		int answer = 0;
		for (int i=0; i<nodes.size(); i++) {
			if (!nodes.get(i).isVisited()) {
				nodes.get(i).setVisited(true);
				int odd = DepthFirstSearch(nodes.get(i));
				
				if (n % 2 == 0 || odd == 0) {
					answer++;
				} else {
					answer += odd / 2;
				}
			}
		}
		
		return answer - 1;
	}
	
	private int DepthFirstSearch(Node node) {
		int odd = node.getAdjacent().size() % 2;
		for (int x : node.getAdjacent()) {
			if (!nodes.get(x).isVisited()) {
				nodes.get(x).setVisited(true);
				odd += DepthFirstSearch(nodes.get(x));
			}
		}
		return odd;
	}

	public static void main(String[] args) {
		System.out.println(new PenLift().numTimes(new String[] {"-252927 -1000000 -252927 549481","628981 580961 -971965 580961",
				"159038 -171934 159038 -420875","159038 923907 159038 418077",
				"1000000 1000000 -909294 1000000","1000000 -420875 1000000 66849",
				"1000000 -171934 628981 -171934","411096 66849 411096 -420875",
				"-1000000 -420875 -396104 -420875","1000000 1000000 159038 1000000",
				"411096 66849 411096 521448","-971965 580961 -909294 580961",
				"159038 66849 159038 -1000000","-971965 1000000 725240 1000000",
				"-396104 -420875 -396104 -171934","-909294 521448 628981 521448",
				"-909294 1000000 -909294 -1000000","628981 1000000 -909294 1000000",
				"628981 418077 -396104 418077","-971965 -420875 159038 -420875",
				"1000000 -1000000 -396104 -1000000","-971965 66849 159038 66849",
				"-909294 418077 1000000 418077","-909294 418077 411096 418077",
				"725240 521448 725240 418077","-252927 -1000000 -1000000 -1000000",
				"411096 549481 -1000000 549481","628981 -171934 628981 923907",
				"-1000000 66849 -1000000 521448","-396104 66849 -396104 1000000",
				"628981 -1000000 628981 521448","-971965 521448 -396104 521448",
				"-1000000 418077 1000000 418077","-1000000 521448 -252927 521448",
				"725240 -420875 725240 -1000000","-1000000 549481 -1000000 -420875",
				"159038 521448 -396104 521448","-1000000 521448 -252927 521448",
				"628981 580961 628981 549481","628981 -1000000 628981 521448",
				"1000000 66849 1000000 -171934","-396104 66849 159038 66849",
				"1000000 66849 -396104 66849","628981 1000000 628981 521448",
				"-252927 923907 -252927 580961","1000000 549481 -971965 549481",
				"-909294 66849 628981 66849","-252927 418077 628981 418077",
				"159038 -171934 -909294 -171934","-252927 549481 159038 549481"}, 1));
	}

}