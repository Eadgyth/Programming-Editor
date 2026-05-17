package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

//--Eadgyth--/
import eg.CharsetChanger;

/**
 * The menu for actions to change the character encoding
 */
public class CharsetMenu {

  private static final String[] CHARSET_NAMES = {
      "windows-1250",
      "ISO-8859-2",
      "windows-1251",
      "ISO-8859-5",
      "windows-1252",
      "windows-1253",
      "ISO-8859-7",
      "windows-1254",
      "ISO-8859-9",
      "windows-1255",
      "ISO-8859-8",
      "windows-1256",
      "windows-1257",
      "ISO-8859-4",
      "ISO-8859-13",
      "windows-1258",
      "Shift_JIS",
      "MS932",
      "EUC-JP",
      "EUC-KR",
      "Big5",
      "GBK",
      "GB18030",
   };

   private final JMenu menu = new JMenu("Select fallback encoding ...");
   private final ButtonGroup group = new ButtonGroup();
   private final List<JRadioButtonMenuItem> items = new ArrayList<>();

   private final JRadioButtonMenuItem noneItm
         = new JRadioButtonMenuItem("None");

   public CharsetMenu() {
      init();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu menu() {
      return menu;
   }

   /**
    * Sets a <code>CharsetChanger</code> to the elements in the
    * list of items for actions to select the encoding
    *
    * @param cc  the {@link CharsetChanger}
    */
   public void setChangeCharsetActions(CharsetChanger cc) {
      for (JRadioButtonMenuItem item : items) {
         item.addActionListener(e -> changeCharset(e, cc));
      }
      noneItm.addActionListener(e -> cc.change(null));
   }

   //
   //--private--/
   //

   private void changeCharset(ActionEvent e, CharsetChanger cc) {
      String name = ((JRadioButtonMenuItem) e.getSource()).getActionCommand();
      cc.change(Charset.forName(name));
   }

   private void init() {
      group.add(noneItm);
      menu.add(noneItm);
      noneItm.setSelected(true);
      menu.addSeparator();
      for (String entry : CHARSET_NAMES) {
         if (Charset.isSupported(entry)) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(entry);
            item.setActionCommand(entry);
            group.add(item);
            menu.add(item);
            items.add(item);
         }
      }
   }
}