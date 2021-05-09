package eg.document;

import java.io.File;

//--Eadgyth--/
import eg.Languages;
import eg.syntax.*;

/**
 * The language that is used for a ducument
 */
public class CurrentLanguage {

   private Languages lang = Languages.NORMAL_TEXT;

   /**
    * Sets the specified language
    *
    * @param lang  the language
    */
   public void setLanguage(Languages lang) {
      this.lang = lang;
   }

   /**
    * Sets the language based on the type of file
    *
    * @param file  the file
    */
   public void setLanguage(String file) {
      String ext = fileExtension(file);
      switch (ext) {
         case "java":
            lang = Languages.JAVA;
            break;
         case "html": case "htm":
            lang = Languages.HTML;
            break;
         case "js":
            lang = Languages.JAVASCRIPT;
            break;
         case "css":
            lang = Languages.CSS;
            break;
         case "cs":
            lang = Languages.C_SHARP;
            break;
         case "php":
            if (lang == Languages.PHP_PURE) {
               break;
            }
            else {
               lang = Languages.PHP_MIXED;
               break;
            }
         case "pl": case "pm":
            lang = Languages.PERL;
            break;
         case "py":
            lang = Languages.PYTHON;
            break;
         case "R":
            lang = Languages.R;
            break;
         case "xml": case "fxml":
            lang = Languages.XML;
            break;
         default:
            lang = Languages.NORMAL_TEXT;
      }
   }

   /**
    * Returns this language
    *
    * @return  the language; default is {@link Languages#NORMAL_TEXT}
    */
   public Languages lang() {
      if (lang == null) {
         throw new IllegalStateException("A language is not set");
      }
      return lang;
   }

   /**
    * Creates a <code>Highlighter</code> for this language
    *
    * @return  the Highlighter; null if no Highlighter is
    * available for the language
    */
   public Highlighter createHighlighter() {
      Highlighter hl = null;
      switch(lang) {
         case CSS:
            hl = new CSSHighlighter();
            break;
         case C_SHARP:
            hl = new CSharpHighlighter();
            break;
         case HTML:
            hl = new HTMLHighlighter();
            break;
         case JAVA:
            hl = new JavaHighlighter();
            break;
         case JAVASCRIPT:
            hl = new JavascriptHighlighter();
            break;
         case PERL:
            hl = new PerlHighlighter();
            break;
         case PHP_MIXED:
            hl = new HTMLHighlighter();
            break;
         case PHP_PURE:
            hl = new PHPHighlighter();
            break;
         case PYTHON:
            hl = new PythonHighlighter();
            break;
         case R:
            hl = new RHighlighter();
            break;
         case XML:
            hl = new XMLHighlighter();
            break;
         case NORMAL_TEXT:
       }
       return hl;
   }

   /**
    * Returns if curly-bracket indentation is used in this
    * language
    *
    * @return  true for curly-bracket mode, false otherwise
    */
   public boolean curlyBracketMode() {
      boolean b;
      switch (lang) {
         case JAVA:
         case JAVASCRIPT:
         case PERL:
         case PHP_PURE:
         case CSS:
         case C_SHARP:
            b = true;
            break;
         default:
            b = false;
      }
      return b;
   }

   //
   //--private--/
   //

   private String fileExtension(String file) {
      int i = file.lastIndexOf('.') + 1;
      int j = file.lastIndexOf(File.separator);
      if (i > j) {
         return file.substring(i);
      }
      else {
         return "";
      }
   }
}
