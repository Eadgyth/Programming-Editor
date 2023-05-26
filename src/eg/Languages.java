package eg;

/**
 * Constants for languages
 */
public enum Languages {

   NORMAL_TEXT("Normal text"),
   C_SHARP("C#"),
   CSS("CSS"),
   HTML("HTML"),
   JAVA("Java"),
   JAVASCRIPT("Javascript"),
   PERL("Perl"),
   PHP_MIXED("PHP (embedded)"),
   PHP_PURE("PHP (pure)"),
   PYTHON("Python"),
   R("R"),
   XML("XML");

   /**
    * Returns the Language constant with the name given
    * by the specified String (calling Enum.valueOf(String))
    * or NORMAL_TEXT if the name does not match a Languages
    * constant.
    *
    * @param s the String
    * @return  the Language or NORMAL_TEXT if no enum
    * type with the name in s is present
    */
   public static Languages initialLanguage(String s) {
      try {
         return Languages.valueOf(s);
      }
      catch (IllegalArgumentException e) {
         return Languages.NORMAL_TEXT;
      }
      catch (NullPointerException e) {
         eg.utils.FileUtils.log(e);
         return Languages.NORMAL_TEXT;
      }
   }

   /**
    * Returns the display value associated with a language
    * constant
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }

   private final String display;

   private Languages(String display) {
      this.display = display;
   }
}
