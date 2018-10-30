package eg;

import eg.utils.FileUtils;

/**
 * The selection of the language based on the file extension
 */
public class LanguageSelector {
   
   /**
    * Returns a language based on the extension of the specified file
    *
    * @param file  the file
    * @return  the language
    */ 
   public static Languages selectLanguage(String file) {
      String ext = FileUtils.fileExtension(file);
      Languages lang;
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
         case "pl": case "pm":
            lang = Languages.PERL;
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
      return lang;
   }
}
