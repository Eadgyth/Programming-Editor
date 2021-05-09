package eg.syntax;

/**
 * The interface for the search of heredoc elements
 */
public interface HeredocSearch {

    /**
     * Returns the position of the next identifier for a heredoc
     *
     * @param text  the text
     * @param start  the position where the search starts
     * @return  the position of the identifier; -1 if not found
     */
    public int nextHeredoc(String text, int start);

    /**
     * Returns the heredoc tag
     *
     * @param text  the text
     * @param pos  the position of the identifier for the heredoc
     * @param lineEnd  the position of the end of the line where
     * the tag is searcher for
     * @return  the tag; the empty string if no or an invalid tag is
     * found
     */
    public String heredocTag(String text, int pos, int lineEnd);

    /**
     * Returns if the heredoc end is valid
     *
     * @param text  the text
     * @param end  the position of the possible terminator tag
     * @param tagLength  the length of the tag
     * @return  true if valid; false otherwise
     */
    public boolean validHeredocEnd(String text, int end, int tagLength);
}
