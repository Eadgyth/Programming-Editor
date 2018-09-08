package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.EditableDocument;

/**
 * The interface that defines an "edit tool" whose graphical component can
 * be added to the <code>EditToolPanel</code> in the main window
 * @see eg.ui.EditToolPanel
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
    * Gets the <code>Component</code> that represents the graphical view
    * of the tool
    *
    * @return  the <code>Component</code>
    */
   public Component toolContent();
   
   /**
    * Sets the <code>EditableDocument</code> that is currently viewed in
    * the editor
    *
    * @param edtDoc  the {@link EditableDocument}
    */
   public void setEditableDocument(EditableDocument edtDoc);
   
   /**
    * Ends the edit tool. Called when the program is exited
    */
   public void end();
}
