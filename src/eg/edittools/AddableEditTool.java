package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.EditableDocument;

/**
 * The interface that defines an "edit tool" whose graphical component can be
 * added to the <code>ToolPanel</code> in the main window
 * @see eg.ui.ToolPanel
 */
public interface AddableEditTool {
   
   /**
    * Adds the action to close the <code>ToolPanel</code> of the main window.
    * <p>
    * The specified button has got the closing action added and must be
    * shown in the graphical view.
    *
    * @param closeBt  the closing button
    */
   public void addClosingAction(JButton closeBt);

   /**
    * Gets the <code>Component</code> that represents the graphical view of
    * the tool. Called when the <code>AddableEditTool</code> is selected
    * in the "Edit" menu
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
