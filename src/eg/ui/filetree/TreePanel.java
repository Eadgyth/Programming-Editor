package eg.ui.filetree;

import java.awt.event.ActionListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import javax.swing.border.LineBorder;

import javax.swing.filechooser.FileSystemView;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.io.File;

//--Eadgyth--/
import eg.Constants;
import eg.ui.IconFiles;
import eg.utils.UIComponents;

/**
 * Defines the panel which contains another JPanel which a JTree for
 * a file system can be added to and a toolbar.
 */
public class TreePanel {

   private final JPanel content         = new JPanel(new BorderLayout());
   private final JPanel holdTreePnl = new JPanel(new BorderLayout());
   private final JScrollPane scroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   private final JButton upBt = new JButton(UIManager.getIcon(
         "FileChooser.upFolderIcon"));

   private final JButton renewBt = new JButton(IconFiles.REFRESH_ICON);
   private final JButton closeBt = new JButton(IconFiles.CLOSE_ICON);

   public TreePanel() {
      initTreePanel();
   }

   /**
    * Gets this JPanel which holds the tree and the toolbar
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Adds the specified <code>JTree</code>
    *
    * @param tree  the JTree
    */
   public void setTree(JTree tree) {
      holdTreePnl.add(tree);
      tree.setRootVisible(true);
      //tree.setFont(Constants.VERDANA_PLAIN_8);
      tree.setBorder(new LineBorder(Color.WHITE, 5));
      tree.setCellRenderer(new TreeRenderer());
      tree.setToggleClickCount(0);
      renewBt.setEnabled(true);
   }

   /**
    * Sets the listener for folder up actions
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setFolderUpAction(ActionListener al) {
      upBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to renew the tree
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setRenewTreeAction(ActionListener al) {
      renewBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to close the file view panel
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setCloseAct(ActionListener al) {
      closeBt.addActionListener(al);
   }

   /**
    * Sets the boolean that specifies if the button for folder
    * up actions is enabled (true) or disabled
    *
    * @param b  the boolen value
    */
   public void enableFolderUpAct(boolean b) {
      upBt.setEnabled(b);
   }

   //
   //--private--/
   //

   private void initTreePanel() {
      scroll.setBorder(Constants.MATTE_TOP);
      scroll.setViewportView(holdTreePnl);
      scroll.getVerticalScrollBar().setUnitIncrement(10);
      content.add(toolbar(), BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      content.setBorder(Constants.GRAY_BORDER);
      renewBt.setEnabled(false);
      enableFolderUpAct(false);
   }

   private JToolBar toolbar() {
      JButton[] bts = new JButton[] {
         upBt, renewBt, closeBt
      };
      String[] tooltips = new String[] {
         "Folder up",
         "Update tree",
         "Close the project explorer",
      };
      return UIComponents.lastBtRightToolbar(bts, tooltips);
   }

   private class TreeRenderer extends DefaultTreeCellRenderer {

      private final FileSystemView fsv = FileSystemView.getFileSystemView();

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
              row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
           value = ((DefaultMutableTreeNode) value).getUserObject();
           if (value instanceof File) {
              File f = (File) value;
              setIcon(fsv.getSystemIcon(f));
              setText(f.getName());
           }
        }
        return this;
      }
   }
}
