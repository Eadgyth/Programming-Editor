package eg;

/**
 * Constants for languages
 */
public enum Languages {

   PLAIN_TEXT("Plain text"),
   JAVA("Java"),
   HTML("Html"),
   PERL("Perl");
   
   private String display;
   
   Languages(String display) {
      this.display = display;
   }
   
   public String display() {
      return display;
   }
   
   public static Languages langByDisplay(String display) {
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
