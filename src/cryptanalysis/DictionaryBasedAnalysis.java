package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import tree.LexicographicTree;
import utils.LetterProbability;

public class DictionaryBasedAnalysis {
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static final int ALERT_NUMBER = 3;
	private int count = 0;

	private LetterProbability letterProbability;
	private String cryptogram;
	private LexicographicTree dict;
	private int numberOfWords;
	private int falseAlert; // sert de nombre de contrôle, après un nombre donné de fausses alerte, le programme se termine
	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.cryptogram = cryptogram;
		this.dict = dict;
		this.numberOfWords = 0;
		this.letterProbability = new LetterProbability(dict);
		this.falseAlert = ALERT_NUMBER; 
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an approximated decoding alphabet.
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		// Retire tous les espaces ainsi que les mots de moins de 3 lettres
		String[] words = cryptogram.split("(\\b\\w{1,2}\\b)|(\\W+)");
		TreeMap<Integer, List<String>> orderedWords = new TreeMap<>();
		orderWords(words, orderedWords);
		// Sans alphabet personnel : ~1.8s   Avec alphabet personnel : ~0.65s
		String decodedAlphabet = setFirstAlphabet(orderedWords);
		decodedWords(alphabet, decodedAlphabet, orderedWords);
		
		for (int i = orderedWords.lastKey(); i > 3; i--) {
			System.out.println(">>> Words of length " + i);
			if(orderedWords.get(i) != null) {
				String foundAlphabet = findAlphabet(decodedAlphabet, i, orderedWords);
				if(foundAlphabet.equals(decodedAlphabet)) {
					falseAlert--;
				} else {
					decodedAlphabet = foundAlphabet;
					falseAlert = ALERT_NUMBER;
				}
				if(falseAlert == 0) {
					System.out.printf("\nThe number of false alert in a row reached %d, the alphabet is most likely found. If not, raise the amount of false alert needed\n", ALERT_NUMBER);
					System.out.println("count :" + count);
					return decodedAlphabet;
				}
			}
		}
		System.out.println("count :" + count);
		return decodedAlphabet;
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * @param text A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		String substitutedText = "";
		int position;
		for (char c : text.toCharArray()) {
			position = (int)(c-65);
			if(position < 0 || position > 26) {
				substitutedText += c;
			} else {
				substitutedText += alphabet.charAt(position);
			}
		}
		return substitutedText;
	}
	
	/*
	 * PRIVATE METHODS
	 */
	
	/**
	 * order the words found in lists based on their length
	 * @param words The list of words found in the text
	 * @param orderedWords A map with the length as key and a list of words as value
	 */
	private void orderWords(String[] words, TreeMap<Integer, List<String>> orderedWords) {
		List<String> wordLength;
		for (String word : words) {
			if(word.length() >= 3) {
				if(!orderedWords.containsKey(word.length())) {
					wordLength = new ArrayList<>();
					wordLength.add(word);
					orderedWords.put(word.length(), wordLength);
					numberOfWords++;
				} else {
					wordLength = orderedWords.get(word.length());
					if(!wordLength.contains(word)) {
						wordLength.add(word);
						numberOfWords++;
					}
				}
			}
		}
	}


	/**
	 * Shows how many words can be decoded with the new alphabet compared to the old alphabet
	 * @param alphabet The alphabet used to decrypt
	 * @param newAlphabet The new found alphabet
	 * @param orderedWords The list of words found in the given text
	 * @return The new alphabet if the number of words found is greater than with the old alphabet,
	 * 		   otherwise the opposite
	 */
    private String decodedWords(String alphabet, String newAlphabet, TreeMap<Integer, List<String>> orderedWords) {
    	count++;
    	int found = numberOfDecodedWords(alphabet, orderedWords);
    	int newFound = numberOfDecodedWords(newAlphabet, orderedWords);
    	if(newFound > found) {
    		System.out.printf("Score decoded : words = %d / valid = %d / invalid = %d \n", numberOfWords, newFound, numberOfWords-newFound);
    		return newAlphabet;
    	} else {
    		System.out.printf("Changes discarded, number of words found with the new Alphabet is lesser than with the old alphabet : old = %d -- new = %d\n", found, newFound);
    		return alphabet;
    	}
    	
	}
    
    /**
     * Gives the number of word decoded with a given alphabet
     * @param alphabet The given alphabet
     * @param orderedWords The words found in the given text
     * @return the number of words found in the dictionnary
     */
    private int numberOfDecodedWords(String alphabet, TreeMap<Integer, List<String>> orderedWords) {
    	int found = 0;
    	for (int i = orderedWords.lastKey(); i > 3; i--) {
    		if(orderedWords.get(i) != null) {
    			for (String word : orderedWords.get(i)) {
    				String copy = decrypt(alphabet, word);
    				
    				if(dict.containsWord(copy.toLowerCase())) {
    					found++;
    				}
    			}
    		}
		}
    	return found;
    }

    /**
     * Generate a first alphabet using the number of letters found in the given text
     * @param orderedWords The words found in the given text
     * @return A new alphabet
     */
	private String setFirstAlphabet(TreeMap<Integer, List<String>> orderedWords) {
    	int[] letters = new int[26];
		for (String word : cryptogram.split("\\W+")) {
			for(char letter : word.toCharArray()) {
				letters[(int) letter -65]++;
			}
		}
		return letterProbability.alphabetProbability(letters);
		
	}

	/**
	 * Decrypt a given word with a given alphabet
	 * @param alphabet The alphabet used to decrypt
	 * @param word The encrypted word
	 * @return The decrypted word
	 */
	private String decrypt(String alphabet, String word) {
		String decrypted = "";
		for (char c : word.toCharArray()) {
			decrypted += alphabet.charAt((int) c-65);
		}
		return decrypted;
	}

	/**
	 * Tries to find the correct decrypting key based on a given text
	 * @param alphabet The current alphabet
	 * @param wordSize The length of searched words 
	 * @param orderedWords the words found in the given text
	 * @return the old alphabet if no new one is found, the new one otherwise
	 */
	private String findAlphabet(String alphabet, int wordSize, TreeMap<Integer, List<String>> orderedWords) {		
		List<Candidate> found;
		String currentAlphabet = alphabet;
		String decodedAlphabet;
		for (String word: orderedWords.get(wordSize)) {
			String copy = decrypt(alphabet, word);
			if(!dict.containsWord(copy.toLowerCase())) {
				
				found = findCompatibleWord(currentAlphabet, word);
				if(found != null) {
					System.out.println("Cryptogram word      -> " + word);
					System.out.println("Invalid decoded word -> " + copy);
					for (Candidate candidate : found) {
						// affichage dans la console
						System.out.println("Next candidate word  -> " + candidate.getWord());
						System.out.println();
						
						// crée un nouvel alphabet et le compare avec l'ancien.
						decodedAlphabet = changeAlphabet(currentAlphabet, candidate.getLetters(), candidate.getWord());
						if(!decodedAlphabet.equals(currentAlphabet)) {
							decodedAlphabet = decodedWords(currentAlphabet, decodedAlphabet, orderedWords);
							currentAlphabet = decodedAlphabet;
						}
					}
				}
			}
		}
		
		return currentAlphabet;
	}
	
	/**
	 * Tries to find all the compatible word in the dictionnary
	 * @param alphabet The alphabet used to decrypt
	 * @param word The word needed to be found
	 * @return A list of candidate for the searched word, or null if nothing is found
	 */
	private List<Candidate> findCompatibleWord(String alphabet, String word) {
		Set<Character> wordLetter = new TreeSet<>();
		for (char letter : word.toCharArray()) {
			wordLetter.add(letter);
		}
		
		if(wordLetter.size() < word.length()) {
			List<String> list = dict.getWordsOfLength(word.length());
			return findValidWords(alphabet, word, list);
		}
		return null;
	}

	/**
	 * Find all the words that could correspond to the searched word
	 * @param alphabet The used alphabet
	 * @param word The seached words
	 * @param foundWords The list of all words with the same length as the searched word
	 * @return A list of candidate for the searched word, or null if nothing is found
	 */
	private List<Candidate> findValidWords(String alphabet, String word, List<String> foundWords) {
		List<Candidate> candidates = new ArrayList<>();
		String decodedWord = decrypt(alphabet, word);
		
		for (String s : foundWords) {
			Candidate foundWord = new Candidate(s);
			if(checkIfValid(foundWord, word)) {
				candidates.add(foundWord);
			}
		}
		
		if(candidates.isEmpty()) {
			return null;
		}
		// Si l'on arrive ici, c'est que des mots similaire à notre mot ont été trouvé.
		// On choisit le candidat le plus proche de la traduction
		return selectCandidate(candidates, decodedWord);
	}
	
	/**
	 * Select the most compatible words based on the decoded word
	 * @param candidates The compatible words
	 * @param decodedWord The word decoded with the alphabet
	 * @return A list of candidate for the searched word, or null if nothing is found
	 */
	private List<Candidate> selectCandidate(List<Candidate> candidates, String decodedWord) {
		List<Candidate> selectedCandidate = null;
		int closestWord = -1; // nombre de similarités sur le candidat choisi
		int similarities = 0; // nombre de similarités sur le candidat actuel
		
		for (Candidate candidate : candidates) {
			char[] word = candidate.getWord().toCharArray();
			for (int j = 0; j < decodedWord.length(); j++ ) {
				if(word[j] == decodedWord.charAt(j)) {
					similarities++;
				}
			}
			if(closestWord < similarities && similarities < decodedWord.length()) {
				selectedCandidate = new ArrayList<>();
				selectedCandidate.add(candidate);
				closestWord = similarities;
			} else if(closestWord == similarities) {
				selectedCandidate.add(candidate);
			}
			similarities = 0;
		}
		return selectedCandidate;
	}

	/**
	 * Check if a given word has the same letter arrangement as the searched word 
	 * @param candidate The word needed to be verified
	 * @param word The searched word
	 * @return true if they have the same letter arrangement, false otherwise
	 */
	private boolean checkIfValid(Candidate candidate, String word) {
		char[] letters = new char[26];
		char currentLetter;
		String foundWord = candidate.getWord();
		for(int i = 0; i < foundWord.length(); i++) {
			// si le mot à tester contient un tiret ou une apostrophe, le mot est invalide
			if(foundWord.charAt(i) == '\'' || foundWord.charAt(i) == '-') {
				return false;
			}
			
			currentLetter = letters[(int) foundWord.charAt(i) -65];
			 // caractère de contrôle d'un champ vide dans un tableau de type primitif
			if(currentLetter == '\u0000') {
				letters[(int) foundWord.charAt(i) -65] = word.charAt(i);
				currentLetter = word.charAt(i);
			// vérifie si la lettre trouvée dans le tableau est similaire à la lettre du mot recherché à la position i
			} else if(currentLetter != word.charAt(i)) {
				return false;
			}
			// vérifie si la lettre trouvée n'est pas déjà présente dans le tableau. Si oui, le mot est invalide
			for (int j = 0; j < 26; j++) {
				if(letters[j] != '\u0000' && currentLetter == letters[j] && j != (int)(foundWord.charAt(i) -65)) {
					return false;
				}
			}
			
		}
		candidate.setLetters(letters);
		return true;
	}

	/**
	 * Modify the current alphabet
	 * @param alphabet The current alphabet
	 * @param letters The letters needed to be remplaced
	 * @param word The searched word
	 * @return a new Alphabet
	 */
	private String changeAlphabet(String alphabet, char[] letters, String word) {
		char[] newAlphabet = alphabet.toCharArray();
		newAlphabet = setNewAlphabet(letters, word, newAlphabet);
		String alpha = "";
		
		for (char c : newAlphabet) {
			alpha += c;
		}
		
		String result = "";
		for (int i = 0; i < 26; i++) {
			result += alpha.charAt(i) == alphabet.charAt(i)? " " : "x";
		}
		// Affichage en console
		System.out.println("Standard Alphabet         : " + LETTERS);
		System.out.println("Approximated Alphabet     : " + alphabet);
		System.out.println("New approximated Alphabet : " + alpha);
		System.out.printf("            Modifications : %s", result);
		System.out.printf("\n\n");
		
		return alpha;
	}

	/**
	 * Setup a new alphabet based on an array of letters found in the given text
	 * @param letters The array of letters used to create the new alphabet
	 * @param word The searched word
	 * @param newAlphabet The new alphabet based on the old alphabet
	 * @return The new alphabet 
	 */
	private char[] setNewAlphabet(char[] letters, String word, char[] newAlphabet) {
		char memory = ' ';
		int letterPosition = ' ';
		for(int i = 0; i < 26; i++) {
			// si la lettre en i n'est pas le caractère de controle
			if(letters[i] != '\u0000') {
				char letter = (char)(i+65); // la lettre à remplacer
				letterPosition = (int)(letters[i]-65); // la lettre qu'elle représente dans le texte encodé
				memory = newAlphabet[letterPosition]; // la lettre représentée actuellement dans le dictionnaire
				
				// on recherche la lettre dans le tableau et on echange les deux lettres
				for(int j = 0; j < 26; j++) {
					if(newAlphabet[j] == letter) {
						newAlphabet[j] = memory;
						j = 25; // lettre trouvée, on termine la boucle
					}
				}
				newAlphabet[letterPosition] = letter;
			}
		}
		return newAlphabet;
	}
	
	/**
	 * Compares two substitution alphabets.
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	/**
	 * Load the text file pointed to by pathname into a String.
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();
		
		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 *  Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
//		String startAlphabet = LETTERS;
		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		long startTime = System.currentTimeMillis();
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		
		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();
		System.out.println("Analysis duration : " + (System.currentTimeMillis() - startTime) / 1000.0);
		
		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}
