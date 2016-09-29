package automaton.testing;

import java.util.Scanner;

import automaton.Animation;
import automaton.AnimationDFA;
import automaton.AnimationNFA;
import automaton.thompsons.ConBlock;
import automaton.thompsons.RegexParser;

/**
 * Class for experimenting with the automata
 * 
 * @author Piotr Wilczynski
 * @author Botond Megyesfalvi
 *
 */
public class AutomatonTryOut {
	public static void main(String args[]) {
		
		Scanner in = new Scanner(System.in);
		System.out.println("Type in the regular expression:");
		String s = in.nextLine();
		
		if(!RegexParser.isWellFormed(s.toCharArray())) {
			System.out.println("It's not well formed :(");
			in.close();
			return;
		}
		
		ConBlock c = RegexParser.parse(s.toCharArray());
		Animation an = new AnimationNFA(c);
		System.out.println();
		System.out.println(an);
			
		Animation an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		System.out.println(an2);
		
		in.close();
	}
}
