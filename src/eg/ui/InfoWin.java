package eg.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * The window that shows info about Eadgyth
 */
public class InfoWin {

   private final static String INFO
         = "<html>"
         + "Version: prepare for 1.1.4<br>"
         + "Malte Bussiek<br>"
         + "https://eadgyth.github.io/Programming-Editor/"
         + "</html>";

   private final static Border EMPTY_BORDER = new EmptyBorder(10, 10, 10, 10);

   private final JFrame  frame    = new JFrame();
   private final JPanel  textPnl  = new JPanel();
   private final JPanel  holdTextPnl = UIComponents.grayBorderedPanel();
   private final JPanel  combine  = new JPanel(new BorderLayout());
   private final JLabel  titleLb  = new JLabel("Eadgyth Programming Editor");
   private final JLabel  infoLb   = new JLabel(INFO);
   private final JPanel  okButton = new JPanel(new FlowLayout());
   private final JButton okBt     = new JButton("OK");

   public InfoWin() {
      titleLb.setFont(ScreenParams.SANSSERIF_BOLD_11);
      infoLb.setFont(ScreenParams.SANSSERIF_PLAIN_9);
      okBt.setFocusPainted(false);
      okBt.addActionListener(e -> frame.setVisible(false));
      okButton.add(okBt);
      textPnl.setLayout(new GridLayout(2, 1));
      textPnl.setBackground(Color.white);
      textPnl.add(titleLb);
      textPnl.add(infoLb);
      holdTextPnl.add(textPnl);
      holdTextPnl.setBackground(Color.white);
      combine.setBorder(EMPTY_BORDER);
      combine.add(holdTextPnl, BorderLayout.CENTER);
      combine.add(okButton, BorderLayout.SOUTH);

      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(650, 100);
      frame.setTitle("About");
      frame.setContentPane(combine);
      frame.setResizable(false);
      frame.setSize(eg.utils.ScreenParams.scaledDimension(250, 150));
      frame.setVisible(true);
   }
}
