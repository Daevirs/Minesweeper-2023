package boggle;

public class Vertex implements Comparable<Integer>{
	private char letter;
	private int position;
	private int[][] neighbours;
	
	public Vertex(char letter, int position, int size) {
		this.letter = letter;
		this.position = position;
		this.neighbours = setNeighbourhood(position, size);
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	public int[][] getNeighbours() {
		return this.neighbours;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	private int[][] setNeighbourhood(int position, int size) {
		int[][] neighbours = new int[3][3];
		for(int i = 0; i < 9; i++) {
			neighbours[i/3][i%3] = -1;
			if(i != 4) {
				if((position/size) -1 >= 0 && i < 3) { // si le sommet ne se situe pas sur la première ligne
					if((position%size) +(i-1) >= 0 && (position%size) +(i-1) < size){
						neighbours[i/3][i%3] = position-size + (i-1);
					}
				}
				if(i >= 3 && i <= 5) { // cas latéraux
					if((position%size) +(i-4) >= 0 && (position%size) +(i-4) < size){
						neighbours[i/3][i%3] = position + (i-4);
					}
				}
				if((position/size) +1 < size && i > 5) { // si le sommet ne se situe pas sur la dernière ligne
					if((position%size) +(i-7) >= 0 && (position%size) +(i-7) < size){
						neighbours[i/3][i%3] = position + size + (i-7);
					}
				}
			}
		}
		return neighbours;
	}

	@Override
	public int compareTo(Integer o) {
		if(this.position != o) return 1;
		return 0;
	}

}
