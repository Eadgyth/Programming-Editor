package eg.syntax;

/**
 * The interface for the search of quote operator elements
 */
public interface QuoteOperatorSearch {
  
    /**
     * Returns the position of the next quote operator keyword
     *
     * @param text  the text
     * @param start  the position where the search starts
     * @return  the position of the keyword; -1 if not found
     */
    public int nextQuoteKeyword(String text, int start);
    
    /**
     * Returns the length of the quote operator keyword
     *
     * @param text  the text
     * @param pos  the position of the keyword
     * @return  the length
     */
    public int quoteKeywordLength(String text, int pos);
    
    /**
     * Returns the length of the quotation
     *
     * @param text  the text
     * @param pos  the start position of the quotation, which
     * is the position after the quote operator keyword
     * @return  the length
     */
    public int quoteLength(String text, int pos);
}
