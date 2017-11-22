package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

import eg.document.FileDocument;

/**
 * The interface to create and add an edit tool to the <code>ToolPanel</code>
 * in the main window
 */
public interface AddableEditTool {
   
   /**
    * Creates the Component that can be added to the <code>ToolPanel</code>.
    * The specified button has got the action to close the tool panel added
    * and must be shown in the view.
    *
    * @param closeBt  the closing button
    */
   public void createToolPanel(JButton closeBt);

   /**
    * Returns the Component to be added to the <code>ToolPanel</code>
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
    * Ends this edit tool when the program is shut down
    */
   public void end();
}
