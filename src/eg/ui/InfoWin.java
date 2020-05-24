package eg.ui;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.Desktop;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * The window that shows info about Eadgyth
 */
public class InfoWin {

   private static final String INFO
         = "<html>"
         + "Programming Editor<br><br>"
         + "Version: 1.1.5-pre<br>"
         + "Written by Malte Bussiek<br>"
         + "Contributions: William Gilreath<br><br>"
         + "</html>";

   private static final String LINK
         = "https://eadgyth.github.io/Programming-Editor/";

   private static final Border EMPTY_BORDER = new EmptyBorder(10, 10, 10, 10);

   private final JFrame  frame    = new JFrame();
   private final JButton okBt     = new JButton("OK");
   private final JButton linkBt   = UIComponents.undecoratedButton();

   public InfoWin() {
      okBt.setFocusPainted(false);
      okBt.addActionListener(e -> frame.setVisible(false));
      JPanel btPnl = new JPanel(new FlowLayout());
      btPnl.add(okBt);
      initLinkBt();

      JPanel  content  = new JPanel(new BorderLayout());
      content.setBorder(EMPTY_BORDER);
      content.add(textPnl(), BorderLayout.CENTER);
      content.add(btPnl, BorderLayout.SOUTH);

      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(650, 100);
      frame.setTitle("About");
      frame.setContentPane(content);
      frame.setResizable(false);
      frame.setSize(eg.utils.ScreenParams.scaledDimension(250, 200));
      frame.setVisible(true);
   }

   private JPanel textPnl() {
      JPanel pnl = UIComponents.grayBorderedPanel();
      JPanel textPnl = new JPanel();
      JLabel  titleLb  = new JLabel("Eadgyth");
      titleLb.setFont(ScreenParams.SANSSERIF_BOLD_11);
      JLabel  infoLb   = new JLabel(INFO);
      infoLb.setFont(ScreenParams.SANSSERIF_PLAIN_9);
      textPnl.setLayout(new BorderLayout());
      textPnl.setBackground(Color.white);
      textPnl.add(titleLb, BorderLayout.NORTH);
      textPnl.add(infoLb, BorderLayout.CENTER);
      textPnl.add(linkBt, BorderLayout.SOUTH);
      pnl.add(textPnl);
      pnl.setBackground(Color.white);
      return pnl;
   }

   private void initLinkBt() {
      linkBt.setText("<html><u>" + LINK + "</u></html>");
      linkBt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
      linkBt.setFont(ScreenParams.SANSSERIF_PLAIN_9);
      linkBt.setForeground(Color.BLUE);
      linkBt.addActionListener(e -> openWebSite());
   }

   private void openWebSite() {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(LINK));
         }
      }
      catch (IOException | URISyntaxException e) {
         eg.utils.FileUtils.log(e);
      }
   }
}
