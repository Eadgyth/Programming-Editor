package eg.ui.filetree;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JToolBar;

import javax.swing.border.LineBorder;

import javax.swing.filechooser.FileSystemView;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import java.io.File;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.FunctionalAction;
import eg.utils.ScreenParams;
import eg.ui.UIComponents;

/**
 * Defines the panel which contains a tool bar and panel for adding
 * a JTree
 */
public class TreePanel {

   private final BackgroundTheme theme = BackgroundTheme.givenTheme();
   private final JPanel content;
   private final JPanel holdTree = new JPanel(new BorderLayout());
   private final JScrollPane scroll = UIComponents.scrollPane();
   private final JButton closeBt = UIComponents.undecoratedButton();

   public TreePanel() {
      content = UIComponents.grayBorderedPanel();
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
    * Sets the specified <code>JTree</code>
    *
    * @param tree  the JTree
    */
   public void setTree(JTree tree) {
      tree.setRootVisible(true);
      tree.setCellRenderer(new TreeRenderer());
      tree.setToggleClickCount(0);
      tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);

      tree.setFocusable(false);
      tree.setBackground(theme.background());
      holdTree.add(tree);
      holdTree.revalidate();
   }

   /**
    * Sets the action for closing the this panel to this closing button
    *
    * @param act  the closing action
    */
   public void setClosingAct(FunctionalAction act) {
      closeBt.setAction(act);
   }

   //
   //--private--/
   //

   private void init() {
      content.setLayout(new BorderLayout());
      content.add(toolbar(), BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      holdTree.setBackground(theme.background());
      holdTree.setBorder(new LineBorder(theme.background(), 5));
      scroll.setViewportView(holdTree);
   }

   private JToolBar toolbar() {
      String[] tooltips = new String[] {
         "Folder up"
      };
      return UIComponents.toolBar(null, tooltips, closeBt);
   }

   @SuppressWarnings("serial")
   private class TreeRenderer extends DefaultTreeCellRenderer {

	  private final transient FileSystemView fsv = FileSystemView.getFileSystemView();

      @Override
      public Color getBackgroundNonSelectionColor() {
         return theme.background();
      }

      @Override
      public Color getBackgroundSelectionColor() {
         return theme.selectionBackground();
      }

      @Override
      public Color getBackground() {
         return theme.background();
      }

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
              row, hasFocus);

        setForeground(theme.normalText());
        if (value instanceof DefaultMutableTreeNode) {
           value = ((DefaultMutableTreeNode) value).getUserObject();
           if (value instanceof File) {
              File f = (File) value;
              setIcon(fsv.getSystemIcon(f));
              setFont(ScreenParams.scaledFontToPlain(getFont(), 8));
              setText(f.getName());
           }
        }
        return this;
      }
   }
}
