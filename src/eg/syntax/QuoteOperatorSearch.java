package eg.syntax;

/**
 * The interface for the search of possibly multiline quoted
 * sections that are defined by a preceeding identifier (symbol
 * or keyword) and certain delimiters.
 */
public interface QuoteOperatorSearch {

    /**
     * Returns the position of the next identifier for a quote
     * operator
     *
     * @param text  the text
     * @param pos  the position where the search starts
     * @return  the position of the identifier; -1 if not found
     */
    public int nextQuoteOperator(String text, int pos);

    /**
     * Returns the length of the identifier
     *
     * @param text  the text
     * @param pos  the position of the identifier
     * @return  the length; 0 to indicate that the identifier is
     * invalid in the given text context
     */
    public int quoteIdentifierLength(String text, int pos);

    /**
     * Returns the length of the quotation including any possible
     * delimiters
     *
     * @param text  the text
     * @param pos  the position following the identifier
     * @return  the length; 0 to indicate that no valid quote
     * follows the identifier
     */
    public int quoteLength(String text, int pos);
}
