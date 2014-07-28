

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {

	private static final String SUBTREE_REGEX = "\"[A-Za-z]+\": \\[[^\\]]+\\]";
	private static final Pattern SUBTREE_PATTERN = Pattern
			.compile(SUBTREE_REGEX);

	private static final String MOTHER_REGEX = "\"([A-Za-z]+)\":";
	private static final Pattern MOTHER_PATTERN = Pattern.compile(MOTHER_REGEX);

	private static final String CHILDREN_REGEX = "\\[[^\\]]*\\]";
	private static final Pattern CHILDREN_PATTERN = Pattern
			.compile(CHILDREN_REGEX);

	private static final String CHILD_REGEX = "\"([A-Za-z]+)\"";
	private static final Pattern CHILD_PATTERN = Pattern.compile(CHILD_REGEX);

	static class Person {

		private final String _name;
		private final Set<Person> _children = new HashSet<Person>();
		private Person _mother;

		Person(final String name_) {
			_name = name_;
		}

		void addChild(final Person child_) {
			_children.add(child_);
		}

		/**
		 * Flip the hasParents flag to true
		 */
		void setMother(final Person mother_) {
			_mother = mother_;
		}

		boolean isRoot() {
			return _mother != null;
		}

		String getName() {
			return _name;
		}

		Set<Person> getChildren() {
			return _children;
		}

		Person getMother() {
			return _mother;
		}

		@Override
		public String toString() {
			String root = isRoot() ? "" : "(ROOT)";
			return _name + root + _children;
		}

	}

	static Map<String, Person> parseTree(final String treeString_) {
		System.out.println("Parsing tree.");
		Map<String, Person> peopleMap = new HashMap<String, Person>();

		// Parse the tree string into a set of subtree strings
		Set<String> subtrees = new HashSet<String>();
		Matcher subtreeMatcher = SUBTREE_PATTERN.matcher(treeString_);
		while (subtreeMatcher.find()) {
			String subtree = subtreeMatcher.group();
			subtrees.add(subtree);
			System.out.println(subtree);
		}

		for (String subtree : subtrees) {
			peopleMap = parseSubtree(subtree, peopleMap);
			// System.out.println("People map = " + peopleMap);
		}

		System.out.println("Finished parsing tree into " + peopleMap);
		return peopleMap;

	}

	static Map<String, Person> parseSubtree(final String subtreeString_,
			final Map<String, Person> peopleSoFar_) {
		System.out.println("Parsing subtree.");
		Matcher motherMatcher = MOTHER_PATTERN.matcher(subtreeString_);
		Matcher childrenMatcher = CHILDREN_PATTERN.matcher(subtreeString_);

		// Get the mother
		Person mother = null;
		if (motherMatcher.find()) {
			String motherName = motherMatcher.group(1);
			if (peopleSoFar_.containsKey(motherName)) {
				mother = peopleSoFar_.get(motherName);
			} else {
				mother = new Person(motherName);
				peopleSoFar_.put(motherName, mother);
			}
		} else {
			// TODO throw exception
		}

		// Extract the children string
		String childrenString = null;
		if (childrenMatcher.find()) {
			childrenString = childrenMatcher.group();
		} else {
			// TODO throw exception
		}

		// Pull the children out of the children string
		Matcher childMatcher = CHILD_PATTERN.matcher(childrenString);
		while (childMatcher.find()) {
			String childName = childMatcher.group(1);
			Person child;
			if (peopleSoFar_.containsKey(childName)) {
				child = peopleSoFar_.get(childName);
			} else {
				child = new Person(childName);
				peopleSoFar_.put(childName, child);
			}
			child.setMother(mother);
			mother.addChild(child);
		}
		System.out.println(mother);

		return peopleSoFar_;
	}

	static String common(String tree_str, String name1, String name2) {
		// TODO Check for nulls

		// If the two people are the same, return that person
		if (name1 != null && name1.equals(name2)) {
			return name1;
		}

		Map<String, Person> peopleMap = parseTree(tree_str);

		// Find the root. Assumes exactly one root.
		Person root = null;
		for (Map.Entry<String, Person> entry : peopleMap.entrySet()) {
			Person person = entry.getValue();
			if (person.isRoot()) {
				root = person;
			}
		}
		// May not have needed this after all...

		// Go up from each name
		Person person1 = peopleMap.get(name1);
		Person person2 = peopleMap.get(name2);

		Person person1NextGen = person1.getMother();
		Person person2NextGen = person2.getMother();
		Set<Person> person1AncestorsSoFar = new HashSet<Person>();
		person1AncestorsSoFar.add(person1);
		Set<Person> person2AncestorsSoFar = new HashSet<Person>();
		person2AncestorsSoFar.add(person2);
		do {
			Set<Person> ancestorsInCommon = anyoneInCommon(
					person1AncestorsSoFar, person2AncestorsSoFar);
			if (!ancestorsInCommon.isEmpty()) {
				if (ancestorsInCommon.size() == 1) {
					return ancestorsInCommon.iterator().next().getName();
				} else {
					// TODO throw exception?
					return ancestorsInCommon.iterator().next().getName();
				}
			} else {
				// Keep going up the tree.
				if (person1NextGen != null) {
					person1AncestorsSoFar.add(person1NextGen);
					person1NextGen = person1NextGen.getMother();
				}
				if (person2NextGen != null) {
					person2AncestorsSoFar.add(person2NextGen);
					person2NextGen = person2NextGen.getMother();
				}
			}
		} while (person1NextGen != null || person2NextGen != null);

		// Perform one last check
		Set<Person> ancestorsInCommon = anyoneInCommon(
				person1AncestorsSoFar, person2AncestorsSoFar);
		if (!ancestorsInCommon.isEmpty()) {
			if (ancestorsInCommon.size() == 1) {
				return ancestorsInCommon.iterator().next().getName();
			} else {
				// TODO throw exception?
				return ancestorsInCommon.iterator().next().getName();
			}
		}
			
		return null;

	}

	static Set<Person> anyoneInCommon(final Set<Person> peopleA_,
			final Set<Person> peopleB_) {
		Set<Person> copyA = new HashSet<Person>(peopleA_);
		copyA.retainAll(peopleB_);
		return copyA;
	}

	public static void main(String[] args_) {
		// Scanner in = new Scanner(System.in);
		String res;
		String _tree_str;
		_tree_str = "{\"Ann\": [\"Betty\", \"Clare\"], "
				+ "\"Betty\": [\"Donna\", \"Elizabeth\", \"Flora\"], "
				+ "\"Clare\": [\"Gloria\", \"Hazel\"]}";
		// _tree_str = in.nextLine();

		String _name1;
		_name1 = "Donna";
		// _name1 = in.nextLine();

		String _name2;
		_name2 = "Elizabeth";
		// _name2 = in.nextLine();

		res = common(_tree_str, _name1, _name2);
		System.out.println(res);
		// String familyTreeString = args_[0];
		// String personA = args_[1];
		// String personB = args_[2];
	}

}
