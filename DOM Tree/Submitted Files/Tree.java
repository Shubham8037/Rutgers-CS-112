package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 */
public class Tree {

    /**
     * Root node
     */
    TagNode root = null;

    /**
     * Scanner used to read input HTML file when building the tree
     */
    Scanner sc;

    /**
     * Initializes this tree object with scanner for input HTML file
     *
     * @param sc Scanner for input HTML file
     */
    public Tree(Scanner sc) {
        this.sc = sc;
        root = null;
    }

    /**
     * Builds the DOM tree from input HTML file, through scanner passed
     * in to the constructor and stored in the sc field of this object.
     * <p>
     * The root of the tree that is built is referenced by the root field of this object.
     */
    public void build() {

        Stack<TagNode> tagNodeStack = new Stack<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("")) continue; // ignore if some lines are blank.

            if (line.charAt(0) == '<' && line.charAt(1) != '/') { // opening tag

                String tagName = line.substring(1, line.length() - 1);
                TagNode openingTag = new TagNode(tagName, null, null);
                if (root == null) root = openingTag;
                tagNodeStack.push(openingTag);

            } else if (line.charAt(0) == '<' && line.charAt(1) == '/') { // closing tag

                TagNode openingTag = tagNodeStack.pop();
                addNodeToParent(tagNodeStack, openingTag);

            } else { //text only

                TagNode leafNode = new TagNode(line, null, null);
                addNodeToParent(tagNodeStack, leafNode);

            }
        }
    }

    private void addNodeToParent(Stack<TagNode> tagNodeStack, TagNode leafNode) {
        if (tagNodeStack.isEmpty()) return;

        TagNode parent = tagNodeStack.peek();
        if (parent.firstChild == null) {
            parent.firstChild = leafNode;
        } else {
            TagNode lastChild = parent.firstChild;
            while (lastChild.sibling != null) {
                lastChild = lastChild.sibling;
            }
            lastChild.sibling = leafNode;
        }
    }

    /**
     * Replaces all occurrences of an old tag in the DOM tree with a new tag
     *
     * @param oldTag Old tag
     * @param newTag Replacement tag
     */
    public void replaceTag(String oldTag, String newTag) {
        replaceTag(root, oldTag, newTag);
    }

    private void replaceTag(TagNode root, String oldTag, String newTag) {
        TagNode next = root;
        while (next != null) {
            if (next.firstChild == null) {
                if (next.tag.equals(oldTag)) {
                    next.tag = newTag;
                }
            } else {
                if (next.tag.equals(oldTag)) {
                    next.tag = newTag;
                }
                replaceTag(next.firstChild, oldTag, newTag);
            }
            next = next.sibling;
        }
    }

    /**
     * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
     * tag appears directly under the td tag of every column of this row.
     *
     * @param row Row to bold, first row is numbered 1 (not 0).
     */
    public void boldRow(int row) {
        boldRowImpl(root, row);
    }

    private void boldRowImpl(TagNode root, int row) {
        TagNode next = root;
        while (next != null) {
            if (next.firstChild != null) {
                if (next.tag.equals("table")) {
                    //next.tag = newTag;
                    TagNode child = next.firstChild;
                    int currentRow = 1;

                    while (row != currentRow) {
                        currentRow++;
                        if (child == null) break;
                        child = child.sibling;
                    }

                    if (child != null) {
                        boldThisRow(child);
                    }
                }
                boldRowImpl(next.firstChild, row);
            }
            next = next.sibling;
        }
    }

    private void boldThisRow(TagNode tagNode) {
        TagNode columnNode = tagNode.firstChild;
        while (columnNode != null) {
            columnNode.firstChild = new TagNode("b", columnNode.firstChild, null);
            columnNode = columnNode.sibling;
        }
    }

    /**
     * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
     * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
     * in addition, all the li tags immediately under the removed tag are converted to p tags.
     *
     * @param tag Tag to be removed, can be p, em, b, ol, or ul
     */
    public void removeTag(String tag) {
        switch (tag) {
            case "p":
            case "em":
            case "b":
            case "ol":
            case "ul":
                removeTagImpl(root, null, tag);
                break;
            default:
                System.err.println("Invalid tag!");
                break;
        }
    }

    private void removeTagImpl(TagNode root, TagNode parent, String tag) {
        TagNode next = root;
        while (next != null) {
            if (next.firstChild != null) {

                removeTagImpl(next.firstChild, next, tag);

                if (next.tag.equals(tag) && parent != null) {
                    TagNode nextChild = parent.firstChild;
                    if (nextChild == next) {
                        parent.firstChild = next.firstChild;

                        adjustTag(next);

                    } else {
                        TagNode leftSibling = null;
                        while (nextChild != next) {
                            leftSibling = nextChild;
                            nextChild = nextChild.sibling;
                        }
                        leftSibling.sibling = next.firstChild;

                        adjustTag(next);
                    }
                }
            }
            next = next.sibling;
        }
    }

    private void adjustTag(TagNode next) {
        TagNode lastChild = next.firstChild;
        while (lastChild.sibling != null) {
            lastChild = lastChild.sibling;
        }
        lastChild.sibling = next.sibling;

        TagNode child = next.firstChild;
        while (child != null) {
            if (child.tag != null) {
                if (child.tag.equals("li")) {
                    child.tag = "p";
                }
                child = child.sibling;
            } else {
                break;
            }
        }
    }

    /**
     * Adds a tag around all occurrences of a word in the DOM tree.
     *
     * @param word Word around which tag is to be added
     * @param tag  Tag to be added
     */
    public void addTag(String word, String tag) {
        if (tag.equalsIgnoreCase("em") || tag.equalsIgnoreCase("b")) {
            addTagImpl(root, null, word, tag);
        }
    }

    private void addTagImpl(TagNode root, TagNode parent, String word, String tag) {
        TagNode next = root;
        while (next != null) {
            if (next.firstChild == null) {
                if (next.tag.toLowerCase().contains(word.toLowerCase())) {
                    tryToAddTag(next, parent, word, tag);
                }
            } else {
                addTagImpl(next.firstChild, next, word, tag);
            }
            next = next.sibling;
        }
    }

    private void tryToAddTag(TagNode node, TagNode parent, String word, String tag) {

        String text = node.tag;
        String[] tokens = text.split(" ");

        ArrayList<String> words = new ArrayList<>();
        ArrayList<TagNode> tags = new ArrayList<>();

        for (String token : tokens) {
            if (token.toLowerCase().startsWith(word.toLowerCase())) {
                if (token.length() == word.length()) {
                    if (!words.isEmpty()) {
                        tags.add(new TagNode(getCombinedString(words), null, null));
                    }
                    TagNode newTag = new TagNode(tag, new TagNode(token, null, null), null);
                    tags.add(newTag);
                } else if (token.length() == word.length() + 1) {
                    int nextChar = token.charAt(word.length());
                    if (nextChar == ',' || nextChar == '?' || nextChar == '!' || nextChar == ':' ||
                            nextChar == ';' || nextChar == '.') {
                        if (!words.isEmpty()) {
                            tags.add(new TagNode(getCombinedString(words), null, null));
                        }
                        TagNode newTag = new TagNode(tag, new TagNode(token, null, null), null);
                        tags.add(newTag);
                    } else {
                        words.add(token);
                    }
                } else {
                    words.add(token);
                }
            } else {
                words.add(token);
            }
        }
        if (!words.isEmpty()) {
            tags.add(new TagNode(getCombinedString(words), null, null));
        }

        final TagNode firstChild = tags.get(0);
        TagNode previousChild = firstChild;
        for (int i = 1, length = tags.size(); i < length; i++) {
            previousChild.sibling = tags.get(i);
            previousChild = previousChild.sibling;
        }

        if (parent.firstChild.equals(node)) {
            parent.firstChild = firstChild;
        } else {
            TagNode sibling = parent.firstChild;
            while (!sibling.sibling.equals(node)) {
                sibling = sibling.sibling;
            }
            sibling.sibling = firstChild;
        }

    }

    private String getCombinedString(ArrayList<String> words) {
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word).append(" ");
        }
        words.clear();
        return builder.toString();
    }

    /**
     * Gets the HTML represented by this DOM tree. The returned string includes
     * new lines, so that when it is printed, it will be identical to the
     * input file from which the DOM tree was built.
     *
     * @return HTML string, including new lines.
     */
    public String getHTML() {
        StringBuilder sb = new StringBuilder();
        getHTML(root, sb);
        return sb.toString();
    }

    private void getHTML(TagNode root, StringBuilder sb) {
        for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
            if (ptr.firstChild == null) {
                sb.append(ptr.tag);
                sb.append("\n");
            } else {
                sb.append("<");
                sb.append(ptr.tag);
                sb.append(">\n");
                getHTML(ptr.firstChild, sb);
                sb.append("</");
                sb.append(ptr.tag);
                sb.append(">\n");
            }
        }
    }

    /**
     * Prints the DOM tree.
     */
    public void print() {
        print(root, 1);
    }

    private void print(TagNode root, int level) {
        for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
            for (int i = 0; i < level - 1; i++) {
                System.out.print("      ");
            }
            if (root != this.root) {
                System.out.print("|---- ");
            } else {
                System.out.print("      ");
            }
            System.out.println(ptr.tag);
            if (ptr.firstChild != null) {
                print(ptr.firstChild, level + 1);
            }
        }
    }
}
