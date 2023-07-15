package eg.projects;

/**
 * Constants for project types
 */
public enum ProjectTypes {
   /**
    * Constant for a Java project;
    * value returned by {@link display()} is Java */
   JAVA("Java"),
   /**
    * Constant for a C# project
    * value returned by {@link display()} is C# */
   CSHARP("C#"),
   /**
    * Constant for an HTML project;
    * value returned by {@link display()} is HTML */
   HTML("HTML"),
   /**
    * Constant for a Perl project;
    * value returned by {@link display()} is Perl */
   PERL("Perl"),
   /**
    * Constant for a Python project;
    * value returned by {@link display()} is Python */
   PYTHON("Python"),
   /**
    * Constant for an R project;
    * value returned by {@link display()} is R */
   R("R"),
   /**
    * Constant for a generic project;
    * value returned by {@link display()} is Custom */
   GENERIC("Custom commands");

   /**
    * Returns the display value associated with a project category
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }

   private final String display;

   private ProjectTypes(String display) {
      this.display = display;
   }
}
