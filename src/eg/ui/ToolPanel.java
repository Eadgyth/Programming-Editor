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
   public JPanel toolPanel() {
      return pnl;
   }
   
   /**
    * Adds a component to this tool panel. A previously added
    * component is removed
    *
    * @param c  the <code>Component</code>
    */
   public void addComponent(Component c) {
      if (c == null) {
         throw new IllegalArgumentException("The parameter c is null");
      }
      BorderLayout layout = (BorderLayout) pnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null && cCenter != c) {
         pnl.remove(cCenter);
      }
      if (cCenter != c) {
         pnl.add(c, BorderLayout.CENTER);
         pnl.revalidate();
         pnl.repaint();
      }
   }
}
   
