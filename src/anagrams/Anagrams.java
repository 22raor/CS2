package anagrams;

import java.awt.Font;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * @author 22raor
 *
 */
public class Anagrams {

	public static ArrayList<String> dictionary = new ArrayList<>();
	public static HashSet<String> anagrams; // hashset instead of arraylist to automatically remove duplicates

	public static void main(String[] args) throws Exception {
		boolean allowExtraSpaces = false; // for allowing series of words using more spaces than are in original string

		System.out.println("running");

		File dict = new File("words.txt"); // dictionary file

		dictionary.addAll(Files.lines(dict.toPath()).map(x -> x.toLowerCase()).collect(Collectors.toList())); //add dictionary

		String anagram = JOptionPane.showInputDialog("Enter Word");
		anagram += allowExtraSpaces ? " ".repeat(anagram.length() - 1) : "";

		anagrams = new HashSet<>();

		getAnagrams("", anagram);

		ArrayList<String> sorted = new ArrayList<>(anagrams);
		Collections.sort(sorted);
		
		
		String all = "\nAll Anagrams: \n" + sorted;

		sorted.removeIf(x -> !inDict(x)); // removes non-word anagrams
		String real = "\nReal Anagrams: \n" + sorted;
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame output = new JFrame("Anagrams");

		JLabel out = new JLabel();
		out.setText("<html>" + all + "<br/><br/><br/>" + real + "</html>");
		out.setFont(new Font("Serif", Font.PLAIN, 14));

		JScrollPane pane = new JScrollPane(out);
		output.add(pane);
		// JTextField field = new JTextField(10);
		
		// output.add(field);
		
		output.pack();
		output.setLocationRelativeTo(null);
		output.setExtendedState(JFrame.MAXIMIZED_BOTH);
		output.setVisible(true);
		output.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @param prefix The string to which anagrams are concatenated to
	 * @param word   The word to be permuted
	 */
	public static void getAnagrams(String prefix, String word) {
		if (word.length() != 1) {
			String prf = prefix;
			for (int i = 0; i < word.length(); i++) { //z swaps first letter with each other letter and runs recursively
				String swapped = swap(word, i);
				anagrams.add((prf + swapped).trim());
				prefix = prf + swapped.charAt(0);
				getAnagrams(prefix, swapped.substring(1, word.length()));
			}
		}

	}

	/**
	 * @param a     String input to be swapped
	 * @param index Index of character to be swapped with first character
	 * @return String with the characters swapped
	 */
	public static String swap(String a, int index) {
		char b = a.charAt(index);
		StringBuilder str = new StringBuilder(a);
		str.setCharAt(index, a.charAt(0));
		str.setCharAt(0, b);
		return str.toString();
	}

	public static String rpl(char a, String b, int index) {
		char[] zz = b.toCharArray();
		zz[index] = a;
		return new String(zz);

	}

	// Checks if word(s) is in dictionary
	public static boolean inDict(String x) {
		String words[] = x.split(" ");
		for (String i : words) {
			if (!dictionary.contains(i)) {
				return false;
			}
		}
		return true;

	}

}
