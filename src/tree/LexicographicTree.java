package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LexicographicTree {
	
	/*
	 * CONSTRUCTORS
	 */
	private Node sNode;
	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		this.sNode = new Node(true);
	}
	
	/**
	 * Constructor : creates a lexicographic tree populated with words 
	 * @param filename A text file containing the words to be inserted in the tree 
	 */
	public LexicographicTree(String filename) {
		this.sNode = new Node(true);
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(filename)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				insertWord(line.toLowerCase());
			}
			reader.close();
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error while reading file : " + e);
		}
	}
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Returns the number of words present in the lexicographic tree.
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return finishedWords(this.sNode);
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * @param word A word
	 */
	public void insertWord(String word) {
		Node currentNode = this.sNode;
		int position = -1;
		for (char letter : word.toLowerCase().toCharArray()) {
			position = getPosition(letter); // récupère la position dans le tableau de la lettre
			// si la node ne possède pas d'enfants, le crée et insère directement
			if(!currentNode.hasChildren()) { 
				currentNode.createChildren(position);
			}
			// s'il n'y a pas d'enfant pour la lettre, crée une nouvelle node vide
			else if(currentNode.getChildren()[position] == null) { 
				currentNode.setChildren(position);
			}
			currentNode = currentNode.getChildren()[position]; // récupère la node suivante
		}
		
		currentNode.setExitNode(true); // active la node comme sortie
	}
	
	/**
	 * Determines if a word is present in the lexicographic tree.
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		Node currentNode = this.sNode;
		int position = -1;
		for (char letter : word.toCharArray()) {
			// si la node ne contient pas d'enfants, le mot n'existe pas
			if(!currentNode.hasChildren()) { 
				return false;
			}
			position = getPosition(letter); // récupère la position dans le tableau de la lettre
			// si l'enfant n'existe pas, alors le mot n'existe pas
			if(currentNode.getChildren()[position] == null) {
				return false;
			}
			currentNode = currentNode.getChildren()[position];
		}
		return currentNode.getExitNode();
	}
	
	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix.
	 * If 'prefix' is an empty string, all words are returned.
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		Node currentNode = this.sNode;
		List<String> wordList = new ArrayList<>();
		// si on reçoit un préfixe, on se déplace dans l'arbre jusqu'au node en utilisant les lettres du préfixe
		if(prefix.length() > 0) { 
			int position = -1;
			for (char letter : prefix.toCharArray()) {
				// si la node n'as pas d'enfant, alors aucun mot n'existe avec le préfixe
				if(!currentNode.hasChildren()) { 
					return wordList;
				}
				position = getPosition(letter);
				// si l'enfant n'existe pas, alors aucune mot n'existe avec le préfixe
				if(currentNode.getChildren()[position] != null) { 
					currentNode = currentNode.getChildren()[position];
				} else {
					return wordList;
				}
			}
		} // si la node récupéré est un mot, on l'ajoute à la liste
		if(currentNode.getExitNode()) {
			wordList.add(prefix);
		}
		getPrefixedWord(currentNode, prefix, wordList);
		return wordList;
	}

	/**
	 * Returns an alphabetic list of all words of a given length.
	 * If 'length' is lower than or equal to zero, an empty list is returned.
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		List<String> lengthList = new ArrayList<>();
		if(length > 0) {
			getWordOfLength(this.sNode.getChildren(), 0, new char[length], lengthList);
		}
		return lengthList;
	}
	
	/**
	 * Get the sum of all letters used to create this lexicon tree
	 * @return The sum of all words' letters
	 */
	public int[] getNumberOfLetters() {
		int[] probabilities = new int[26];
		List<String> listo = getWords("");
		for (String mot : listo) {
			for (char letter : mot.toCharArray()) {
				if(letter != '\'' && letter != '-') {
					probabilities[(int) letter -97]++;
				}
			}
		}
		return probabilities;
	}
	
	public Node getFirstNode() {
		return this.sNode;
	}

	/*
	 * PRIVATE METHODS
	 */
	
	/**
	 * Return the number of existing word in the lexicon tree
	 * @param currentNode The current node used
	 * @return The number of all existing words collected from their branch
	 */
	private int finishedWords(Node currentNode) {
		int number = 0;
		for (Node nodus : currentNode.getChildren()) {
			if(nodus != null) {
				if(nodus.getExitNode()) {
					number++;
				}
				if(nodus.hasChildren()) {
					number += finishedWords(nodus);
				}
			}
		}
		return number;
	}
	
	/**
	 * Populate a list with existing word in the lexicon tree based on a given length
	 * @param currentNode The current node used
	 * @param currentLength The current depth in the tree
	 * @param word The current word formed
	 * @param list The list to populate
	 */
	private void getWordOfLength(Node[] currentNode, int currentLength, char[] word, List<String> list) {
		char[] copy = word.clone();
		int i = 0;
		for (Node nodus : currentNode) {
			if(nodus != null) {
				copy[currentLength] = getChar(i);
				if(currentLength < word.length -1) {
					if(nodus.hasChildren()) {
						getWordOfLength(nodus.getChildren(), currentLength +1, copy, list);
					}
				} else {
					if(nodus.getExitNode()) {
						list.add(new String(copy));
					}
				}
			}
			++i;
		}
	}
	
	/**
	 * Populate a list with existing word in the lexicon tree based on a given prefix
	 * @param currentNode The current node used
	 * @param word The current word formed
	 * @param list The list to populate
	 */
	private void getPrefixedWord(Node currentNode, String word, List<String> list) {
		int i = 0;
		for (Node nodus : currentNode.getChildren()) {
			if(nodus != null) { // si le node est vide, on passe au suivant
				String copy = word;
				copy += getChar(i);
				if(nodus.getExitNode()) {
					list.add(copy);
				}
				if(nodus.hasChildren()) { // s'il existe encore des mots, on rappelle la fonction
					getPrefixedWord(nodus, copy, list);
				}
			}
			++i;
		}
	}
	
	/**
	 * Return a number corresponding to the position in an array of a given letter
	 * @param letter Given letter
	 * @return A number between 0 and 27
	 */
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

	/**
	 * Return a character corresponding to the index of an array with a length of 28
	 * @param i Given index
	 * @return A lower character or either a ' or a - sign
	 */
	private char getChar(int i) {
		switch (i) {
		case 0: 
			return '\'';
		case 1:
			return '-';
		default:
			return (char)(i + 95);
		}
	}
	
	/*
	 * TEST FUNCTIONS
	 */
		
	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char)('a' + (int)(number % radix)) + word;
			number = number / radix;
		} while(number != 0);
		return word;
	}
	
	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;
		
		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();
		
		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine();
				    boolean found = dico.containsWord(word);
				    if (!found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine() + "xx";
				    boolean found = dico.containsWord(word);
				    if (found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory()/MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory()/MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory()/MB);
			}
		}
	}
	
	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");
		
		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}

}
