package automaton.thompsons;

/**
 * Class containing methods for parsing a regular expression
 *
 * @author Danyil Ilchenko
 * @author Botond Megyesfalvi
 * @author Piotr Wilczynski
 *
 */
public class RegexParser {
	
	/**
	 * Check if a regular expression is well formed
	 * @param regex
	 * @return True if it is a well formed regular expression
	 */
	public static boolean isWellFormed(char[] regex) {
		// make sure it is not just an OR
		if(regex.length == 1 && regex[0] == '|') {
			return false;
		}
		
		// make sure it is well bracketed
		int brackets = 0;
		
		// make sure there is no empty component before a star
		boolean emptyComponent = false;
		
		// loop through the regular expression
		for(int i = 0; i < regex.length; i++) {
			if(regex[i] == '(') {
				if(i < regex.length - 1) {
					if(regex[i + 1] == ')') {
						emptyComponent = true;
					}
				}
				brackets++;
			} else if(regex[i] == ')') {
				brackets--;
				//charBeforeStar = true;
				if(brackets < 0) {
					// not well bracketed
					return false;
				}
			} else if(regex[i] == '*') {
				// we should not put a star after an empty string
				if(i == 0) {
					return false;
				}
				if(regex[i - 1] == '*') {
					return false;
				}
				if(regex[i - 1] == '(') {
					return false;
				}
				if(regex[i - 1] == ')') {
					if(emptyComponent) {
						return false;
					}
				}
			} else if(regex[i] == '|') {
				if(i > 0) {
					if(regex[i - 1] == '|') {
						return false;
					}
					if(regex[i - 1] == '(') {
						emptyComponent = true;
					}
				}
				if(i < regex.length - 1) {
					if(regex[i + 1] == '*') {
						return false;
					}
					if(regex[i + 1] == ')') {
						emptyComponent = true;
					}
				}
			}
			if(i > 0) {
				if(regex[i - 1] == ')' && regex[i] != ')') {
					emptyComponent = false;
				}
			}
		}
		return brackets == 0;
	}

	/**
	 * Parse a RegEx into construction blocks for Thompson's construction algorithm
	 * @param regex The regular expression
	 * @return The ConBlock
	 */
	public static ConBlock parse(char[] regex) {
		if (regex.length == 0)
			return new ConBlock(BlockType.EMPTY,null);
		int disjs = findOr(regex);
		if (disjs == -1) {
			// the case when there is no global OR
			if (regex[0] == '(') {
				// case when the current expression is a bracketed expression
				String r = new String(regex);
				int endbrack = complbrack(r.substring(1).toCharArray());
				if (endbrack == regex.length - 1) {
					// case when the whole regex is a bracketed expression
					return parse(r.substring(1, endbrack).toCharArray());
				} else {
					if (regex[++endbrack] == '*') {
						// case when brackets serve the purpose of expanding
						// STAR operator on more than one char
						if (endbrack == regex.length - 1) {
							// case when the whole regex is a bracketed
							// expression with a star
							return new ConBlock(BlockType.STAR,
									new ConBlock[] { parse(r.substring(1, --endbrack).toCharArray()) });
						} else {
							// case when a bracketed expression with a star is
							// followed by another expression
							return new ConBlock(BlockType.CONCAT,
									new ConBlock[] {
											new ConBlock(BlockType.STAR,
													new ConBlock[] { parse(r.substring(1, --endbrack).toCharArray()) }),
											parse(r.substring(endbrack + 2, regex.length).toCharArray()) });
						}
					} else {
						// case when a bracketed expression is followed by
						// another expression
						return new ConBlock(BlockType.CONCAT,
								new ConBlock[] { parse(r.substring(1, --endbrack).toCharArray()),
										parse(r.substring(++endbrack, regex.length).toCharArray()) });
					}
				}
			} else {
				// case when the current expression is a char
				if (regex.length == 1) {
					// case when the whole regex is a char
					return new ConBlock(regex[0]);
				} else {
					String r = new String(regex);
					if (regex[1] == '*') {
						// case when current expression is a char followed by a
						// STAR
						if (regex.length == 2) {
							// case when the whole regex is a char followed by a
							// STAR
							return new ConBlock(BlockType.STAR, new ConBlock[] { new ConBlock(regex[0]) });
						}
						// case when a char followed by a star is followed by
						// another expression
						return new ConBlock(BlockType.CONCAT,
								new ConBlock[] {
										new ConBlock(BlockType.STAR, new ConBlock[] { new ConBlock(regex[0]) }),
										parse(r.substring(2).toCharArray()) });
					} else {
						// case when a char is followed by another expression
						return new ConBlock(BlockType.CONCAT,
								new ConBlock[] { new ConBlock(regex[0]), parse(r.substring(1).toCharArray()) });
					}
				}
			}
		} else {
			// case when there is a global OR
			String r = new String(regex);
			// create a construction block for the whole regex
			if(disjs < 1){
				return new ConBlock(BlockType.DISJ,
						new ConBlock[] {
								// create a construction block for expression to the
								// left of the OR element
								new ConBlock(BlockType.EMPTY, null),
								// create a construction block for expression to the
								// right of the OR element
								parse(r.substring(++disjs).toCharArray()) });
			}else{
				if(disjs + 1 == r.length()){
					return new ConBlock(BlockType.DISJ,
							new ConBlock[] {
									// create a construction block for expression to the
									// left of the OR element
									parse(r.substring(0, disjs).toCharArray()),
									// create a construction block for expression to the
									// right of the OR element
									new ConBlock(BlockType.EMPTY, null) });
				}else{
					return new ConBlock(BlockType.DISJ,
							new ConBlock[] {
									// create a construction block for expression to the
									// left of the OR element
									parse(r.substring(0, disjs).toCharArray()),
									// create a construction block for ex,pression to the
									// right of the OR element
									parse(r.substring(++disjs).toCharArray()) });
				}
			}
			
		}
	}

	/**
	 * Find global "|" (OR) element in the RegEx, ignoring the ones in brackets
	 * and return its index
	 * 
	 * @param regex
	 *            The RegEx
	 * @return The global "|"'s index
	 */
	private static int findOr(char[] regex) {
		int brackets = 0;
		for (int i = 0; i < regex.length; i++) {
			char val = regex[i];
			if (val == '(')
				brackets++;
			if (val == ')')
				brackets--;
			if (val == '|' && brackets == 0)
				return i;
		}
		return -1;
	}

	/**
	 * Identify a bracketed expression
	 * 
	 * @param regex
	 *            The RegEx
	 * @return The index of the closing bracket of this block
	 */
	private static int complbrack(char[] regex) {
		int brackets = 1;
		int counter = 0;
		while (brackets != 0) {
			char val = regex[counter];
			if (val == '(')
				brackets++;
			if (val == ')')
				brackets--;
			counter++;
		}
		return counter;
	}
}