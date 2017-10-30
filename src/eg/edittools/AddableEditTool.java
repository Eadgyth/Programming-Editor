package eg.edittools;

import eg.document.FileDocument;
import eg.ui.ToolPanel;

/**
 * The interface to to add an edit tool that is shown in the
 * <code>ToolPanel</code> in the main window and that has access
 * to the currently open or selected <code>FileDocument</code>
 */
public interface AddableEditTool {
   
   /**
    * Sets the <code>FileDocument</code> that is edited
    *
    * @param fDoc  the {@link FileDocument}
    */
   public void setFileDocument(FileDocument fDoc);

   /**
    * Adds this component to the tool panel. Must call
    * {@link ToolPanel#addComponent(Component,String)}
    *
    * @param toolPnl  the reference to {@link ToolPanel}
    */
   public void addComponent(ToolPanel toolPnl);
}
