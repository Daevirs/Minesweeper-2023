package utils;

import tree.LexicographicTree;

/**
 * Permet de créer des chaines de caractères basé sur la fréquence d'apparition d'une
 * lettre provenant d'un dictionnaire francais.
 * @author dmart
 *
 */
public class LetterProbability {
	private int[] probabilities;
	private int numberOfLetters;

	public LetterProbability(LexicographicTree tree) {
		this.probabilities = tree.getNumberOfLetters();
		this.numberOfLetters = 0;
		for (int i : probabilities) {
			numberOfLetters += i;
		}
	}
	
	/**
	 * génère une chaine de caractères pseudo-aléatoire afin de remplir un tableau
	 * @param size la taille du tableau à remplir
	 * @return la chaine de caractères
	 */
	public String getLetters(int size) {
		String chain = "";
		for(int i = 0; i < size*size; ++i) {
			chain += chooser();
		}
		return chain;
	}
	
	public String alphabetProbability(int[] letters) {
		char[] alphabet = sortAlphabet(insertionSort(letters), insertionSort(probabilities.clone()));
		String result = "";
		for (char c : alphabet) {
			result += c;
		}
		return result;
	}

	/**
	 * Retourne une lettre basée sur sa fréquence d'apparition dans la langue francaise
	 * @return une lettre
	 */
	private char chooser() {
		
		int random = (int) Math.round(Math.random()*(numberOfLetters));
		int currentNumber = 0;
		
		for (int i = 0; i < probabilities.length; ++i) {
			currentNumber += probabilities[i];
			if(random < currentNumber) {
				return (char) (i + 97);
			}
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Retourne un tableau de caractère trié par leur fréquence d'apparition dans un texte ou
	 * un dictionnaire
	 * @param array Le tableau de probabilité d'apparition
	 * @return un tableau de caractères trié
	 */
	private char[] insertionSort(int[] array) {
		char[] alphabet = new char[26];
		for(int i = 0; i < 26; i++) {
			alphabet[i] = (char)(65 + i);
		}
		
		for(int i = 0; i < 26; i++) {
			int nIndex = array[i];
			char cIndex = alphabet[i];
			
			int j = i -1;
			while(j >= 0 && array[j] > nIndex) {
				array[j+1] = array[j];
				alphabet[j+1] = alphabet[j];
				j--;
			}
			array[j+1] = nIndex;
			alphabet[j+1] = cIndex;
		}
		return alphabet;
	}
	
	/**
	 * Retourne un tableau de caractères trié en fonction de leur taux d'apparition dans un texte
	 * comparé à leur taux d'apparition dans un dictionnaire francais.
	 * @param sortedLetters	Les lettres triées du texte
	 * @param sortedProbabilities Les lettres triées du dictionnaire
	 * @return un tableau de caractères
	 */
	private char[] sortAlphabet(char[] sortedLetters, char[] sortedProbabilities) {
		char[] sortedAlphabet = new char[26];
		int i = 0;
		for (char c : sortedLetters) {
			sortedAlphabet[(int) c-65] = sortedProbabilities[i];
			i++;
		}
		return sortedAlphabet;
	}
}

/**
	Ancienne méthode utilisant le dictionnaire francais général. la nouvelle méthode utilise
	les mots du dictionnaire donné afin de vérifier les chances d'apparitions d'une lettre.
	
	en Francais, le pourcentage d'apparition d'une lettre est la suivante :
	a : 8.173%		e : 16.724% (16.706%)
	b : 0.901%		a : 8.173% 
	c : 3.345%		s :	7.948%
	d : 3.669%		i : 7.529%
	e : 16.708% 	t : 7.244%
	f :	1.066%		n :	7.095%
	g :	0.866%		r :	6.693%
	h :	0.737%		u :	6.429%
	i : 7.529%		o :	5.819%
	j : 0.613%		l : 5.456%
	k :	0.074%		d : 3.669%
	l : 5.456%		c : 3.345%
	m :	2.968%		m :	2.968%
	n :	7.095%		p :	2.521%
	o :	5.819%		v :	1.838%
	p :	2.521%		q :	1.362%
	q :	1.362%		f :	1.066%
	r :	6.693%		b : 0.901%
	s :	7.948%		g :	0.866%
	t : 7.244%		h :	0.737%
	u :	6.429%		j : 0.613%
	v :	1.838%		x :	0.427%
	w :	0.049%		z :	0.326%
	x :	0.427%		y : 0.128%
	y : 0.128%		k :	0.074%
	z :	0.326%		w :	0.049%
	
	pour un total de 99.984% de charactères utilisé, les 0.016% restants sont rajoutés au 'e'
	
	methode barbare : 
	if(random > 0 && random <= 0.049) {
			return 'w';
		} else if(random > 0.049 && random <= 0.177) {
			return 'y';
		} else if(random > 0.177 && random <= 0.604) {
			return 'x';
		} else if(random > 0.604 && random <= 1.341) {
			return 'h';
		} else if(random > 1.341 && random <= 2.242) {
			return 'b';
		} else if(random > 2.242 && random <= 3.604) {
			return 'q';
		} else if(random > 3.604 && random <= 6.125) {
			return 'p';
		} else if(random > 6.125 && random <= 9.470) {
			return 'c';
		} else if(random > 9.470 && random <= 14.926) {
			return 'l';
		} else if(random > 14.926 && random <= 21.355) {
			return 'u';
		} else if(random > 21.355 && random <= 28.450) {
			return 'n';
		} else if(random > 28.450 && random <= 35.979) {
			return 'i';
		} else if(random > 35.979 && random <= 44.152) {
			return 'a';
		} else if(random > 44.152 && random <= 60.876) {
			return 'e';
		} else if(random > 60.876 && random <= 68.824) {
			return 's';
		} else if(random > 68.824 && random <= 76.068) {
			return 't';
		} else if(random > 76.068 && random <= 82.761) {
			return 'r';
		} else if(random > 82.761 && random <= 88.580) {
			return 'o';
		} else if(random > 88.580 && random <= 92.249) {
			return 'd';
		} else if(random > 92.249 && random <= 95.217) {
			return 'm';
		} else if(random > 95.217 && random <= 97.055) {
			return 'v';
		} else if(random > 97.055 && random <= 98.111) {
			return 'f';
		} else if(random > 98.111 && random <= 98.977) {
			return 'g';
		} else if(random > 98.977 && random <= 99.590) {
			return 'j';
		} else if(random > 99.590 && random <= 99.916) {
			return 'z';
		} else {
			return 'k';
		}
*/