package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

/**
 * Defines the panel to which a variable <code>Component</code> can
 * be added. The component is added in the center of a JPanel with
 * a <code>BorderLayout</code>.
 */
public class EditToolPanel {

   private final JPanel content;

   public EditToolPanel() {
      content = UIComponents.grayBorderedPanel();
      content.setLayout(new BorderLayout());
   }

   /**
    * Gets this <code>JPanel</code> which a Component can be added to
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Adds a component. A previously added component is replaced
    *
    * @param c  the Component
    */
   public void addComponent(Component c) {
      if (c == null) {
         throw new IllegalArgumentException("c is null");
      }
      BorderLayout layout = (BorderLayout) content.getLayout();
      Component cPrev = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cPrev == c) {
         return;
      }
      if (cPrev != null) {
         content.remove(cPrev);
      }
      content.add(c, BorderLayout.CENTER);
      content.revalidate();
      content.repaint();
   }
}
