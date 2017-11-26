package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.FileDocument;

/**
 * The interface to create an edit tool that has acces to the currently
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
    * The Compoment is added to the {@link eg.ui.ToolPanel} in the
    * main window
    *
    * @return  the <code>Component</code>
    */
   public Component toolComponent();
   
   /**
    * Sets the <code>FileDocument</code> that is currently viewed in the
    * editor (or tab)
    *
    * @param fDoc  the {@link FileDocument}
    */
   public void setFileDocument(FileDocument fDoc);
   
   /**
    * Ends this edit tool. Called when the when the program is exited
    */
   public void end();
}
