package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;


public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;

	@BeforeAll
	private static void initTestDictionary() {
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetPerfectTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Toutes les correspondances n'ont pas été trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetNormalAlphabet() {
		String cryptogram = readFile("txt/Plus fort que Sherlock Holmes (Standard alphabet).txt", StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		long startTime = System.currentTimeMillis();
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < LETTERS.length(); i++) {
			if (LETTERS.charAt(i) == alphabet.charAt(i)) score++;
		}
		System.out.println("Decoding     alphabet : " + LETTERS);
		System.out.println("Approximated alphabet : " + alphabet);
		System.out.println("Remaining differences : " + compareAlphabets(LETTERS, alphabet));
		System.out.println();
		System.out.println("Analysis duration : " + (System.currentTimeMillis() - startTime) / 1000.0);
		assertTrue(score == 26, "Toutes les correspondances n'ont pas été trouvées [" + score + "]");
	}
	
	@Test
	void guessApprocimatedAlphabetOtis() {
		String cryptogram = readFile("txt/Otis le scribe (cryptogram).txt", StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		long startTime = System.currentTimeMillis();
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + alphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, alphabet));
		System.out.println();
		System.out.println("Analysis duration : " + (System.currentTimeMillis() - startTime) / 1000.0);
		assertTrue(score >= 20, "Moins de 20 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetVendetta() {
		String cryptogram = readFile("txt/V pour Vendetta (cryptogram).txt", StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		long startTime = System.currentTimeMillis();
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		System.out.println("             alphabet : " + LETTERS);
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + alphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, alphabet));
		System.out.println();
		System.out.println("Analysis duration : " + (System.currentTimeMillis() - startTime) / 1000.0);
		assertTrue(score >= 20, "Moins de 20 correspondances trouvées [" + score + "]");
	}
	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
}
