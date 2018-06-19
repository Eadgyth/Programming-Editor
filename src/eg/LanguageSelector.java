package eg;

import eg.utils.FileUtils;

/**
 * The selection of the language based on a filename.
 * <p>
 * This method {@link #selectLanguage(String)} has to be modified
 * if a another Language is being added in {@link Languages}
 */
public class LanguageSelector {
   
   /**
    * Returns a language based on the extension of the specified file
    *
    * @param file  the file
    * @return  a language from {@link Languages}
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
         default:
            lang = Languages.NORMAL_TEXT;
      }
      return lang;
   }
}
