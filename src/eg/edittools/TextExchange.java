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
   private final static File BACK_UP = new File(System.getProperty("user.dir")
                                     + F_SEP + "exchangeContent.txt");
   
   private final FileDocument exchangeDoc;
   private final JTextPane exchangeArea;
   private final Preferences prefs = new Preferences();

   private FileDocument sourceDoc;
   private JTextPane sourceArea;
   private Languages lang = Languages.NORMAL_TEXT; 
   private String indentUnit = "";
   private boolean isReplace = false;
   private boolean isInsertSel = false;
   
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
   
   public void setReplace(boolean b) {
      isReplace = b;
   }
   
   public void setInsertSelection(boolean b) {
      isInsertSel = b;
   }
   
   public void setTextFromDoc() {
      String textToIns = sourceArea.getSelectedText();
      if (textToIns == null) {
         Dialogs.infoMessage("No text is selected", null);
         return;
      }
      exchangeDoc.requestFocus();
      int posToIns = 0;
      String textToReplace = null;
      if (isReplace) {
         exchangeDoc.remove(0, exchangeDoc.getDocLength());
      }
      else {
         textToReplace = exchangeArea.getSelectedText();
         posToIns = exchangeArea.getSelectionStart();
      }
      int posFin = posToIns;
      exchangeDoc.enableCodeEditing(false);
      if (textToReplace != null) {
         exchangeDoc.remove(posFin, textToReplace.length());
      }
      EventQueue.invokeLater(() -> {
         exchangeDoc.insert(posFin, textToIns);
         exchangeDoc.colorSection(textToIns, posFin);
         exchangeDoc.enableCodeEditing(true);
      });
   }
   
   public void replaceTextInDoc() {
      String text;
      if (isInsertSel) {
         text = exchangeArea.getSelectedText();
         if (text == null) {
            Dialogs.infoMessage("No text to insert is selected", null);
            return;
         }
      }
      else {
         text = exchangeDoc.getDocText();
         if (text.length() == 0) {
            return;
         }
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
   
   public void changeCodeEditing() {
      if (lang != sourceDoc.language()) {
         lang = sourceDoc.language();
         exchangeDoc.changeLanguage(lang);
      }
      if (!indentUnit.equals(sourceDoc.getIndentUnit())) {
         indentUnit = sourceDoc.getIndentUnit();
         exchangeDoc.setIndentUnit(indentUnit);
      }
   }
   
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.getDocLength());
   }
   
   public void save() {
      exchangeDoc.saveCopy(BACK_UP);
   }
}
