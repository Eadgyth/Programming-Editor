package eg.document;

/**
 * The interface for controlling the updating done in <code>
 * EditorUpdating</code> for possibly multiline text changes
 */
@FunctionalInterface
public interface TextChange {

   /**
    * Makes a text change
    */
   public void edit();
}
