package eg.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;


/**
 * A frame that shows info about Eadgyth
 */
public class InfoWin {

   private final static Font TITLE_FONT = new Font("Verdana", Font.BOLD, 14);
   private final static String INFO
         = "<html>"
         + "Version 1.0 beta<br>"
         + "Malte Bussiek, m.bussiek@web.de"
         + "</html>";

   private final JFrame frame  = new JFrame("About Eadgyth");
   private final JPanel okButton = new JPanel(new FlowLayout());
   private final JPanel text     = new JPanel(new GridLayout(2, 1));
   private final JPanel combine  = new JPanel(new BorderLayout());
   private final JButton closeBt = new JButton("OK");
   private final JLabel titleLb  = new JLabel("Eadgyth");
   private final JLabel infoLb   = new JLabel(INFO);

   public InfoWin() {  
      titleLb.setFont(TITLE_FONT);
      closeBt.setFocusPainted(false);

      okButton.add(closeBt);
      
      text.setBackground(Color.white);
      text.setBorder(eg.Constants.EMPTY_BORDER);
      text.add(titleLb);
      text.add(infoLb);

      combine.setBorder(eg.Constants.EMPTY_BORDER);
      combine.add(text, BorderLayout.CENTER);
      combine.add(okButton, BorderLayout.SOUTH);
            
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setResizable(false);
      frame.setLocation(650, 100);
      frame.setSize(300, 150);
      frame.setContentPane(combine);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
      frame.setVisible(true);

      closeBt.addActionListener(e -> frame.setVisible(false));  
   }
}
