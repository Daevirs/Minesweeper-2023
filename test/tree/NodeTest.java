package tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NodeTest {

	@Test
	void checkNodeSize() {
		Node node = new Node();
		assertNull(node.getChildren());
		node.createChildren(0);
		assertEquals(node.getChildren().length, 28);
	}
	
	@Test
	void checkRightLetterInserted() {
		Node node = new Node();
		node.createChildren(getPosition('a'));
		assertNotNull(node.getChildren()[2]);
		assertNull(node.getChildren()[1]);
	}
	
	@Test
	void checkIfChildNodeIsPossible() {
		Node node = new Node();
		node.createChildren(getPosition('a'));
		assertTrue(node.isPossible(2));
		assertFalse(node.isPossible(1));
	}
	
	private int getPosition(char letter) {
		switch (letter) {
		case '\'':
			return 0;
		case '-':
			return 1;
		default:
			return (letter - 95);
		}
	}

}
