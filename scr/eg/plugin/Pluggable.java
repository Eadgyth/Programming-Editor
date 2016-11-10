package eg.plugin;

/**
 * The interface that a plugin must implement
 */
public interface Pluggable {
   
   /**
    * Sets a reference to an EditorAccess object
    * @param acc  an object of {@link EditorAccess}
    */
   public void setEditorAccess(EditorAccess acc);

   /**
    * starts a plugin
    */
   public void start();
}