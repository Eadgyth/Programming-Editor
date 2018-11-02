package eg.ui.filetree;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;

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
import javax.swing.tree.TreeSelectionModel;

import java.io.File;

//--Eadgyth--/
import eg.Constants;
import eg.ui.IconFiles;
import eg.utils.UIComponents;

/**
 * Defines the panel which contains a tool bar and panel for adding
 * a JTree
 */
public class TreePanel {

   private final JPanel content = new JPanel(new BorderLayout());
   private final JPanel holdTree = new JPanel(new BorderLayout());
   private final JScrollPane scroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   private final JButton upBt = new JButton(UIManager.getIcon(
         "FileChooser.upFolderIcon"));

   private final JButton renewBt = new JButton(IconFiles.REFRESH_ICON);
   private final JButton closeBt = new JButton(IconFiles.CLOSE_ICON);

   public TreePanel() {
      init();
   }

   /**
    * Gets this JPanel which contains the toolbar and the panel for
    * adding the tree
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
      tree.setRootVisible(true);
      tree.setBorder(new LineBorder(Color.WHITE, 5));
      tree.setCellRenderer(new TreeRenderer());
      tree.setToggleClickCount(0);
      tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);

      tree.setFocusable(false);
      renewBt.setEnabled(true);
      holdTree.add(tree);
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
    * Enables or disables the button for folder up actions
    *
    * @param b  true to enable, false to disable
    */
   public void enableFolderUpAct(boolean b) {
      upBt.setEnabled(b);
   }

   //
   //--private--/
   //

   private void init() {
      scroll.setBorder(Constants.MATTE_TOP_LIGHT_GRAY);
      scroll.setViewportView(holdTree);
      scroll.getVerticalScrollBar().setUnitIncrement(10);
      content.add(toolbar(), BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      content.setBorder(Constants.GRAY_LINE_BORDER);
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
      return UIComponents.toolBar(bts, tooltips);
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
