package eg;

/**
 * Constants for languages
 */
public enum Languages {

   PLAIN_TEXT("Plain text"),
   JAVA("Java"),
   HTML("Html"),
   JAVASCRIPT("Javascript"),
   PERL("Perl");
   
   private String display;
   
   private Languages(String display) {
      this.display = display;
   }
   
   /**
    * Returns the display value associated with a language
    * constant
    *
    * @return  the display value for a language
    */
   public String display() {
      return display;
   }
   
   /**
    * Returns the language constant that is associated with the
    * specified display value
    *
    * @param display  the display value for the language constant
    * @return  the language constant with the specified display value
    */
   public static Languages languageByDisplay(String display) {
      Languages lang = null;
      for (Languages l : Languages.values()) {
         if (l.display().equals(display)) {
            lang = l;
            break;
         }
      }
      return lang;
   }
}
