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

   JFrame infoWin = new JFrame("About Eadgyth");
   JPanel okButton = new JPanel(new FlowLayout());
   JPanel text = new JPanel(new GridLayout(4,1));
   JPanel combine = new JPanel(new BorderLayout());

   JButton closeInfo = new JButton("OK");
   JLabel title = new JLabel("Eadgyth");
   Font font = new Font("Verdana", Font.BOLD, 15);
   JLabel version = new JLabel("Version 1.0 Beta");
   JLabel author = new JLabel("Malte Bussiek, m.bussiek@web.de");

   public InfoWin() {  
      title.setFont(font);
      closeInfo.setFocusPainted(false);

      okButton.add(closeInfo);
      
      text.setBackground(Color.white);
      text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      text.add(title);
      text.add(version);
      text.add(author);

      combine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      combine.add(text, BorderLayout.CENTER);
      combine.add(okButton, BorderLayout.SOUTH);
            
      infoWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      infoWin.pack();
      infoWin.setResizable(false);
      infoWin.setLocation(650, 100);
      infoWin.setSize(300,200);
      infoWin.setContentPane(combine);
      infoWin.setIconImage(IconFiles.eadgythIcon.getImage());
      infoWin.setVisible(true);

      closeInfo.addActionListener(e -> infoWin.setVisible(false));  
   }
}
