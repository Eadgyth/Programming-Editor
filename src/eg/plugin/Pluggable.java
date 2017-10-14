package eg.plugin;

/**
 * The interface that a plugin must implement
 */
public interface Pluggable {
   
   /**
    * Sets the reference to <code>EditorAccess</code>
    * @param acc  the reference to {@link EditorAccess}
    */
   public void setEditorAccess(EditorAccess acc);

   /**
    * Starts a plugin
    */
   public void start();
}
