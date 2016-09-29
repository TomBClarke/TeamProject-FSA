package automaton.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import automaton.Animation;
import automaton.AnimationDFA;
import automaton.AnimationNFA;
import automaton.AnimationTestString;
import automaton.thompsons.ConBlock;
import automaton.thompsons.RegexParser;
import gui.canvas.StateHighlights;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;

/**
 * Class for testing the automata
 * 
 * @author Piotr Wilczynski
 * @author Mihnea Patentasu
 *
 */
public class AutomatonTest {

	private ConBlock c;
	private Animation an;
	private Animation an2;

	/**
	 * Test well-formedness
	 */
	@Test
	public void testIsWellFormed() {
		assertTrue(RegexParser.isWellFormed("a".toCharArray()));
		assertTrue(RegexParser.isWellFormed("ab".toCharArray()));
		assertTrue(RegexParser.isWellFormed("".toCharArray()));
		assertTrue(RegexParser.isWellFormed("a*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("a|b".toCharArray()));
		assertTrue(RegexParser.isWellFormed("ab*|c*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("a*b*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("a*b*|cd*|ef*g".toCharArray()));
		assertTrue(RegexParser.isWellFormed("(ab|b*)*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("|b*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("ac*|".toCharArray()));
		assertTrue(RegexParser.isWellFormed("(ads*)|".toCharArray()));
		assertTrue(RegexParser.isWellFormed("|(|b)|c*".toCharArray()));
		assertTrue(RegexParser.isWellFormed("(|(|b)|c*)".toCharArray()));
		assertTrue(RegexParser.isWellFormed("((a|b)|(c|d))*".toCharArray()));
	}

	/**
	 * Test being not well-formed
	 */
	@Test
	public void testNotWellFormedness() {
		assertFalse(RegexParser.isWellFormed(")(".toCharArray()));
		assertFalse(RegexParser.isWellFormed("qwe(".toCharArray()));
		assertFalse(RegexParser.isWellFormed("*".toCharArray()));
		assertFalse(RegexParser.isWellFormed(")(()((()()()()(()".toCharArray()));
		assertFalse(RegexParser.isWellFormed("sddsf*sdf*|*asd".toCharArray()));
		assertFalse(RegexParser.isWellFormed("|sdf|s|*sdf()))".toCharArray()));
		assertFalse(RegexParser.isWellFormed("sds)".toCharArray()));
		assertFalse(RegexParser.isWellFormed("()*".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(|||||||||)*".toCharArray()));
		assertFalse(RegexParser.isWellFormed("a**".toCharArray()));
		assertFalse(RegexParser.isWellFormed("|||||||||||||||".toCharArray()));
		assertFalse(RegexParser.isWellFormed("|".toCharArray()));
		assertFalse(RegexParser.isWellFormed("asdsada||asdasf".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(|ab)*".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(asc*|)*".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(asc|)*".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(((|ab)))*a|b".toCharArray()));
		assertFalse(RegexParser.isWellFormed("(*)".toCharArray()));
	}
	
	/**
	 * Test the number of frames
	 */
	@Test
	public void testFrameCount()
	{
		char[] regex = "a".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 4);
		assertEquals(an2.getFrameCount(), 4);
		
		
		regex = "ab".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 8);
		assertEquals(an2.getFrameCount(), 6);
		
		regex = "a*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 6);
		assertEquals(an2.getFrameCount(), 4);
		
		regex = "a|b".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 8);
		assertEquals(an2.getFrameCount(), 6);
		
		regex = "ab*|c*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 16);
		assertEquals(an2.getFrameCount(), 10);
		
		regex = "a*b*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 12);
		assertEquals(an2.getFrameCount(), 8);
		
		regex = "a*b*|cd*|ef*g".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getFrameCount(), 36);
		assertEquals(an2.getFrameCount(), 24);
	}
	
	/**
	 * Test the number of states
	 */
	@Test
	public void testNumberOfStates()
	{
		char[] regex = "a".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 2);
		assertEquals(an2.getAutomaton().getNumberOfStates(), 2);
		
		regex = "ab".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 3);
		assertEquals(an2.getAutomaton().getNumberOfStates(), 3);
		
		regex = "a*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 4);
		assertEquals(an2.getAutomaton().getNumberOfStates(), 1);
		
		regex = "a|b".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 6);
		assertEquals(an2.getAutomaton().getNumberOfStates(), 3);
		
		regex = "ab*|c*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 11);
		assertEquals(an2.getAutomaton().getNumberOfStates(), 3);
		
		regex = "a*b*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 7);
		assertEquals(an2.getAutomaton().getNumberOfStates(),2);
		
		regex = "a*b*|cd*|ef*g".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an2 = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		assertEquals(an.getAutomaton().getNumberOfStates(), 22);
		assertEquals(an2.getAutomaton().getNumberOfStates(),7);
	}
	
	/**
	 * Test the number of accepting states in the last frame after a word has been checked
	 */
	@Test
	public void testNumberOfAcceptingStates()
	{
		char[] regex = "a*b*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "aaab");
		ArrayList<VisualRepresentation> frames = an.getAllFrames();
		int countAcceptingStates = 0;
		for (VisualState s : frames.get(frames.size() - 1).getStates()) {
			if (s.getHL() == StateHighlights.GREEN) {
				countAcceptingStates++;
			}
		}
		assertEquals(countAcceptingStates, 1);
		
		regex = "a*b*|cd*|ef*g".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "efffffffffffgf");
		frames = an.getAllFrames();
		countAcceptingStates = 0;
		for (VisualState s : frames.get(frames.size() - 1).getStates()) {
			if (s.getHL() == StateHighlights.GREEN) {
				countAcceptingStates++;
			}
		}
		assertEquals(countAcceptingStates, 0);

		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "aaaaaaaaabbbbbbbb");
		frames = an.getAllFrames();
		countAcceptingStates = 0;
		for (VisualState s : frames.get(frames.size() - 1).getStates()) {
			if (s.getHL() == StateHighlights.GREEN) {
				countAcceptingStates++;
			}
		}
		assertEquals(countAcceptingStates, 1);
	}

	/**
	 * Test checking words
	 */
	@Test
	public void testWordChecking() {
		char[] regex = "a*b*".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "aaab");
		ArrayList<VisualRepresentation> frames = an.getAllFrames();
		assertEquals(frames.size(), 12);
		
		regex = "a*b*|cd*|ef*g".toCharArray();
		c = RegexParser.parse(regex);
		an = new AnimationNFA(c);
		an = new AnimationDFA(an.getAutomaton(), an.getLastFrame());
		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "efffffffffffgf");
		frames = an.getAllFrames();
		assertEquals(frames.size(), 36);

		an = new AnimationTestString(an.getFinalDFAwithoutNFA(), "aaaaaaaaabbbbbbbb");
		frames = an.getAllFrames();
		assertEquals(frames.size(), 38);
	}
}