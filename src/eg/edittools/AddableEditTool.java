package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.EditableDocument;

/**
 * The interface to create an "edit tool" that has access to the currently
 * viewed <code>EditableDocument</code> and whose view can be added to the
 * <code>ToolPanel</code> in the main window
 */
public interface AddableEditTool {
   
   /**
    * Initializes the Component that represents the graphical view of the tool.
    * <p>
    * The specified button has got the action to close the tool panel added
    * and must be shown in the view.
    *
    * @param closeBt  the closing button
    */
   public void initToolComponent(JButton closeBt);

   /**
    * Gets the Component that represents the graphical view of the tool.
    * The Component is displayed in the main window when it is selected
    * in the menu.
    *
    * @return  the <code>Component</code>
    */
   public Component toolComponent();
   
   /**
    * Sets the <code>EditableDocument</code> that is currently viewed in the
    * editor
    *
    * @param edtDoc  the {@link EditableDocument}
    */
   public void setEditableDocument(EditableDocument edtDoc);
   
   /**
    * Ends the edit tool. Called when the program is exited
    */
   public void end();
}
