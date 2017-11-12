package eg.edittools;

import java.io.File;
import java.awt.EventQueue;
import javax.swing.JTextPane;

//--Eadgyth--/
import eg.Preferences;
import eg.Languages;
import eg.document.FileDocument;
import eg.utils.Dialogs;

public class TextExchange {
   
   private final static String F_SEP = File.separator;
   private final static File BACK_UP
         = new File(System.getProperty("user.dir")
         + F_SEP + "exchangeContent.txt");
   
   private final FileDocument exchangeDoc;
   private final JTextPane exchangeArea;
   private final Preferences prefs = new Preferences();
   private FileDocument sourceDoc;
   private JTextPane sourceArea;
   private Languages lang = Languages.NORMAL_TEXT; 
   private String indentUnit = "";
   
   public TextExchange(FileDocument exchangeDoc) {
      this.exchangeDoc = exchangeDoc;
      this.exchangeArea = exchangeDoc.docTextArea();
      prefs.readPrefs();
      exchangeDoc.setIndentUnit(prefs.getProperty("indentUnit"));
      if (BACK_UP.exists()) {
         exchangeDoc.displayFileContent(BACK_UP);
      }
   }
   
   public void setSourceDocument(FileDocument sourceDoc) {
      this.sourceDoc  = sourceDoc;
      this.sourceArea = sourceDoc.docTextArea();
   }
   
   public void setTextFromDoc() {
      String textToIns = sourceArea.getSelectedText();
      if (textToIns == null) {
         String filename = "unnamed";
         if (sourceDoc.hasFile()) {
            filename = sourceDoc.filename();
         }
         Dialogs.warnMessage("No text is selected in " + filename);
         return;
      }
      exchangeDoc.requestFocus();
      String textToReplace = exchangeArea.getSelectedText();
      int posToIns = exchangeArea.getSelectionStart();
      exchangeDoc.enableCodeEditing(false);
      if (textToReplace != null) {
         exchangeDoc.remove(posToIns, textToReplace.length());
      }
      EventQueue.invokeLater(() -> {
         exchangeDoc.insert(posToIns, textToIns);
         exchangeDoc.colorSection(textToIns, posToIns);
         exchangeDoc.enableCodeEditing(true);
      });
   }
   
   public void replaceTextInDoc() {
      String text;
      text = exchangeArea.getSelectedText();
      if (text == null) {
         Dialogs.warnMessage("No text is selected in the exchange editor");
         return;
      }
      sourceDoc.requestFocus();
      String textFin = text;
      String sel = sourceArea.getSelectedText();
      int pos = sourceArea.getSelectionStart();
      sourceDoc.enableCodeEditing(false);
      if (sel != null) {
         sourceDoc.remove(pos, sel.length());
      }
      EventQueue.invokeLater(() -> {
         sourceDoc.insert(pos, textFin);
         sourceDoc.colorSection(textFin, pos);
         sourceDoc.enableCodeEditing(true);
      });
   }
   
   public void changeCodeEditing(Languages lang) {
      exchangeDoc.changeLanguage(lang);
   }
   
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.getDocLength());
   }
   
   public void save() {
      exchangeDoc.saveCopy(BACK_UP);
   }
}
