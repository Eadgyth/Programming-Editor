package eg.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

//--Eadgyth--/
import eg.Constants;

/**
 * The window that shows info about Eadgyth
 */
public class InfoWin {

   private final static String INFO
         = "<html>"
         + "Version since 1.0.6.2 to test<br>"
         + "Malte Bussiek<br>"
         + "https://eadgyth.github.io/Programming-Editor"
         + "</html>";

   private final JFrame  frame    = new JFrame();
   private final JPanel  okButton = new JPanel(new FlowLayout());
   private final JPanel  text     = new JPanel(new GridLayout(2, 1));
   private final JPanel  combine  = new JPanel(new BorderLayout());
   private final JButton okBt     = new JButton("OK");
   private final JLabel  titleLb  = new JLabel("Eadgyth Programming-Editor");
   private final JLabel  infoLb   = new JLabel(INFO);

   public InfoWin() {  
      titleLb.setFont(Constants.SANSSERIF_BOLD_11);
      infoLb.setFont(Constants.SANSSERIF_PLAIN_9);
      okBt.setFocusPainted(false);
      okBt.addActionListener(e -> frame.setVisible(false));  

      okButton.add(okBt);
      
      text.setBackground(Color.white);
      text.setBorder(eg.Constants.EMPTY_BORDER_5);
      text.add(titleLb);
      text.add(infoLb);

      combine.setBorder(Constants.EMPTY_BORDER_5);
      combine.add(text, BorderLayout.CENTER);
      combine.add(okButton, BorderLayout.SOUTH);
            
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(650, 100);
      frame.setTitle("About");
      frame.setContentPane(combine);
      frame.setResizable(false);
      frame.setSize(eg.utils.ScreenParams.scaledDimension(300, 150));
      frame.setVisible(true);
   }
}
