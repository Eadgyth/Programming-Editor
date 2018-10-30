package eg;

import eg.Languages;

/**
 * The interface to set the language
*/
@FunctionalInterface
public interface LanguageChanger {

   /**
    * Changes the language
    *
    * @param lang  a language in {@link Languages}
    */
   public void change(Languages lang);
}
