package cryptanalysis;

public class Candidate {
	private String word;
	private char[] letters;
	
	public Candidate(String word) {
		this.word = word.toUpperCase();
		this.letters = new char[26];
	}

	public String getWord() {
		return word;
	}

	public char[] getLetters() {
		return letters;
	}
	
	public void setLetters(char[] letters) {
		this.letters = letters;
	}
	
}
