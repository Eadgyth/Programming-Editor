package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * The interface to highlight text elements.
 * <p>
 * Highlighting is done by calling selected 'search methods' in
 * {@link SyntaxSearcher} from this
 * {@link #highlight(SyntaxSearcher, Attributes)} method.
 * However, the sequence of method calls matters. An example
 * is:<blockquote>
 * {@link SyntaxSearcher#resetAttributes()}<br>
 * {@link SyntaxSearcher#quote(boolean)}<br>
 * {@link SyntaxSearcher#lineComments(String[])}<br>
 * {@link SyntaxSearcher#keywords(String[],char[],SimpleAttributeSet)}<br>
 * {@link SyntaxSearcher#blockComments(String,String,boolean)}</blockquote>
 * For special validation of found text elements a 'condition' may be
 * set by calling {@link SyntaxSearcher#setCondition(int)} before one
 * or more search methods. The validation is implemented in
 * {@link #isValid(String, int, int)}.
 * <p>
 * Implementations of {@link #behindLineCmntMark(String, int)} and/or
 * {@link #inBlockCmntMarks(String, int)} must ignore if comment marks
 * are quoted.
 * @see SyntaxUtils#behindMark(String,String,int)
 * @see SyntaxUtils#inBlock(String,String,String,int)
 */
public interface Highlighter {

   /**
    * Defines the syntax highlighting
    *
    * @param s  the SyntaxSearcher
    * @param attr  the Attributes that may be set on text elements
    */
   public void highlight(SyntaxSearcher s, Attributes attr);

   /**
    * Returns if a text element found at the specified position is
    * valid
    *
    * @param text  the text
    * @param pos  the position
    * @param condition  the condition for validating a text element.
    * @return  true if valid (or no additional validation is
    * necessary); false otherwise
    */
   public boolean isValid(String text, int pos, int condition);

   /**
    * Returns if the specified position is found behind a line
    * comment mark in the same line
    *
    * @param text  the text
    * @param pos  the position
    * @return  the position of the last line comment mark; -1 if no
    * mark is found or if line comment marks can be ignored
    */
   public int behindLineCmntMark(String text, int pos);

   /**
    * Returns if the specified position is found between the marks
    * that define a block comment
    *
    * @param text  the text
    * @param pos  the position
    * @return the position of the last block comment start; -1 if
    * not between comment marks or if block comments can be ignored
    */
   public int inBlockCmntMarks(String text, int pos);
}
