package automaton.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import automaton.thompsons.BlockType;
import automaton.thompsons.ConBlock;
import automaton.thompsons.RegexParser;

/**
 * Class for testing the ConBlocks
 * 
 * @author Piotr Wilczynski
 * @author Mihnea Patentasu
 *
 */
public class ConBlockTest {
	
	private ConBlock c;

	/**
	 * Test the type
	 */
	@Test
	public void testType() {
		c = RegexParser.parse("a".toCharArray());
		assertTrue(c.getType() == BlockType.ELEM);
		c = RegexParser.parse("a|b".toCharArray());
		assertTrue(c.getType() == BlockType.DISJ);
		c = RegexParser.parse("ab".toCharArray());
		assertTrue(c.getType() == BlockType.CONCAT);
		c = RegexParser.parse("a*".toCharArray());
		assertTrue(c.getType() == BlockType.STAR);
		c = RegexParser.parse("((ab|c)*|d)".toCharArray());
		assertTrue(c.getType() == BlockType.DISJ);
	}
	
	/**
	 * Test the contents
	 */
	@Test
	public void testContents() {
		c = RegexParser.parse("(ab|c)*|d".toCharArray());
		ConBlock disj1 = c.getInsides()[0];
		ConBlock disj2 = c.getInsides()[1];
		assertTrue(disj1.getType() == BlockType.STAR);
		assertTrue(disj1.toString().equals("((((ab)|c))*)"));
		assertTrue(disj2.getType() == BlockType.ELEM);
		assertTrue(disj2.toString().equals("d"));
		ArrayList<String> contents = new ArrayList<>();
		ArrayList<ConBlock> secondInsides = new ArrayList<>();
		ConBlock current = c;
		while (current.getInsides() != null) {
			if (current.getType() == BlockType.ELEM) {
				contents.add(current.toString());
			}
			if (current.getType() == BlockType.STAR) {
				contents.add(current.getInsides()[0].toString());
				current = current.getInsides()[0];
			} else {
				contents.add(current.getInsides()[0].toString());
				contents.add(current.getInsides()[1].toString());
				secondInsides.add(current.getInsides()[1]);
				current = current.getInsides()[0];
			}
		}
		for (int i = 0; i < secondInsides.size(); i++) {
			current = secondInsides.get(i);
			while (current.getInsides() != null) {
				if (current.getType() == BlockType.ELEM) {
					contents.add(current.toString());
				}
				if (current.getType() == BlockType.STAR) {
					contents.add(current.getInsides()[0].toString());
					current = current.getInsides()[0];
				} else {
					contents.add(current.getInsides()[0].toString());
					contents.add(current.getInsides()[1].toString());
					secondInsides.add(current.getInsides()[1]);
					current = current.getInsides()[0];
				}
			}
		}
		String[] expectedResults = { "((((ab)|c))*)", "a", "c", "(ab)", "((ab)|c)", "b", "d" };
		Arrays.sort(expectedResults);
		Collections.sort(contents);
		assertArrayEquals(contents.toArray(), expectedResults);
	}
	
	/**
	 * Test the string form
	 */
	@Test
	public void testToString() {
		char[] regex = "a".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "a");
		
		regex = "ab".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "(ab)");

		regex = "a*".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "((a)*)");
		
		regex = "a|b".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "(a|b)");
		
		regex = "ab*|c*".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "((a((b)*))|((c)*))");
		
		regex = "a*b*".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "(((a)*)((b)*))");
		
		regex = "a*b*|cd*|ef*g".toCharArray();
		c = RegexParser.parse(regex);
		assertEquals(c.toString(), "((((a)*)((b)*))|((c((d)*))|(e(((f)*)g))))");
	}
}