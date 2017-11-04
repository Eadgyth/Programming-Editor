package eg;

import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class FunctionalAction extends AbstractAction {
      
   ActionListener al;
   
   public FunctionalAction(String name, Icon icon, ActionListener al) {

      super(name, icon);
      this.al = al;
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      al.actionPerformed(e);
   }
} 
