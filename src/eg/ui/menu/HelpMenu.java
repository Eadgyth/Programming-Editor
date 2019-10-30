package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.Desktop;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The menu for help actions
 */
class HelpMenu {

   private final JMenu     menu        = new JMenu("?");
   private final JMenuItem showHelpItm = new JMenuItem("Open help site");
   private final JMenuItem aboutItm    = new JMenuItem("About");

   public HelpMenu() {
      menu.add(showHelpItm);
      menu.add(aboutItm);
      aboutItm.addActionListener(e -> new eg.ui.InfoWin());
      showHelpItm.addActionListener(e -> showHelpSite());
   }

   public JMenu getMenu() {
      return menu;
   }

   private void showHelpSite() {
      openWebSite("https://eadgyth.github.io/Programming-Editor/docs/help/help.html");
   }

   private void openWebSite(String url) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
         }
      }
      catch (IOException | URISyntaxException e) {
         eg.utils.FileUtils.log(e);
      }
   }
}
