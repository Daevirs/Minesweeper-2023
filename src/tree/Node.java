package tree;

public class Node {
	private static final int SIZE = 28;
	private boolean exitNode;
	private Node[] children;
	
	public Node(boolean first) {
		this.exitNode = false;
		this.children = new Node[SIZE];
	}

	public Node() {
		this.exitNode = false;
		this.children = null;
	}

	public Node[] getChildren() {
		return this.children;
	}
	
	public void setChildren(int position) {
		children[position] = new Node();
	}
	
	/**
	 * Create children and set the following letter as a node
	 * @param position The following letter
	 */
	public void createChildren(int position) {
		this.children = new Node[SIZE];
		this.children[position] = new Node();
	}
	
	/**
	 * Check if this node has children nodes
	 * @return true if it has children, false otherwise
	 */
	public boolean hasChildren() {
		return children != null;
	}
	
	/**
	 * Check if the next node exists
	 * @param letter The position of the next letter
	 * @return true if found, false otherwise
	 */
	public boolean isPossible(int position) {
		// si la node ne contient pas d'enfants, le mot n'existe pas
		if(this.children == null) { 
			return false;
		}
		// si l'enfant n'existe pas, alors le mot n'existe pas
		if(this.children[position] == null) {
			return false;
		}
		return true;
	}
	
	public boolean getExitNode() {
		return this.exitNode;
	}
	
	public void setExitNode(boolean value) {
		this.exitNode = value;
	}
}
