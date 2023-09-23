package boggle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VertexTest {
	public static final int SIZE = 3;
	public static final String LETTERS = "azertyuio";

	@Test
	void middleVertex() {
		Vertex vertex = new Vertex('t', 4, SIZE);
		int[][] position = new int[][] {{0,1,2},{3,-1,5},{6,7,8}};
		int[][] neighbours = vertex.getNeighbours();
		for(int i = 0; i < 3; i++) {
			assertArrayEquals(position[i], neighbours[i]);
		}
	}
	
	@Test
	void cornerUpperLeftVertex() {
		Vertex vertex = new Vertex('a', 0, SIZE);
		int[][] position = new int[][] {{-1,-1,-1},{-1,-1,1},{-1,3,4}};
		int[][] neighbours = vertex.getNeighbours();
		for(int i = 0; i < 3; i++) {
			assertArrayEquals(position[i], neighbours[i]);
		}
	}
	
	@Test
	void cornerLowerRightVertex() {
		Vertex vertex = new Vertex('o', 8, SIZE);
		int[][] position = new int[][] {{4,5,-1},{7,-1,-1},{-1,-1,-1}};
		int[][] neighbours = vertex.getNeighbours();
		for(int i = 0; i < 3; i++) {
			assertArrayEquals(position[i], neighbours[i]);
		}
	}

}
