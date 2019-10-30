package eg.projects;

/**
 * Constants for project types
 */
public enum ProjectTypes {

   HTML("HTML"),
   JAVA("Java"),
   PERL("Perl"),
   PYTHON("Python"),
   R("R"),
   GENERIC("Custom cmd");

   private final String display;

   /**
    * Returns the display value associated with a project category
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }

   private ProjectTypes(String display) {
      this.display = display;
   }
}
