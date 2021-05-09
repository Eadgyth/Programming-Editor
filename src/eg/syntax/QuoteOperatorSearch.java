package eg.syntax;

/**
 * The interface for the search of possibly multiline string literals
 * that are defined by a preceeding identifier (symbol or keyword) and
 * certain delimiters.
 */
public interface QuoteOperatorSearch {

    /**
     * Returns the position of the next identifier for a quote
     * operator
     *
     * @param text  the text
     * @param start  the position where the search starts
     * @return  the position of the identifier; -1 if not found
     */
    public int nextQuoteOperator(String text, int start);

    /**
     * Returns the length of the identifier
     *
     * @param text  the text
     * @param pos  the position of the identifier
     * @return  the length
     */
    public int quoteIdentifierLength(String text, int pos);

    /**
     * Returns the length of the quotation
     *
     * @param text  the text
     * @param pos  the position following the identifier
     * @return  the length; can be 0 to indicate that no valid
     * quote follows the identifier
     */
    public int quoteLength(String text, int pos);
}
