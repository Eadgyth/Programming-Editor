package eg.edittools;

import java.awt.Component;

import javax.swing.JButton;

//--Eadgyth--/
import eg.document.EditableDocument;

/**
 * The interface that defines an "edit tool" whose graphical view can
 * be added to the <code>EditToolPanel</code>. This  panel is positioned
 * on the right of the splite pane in the main window. The graphical view
 * of the edit tool is represented by a <code>Component</code> object.
 *
 * @see eg.ui.EditToolPanel
 */
public interface AddableEditTool {

   /**
    * Adds the button for closing the <code>EditToolPanel</code>
    *
    * @param closeBt  the reference to the button that closes
    * {@link eg.ui.EditToolPanel} in the main window
    */
   public void addClosingButton(JButton closeBt);

   /**
    * Returns the current width of this <code>Component</code>. May define
    * an initial width that is used when the component is added the first
    * time.
    *
    * @return  the width
    */
   public int width();

   /**
    * Returns if the width of this <code>Component</code> is resized when
    * the width of main window is resized.
    *
    * @return  true to resize, false to maintain the width
    */
   public boolean resize();

   /**
    * Gets this <code>Component</code>
    *
    * @return  the Component
    */
   public Component content();

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
