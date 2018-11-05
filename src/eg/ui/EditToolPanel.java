package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

//--Eadgyth--/
import eg.Constants;

/**
 * Defines the panel to which a variable <code>Component</code> can
 * be added. The component is added in the center of a JPanel with
 * a BorderLayout. The JPanel has a {@link Constants#GRAY_LINE_BORDER}.
 */
public class EditToolPanel {

   private final JPanel content = new JPanel(new BorderLayout());

   public EditToolPanel() {
      content.setBorder(Constants.GRAY_LINE_BORDER);
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
