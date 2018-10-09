package eg.syntax;

/**
 * Constants for strings and characters used for searching text elements
 */
public class SyntaxConstants {
   
   /**
    * The single quote character */
   public final static char SINGLE_QUOTE = '\'';
   /**
    * The double quote character */
   public  final static char DOUBLE_QUOTE = '\"';
   /**
    * The slash-star block comment start */
   public final static String SLASH_STAR = "/*";
   /**
    * The star-slash block comment end */
   public final static String STAR_SLASH = "*/";
   /**
    * The double slash line comment start */
   public final static String DOUBLE_SLASH = "//";
   /**
    * The hash sign line comment start */
   public final static String HASH = "#";
   /**
    * The markup block comment start */
   public final static String HTML_BLOCK_CMNT_START = "<!--";
   /**
    * The markup block comment end */
   public final static String HTML_BLOCK_CMNT_END = "-->";
   /**
    * The markup CDATA block start */
   public final static String CDATA_BLOCK_START = "<![CDATA[";
   /**
    * The markup CDATA block end */
   public final static String CDATA_BLOCK_END = "]]>";
   /**
    * The characters that end an XML tag */
   public final static char[] XML_TAG_END_CHARS = {
      '/', ' ', '\n', '>', '<', '&', '\"', '%', '=', '+', ';', ','
   };
   /**
    * The characters that end an XML atrribute */
   public final static char[] XML_ATTR_START_CHARS = {
      ' ', '\n' , '\"', '\'', '&', '=', ';', ','
   };
   /**
    * The characters that end an XML atrribute */
   public final static char[] XML_ATTR_END_CHARS = {
      '\n', '>', '<', '=', '&', ';', ',', '/'
   };
}
