package eg.projects;

/**
 * Represents a placeholder project that does not override any method
 * of {@code ProjectActions} and that is defined for text files with
 * extension '.txt'.
 */
public final class TxtActions extends ProjectConfig implements ProjectActions {
   
   TxtActions() {
      super(".txt");
   }
}
