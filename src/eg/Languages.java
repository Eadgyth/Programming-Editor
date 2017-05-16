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
   
   public static String[] displayArr() {
      String[] dis = new String[4];
      for (int i = 0; i < 4; i++) {
         dis[i] = Languages.values()[i].display();
      }
      return dis;
   }
   
    public static String[] displayArrWithoutPlain() {
      String[] dis = new String[3];
      for (int i = 1; i < 4; i++) {
         dis[i - 1] = Languages.values()[i].display();
      }
      return dis;
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
