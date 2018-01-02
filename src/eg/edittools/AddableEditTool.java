package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.FileDocument;

/**
 * The interface to create an "edit tool" that has access to the currently
 * viewed <code>FileDocument</code> and whose view can be added to the
 * <code>ToolPanel</code> in the main window
 */
public interface AddableEditTool {
   
   /**
    * Creates the edit tool.
    * The specified button has got the action to close the tool panel added
    * and must be shown in the view.
    *
    * @param closeBt  the closing button
    */
   public void createTool(JButton closeBt);

   /**
    * Gets the Component that represents the view of the tool.
    * The Component is displayed in the main window when it is selected
    * in the menu.
    *
    * @return  the <code>Component</code>
    */
   public Component toolComponent();
   
   /**
    * Sets the <code>FileDocument</code> that is currently viewed in the
    * editor
    *
    * @param fDoc  the {@link FileDocument}
    */
   public void setFileDocument(FileDocument fDoc);
   
   /**
    * Ends the edit tool. Called when the program is exited
    */
   public void end();
}
