package utils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import cryptanalysis.DictionaryBasedAnalysis;

/**
 * Sert de classe de cryptage de fichier pour les tests de cryptanalyse
 * @author dmart
 *
 */
public class Cryptage {
	// modifier le chemin pour vos fichiers
	private static final String PATH = "txt/Otis le scribe.txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK";
	public static void main(String[] args) {

		String cryptogram = readFile(PATH, StandardCharsets.UTF_8).toLowerCase();
		cryptogram = cryptogram.replaceAll("[éêè]+", "e");
		cryptogram = cryptogram.replace('à', 'a');
		cryptogram = cryptogram.replace("ç", "c");
		cryptogram = cryptogram.replace('î', 'i');
		cryptogram = cryptogram.replace('ù', 'u');
		cryptogram = cryptogram.replace('ô', 'o');
		cryptogram = cryptogram.replaceAll("\\d+", " ");
		cryptogram = cryptogram.replaceAll("[?!;:,.\\/<>«»_-]+", " ");
		cryptogram = cryptogram.replaceAll("\\b[^a-zA-Z]+\\b", " ");
		System.out.println(
				DictionaryBasedAnalysis.applySubstitution(cryptogram.toUpperCase(), ENCODING_ALPHABET));
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
	
}
