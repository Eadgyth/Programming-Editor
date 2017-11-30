package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

//--Eadgyth--/
import eg.Constants;

/**
 * Defines a panel to which a variable <code>Component</code> can
 * be added
 */
public class ToolPanel {
   
   private final JPanel pnl = new JPanel(new BorderLayout());
   
   public ToolPanel() {
      pnl.setBorder(Constants.GRAY_BORDER);
   }
   
   /**
    * Gets this tool panel
    *
    * @return  the <code>JPanel</code>
    */
   public JPanel panel() {
      return pnl;
   }
   
   /**
    * Adds a component to this tool panel. A previously added
    * component is removed
    *
    * @param c  the <code>Component</code>
    */
   public void addComponent(Component c) {
      BorderLayout layout = (BorderLayout) pnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null) {
         pnl.remove(cCenter);
      }
      if (c != null) {
         pnl.add(c, BorderLayout.CENTER);
      }
      pnl.revalidate();
      pnl.repaint();
   }
}
   
