package eg;

import java.io.File;

/**
 * The interface to open a file
 */
public interface FileOpener {

   /**
    * Opens a file
    *
    * @param f  the file
    */
   public void open(File f);
}
