package eg.document;

import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import java.awt.EventQueue;
import java.awt.Color;

//--Eadgyth--//
import eg.utils.Finder;
import javax.swing.text.BadLocationException;

/**
 * The editing of text during typing
 */
public class TypeText implements DocumentListener {

   private final UndoManager undomanager = new UndoManager();
   private final StyledDocument lineDoc;
   private final StyledDocument doc;  
   private final Element el;

   private final SimpleAttributeSet normalSet = new SimpleAttributeSet(); 
   private final SimpleAttributeSet comSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet keySet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet     = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet = new SimpleAttributeSet();

   private final TypeText.Coloring col = new TypeText.Coloring();
   private final AutoIndent autoInd;
   private final RowNumbers rowNum;

   private String[] keywords;
   private String lineCmnt;
   private boolean useLineCmnt = false;
   private String blockCmntStart;
   private String blockCmntEnd;
   private boolean useBlockCmnt = false;
   private boolean useStringLit = false;
   private boolean useIndent = false;
   private boolean constrainWord = false;
   /*
    * true to call modifying methods from this update methods */
   private boolean isDocListen = true; 
   /*
    * true to call text coloring and indentation from this update methods */
   private boolean isTextModify = false;

   public TypeText(EditArea editArea) {
      lineDoc = editArea.lineNumbers().getStyledDocument();
      doc = editArea.textArea().getStyledDocument();
      el = doc.getParagraphElement(0);
      setStyles();

      doc.addDocumentListener(this);

      doc.addUndoableEditListener((UndoableEditEvent e) -> {
         AbstractDocument.DefaultDocumentEvent event =
               (AbstractDocument.DefaultDocumentEvent)e.getEdit();
         if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
            return;
         }
         if (isDocListen) {
             undomanager.addEdit(e.getEdit());
         }
      });
      undomanager.setLimit(1000);

      rowNum = new RowNumbers(lineDoc, editArea.scrolledArea());
      autoInd = new AutoIndent(editArea.textArea(), doc, normalSet);
   }

   /**
    * Updates row numbers, synthax coloring and auto indentation
    * upon inserting text
    */ 
   @Override
   public void insertUpdate(DocumentEvent de) {
      if (isDocListen) {
         String in = getDocText();
         updateRowNumber(in);
         if (isTextModify) {
            int pos = de.getOffset();
            insertTextModify(de, in, pos);
         }
      }
   }

   /**
    * Updates row numbers, synthax coloring and auto indentation
    * upon removing text
    */
   @Override
   public void removeUpdate(DocumentEvent de) {
      if (isDocListen) {
         String in = getDocText();
         updateRowNumber(in);
         if (isTextModify) {
            int pos = de.getOffset();
            removeTextModify(de, in, pos);
         }
      }
   }

   @Override
   public void changedUpdate(DocumentEvent de) {
   }

   StyledDocument doc() {
      return doc;
   }

   SimpleAttributeSet normalSet() {
      return normalSet;
   }

   String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   void enableDocListen(boolean isDocListen) {
      this.isDocListen = isDocListen;
   }

   void enableTextModify(boolean isTextModify) {
      this.isTextModify = isTextModify;
   }

   void configTypeText(String[] keywords, String lineCmnt, String blockCmntStart,
            String blockCmntEnd, boolean useStringLit, boolean useIndent,
            boolean constrainWord) {
      this.keywords = keywords;
      this.lineCmnt = lineCmnt;
      useLineCmnt = lineCmnt.length() > 0;
      this.blockCmntStart = blockCmntStart;
      this.blockCmntEnd = blockCmntEnd;
      useBlockCmnt = blockCmntStart.length() > 0;
      this.useStringLit = useStringLit;
      this.useIndent = useIndent;
      if (!useIndent) {
         autoInd.resetIndent();
      }
      this.constrainWord = constrainWord;
   }

   String getDocText() {
      String in = null;
      try {
         in = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         e.printStackTrace();
      }
      return in;
   }

   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
   }

   void updateRowNumber(String in) {
      rowNum.updateRowNumber(in);
   }

   void backInBlack(int length, int pos) {
      doc.setCharacterAttributes(pos, length, normalSet, false);
   }

   void colorAll(boolean enableTextModify) {
      this.isTextModify = false;
      String all = getDocText();
      backInBlack(all.length(), 0);
      col.color(all, 0);
      this.isTextModify = enableTextModify;
   }  

   void colorAllNew() {
      String all = getDocText();
      col.color(all, 0);
   }

   void undo() {
      try {
         if (undomanager.canUndo()) {
            undomanager.undo();
            if (isTextModify) {
               enableTextModify(false);
               colorAll(true);
            }
         }
      }
      catch (CannotUndoException cue) {
         cue.printStackTrace();
      }
   }

   void redo() {
      try {
         if (undomanager.canRedo()) {
            undomanager.redo();
             if (isTextModify) {
               enableTextModify(false);
               colorAll(true);
            }
         }
      }
      catch (CannotRedoException cre) {
         cre.printStackTrace();
      }
   }

   //
   //--private
   //

   private void insertTextModify(DocumentEvent de, String in, int pos) {
      if (pos > 0 && useIndent) {
         autoInd.openBracketIndent(in, pos);
      }
      EventQueue.invokeLater(() -> {
         col.color(in, pos);
         if (useIndent) {
            autoInd.closeBracketIndent(in, pos); // must be invoked later
         }
      });
   }

   private void removeTextModify(DocumentEvent de, String in, int pos) {
      EventQueue.invokeLater( () -> {
         if (useBlockCmnt) {
            col.uncommentBlock(in, pos);
         }
         col.color(in, pos);
      });
   } 

   private void setStyles() {
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setLineSpacing(normalSet, 0.2f );
      StyleConstants.setBold(normalSet, false);
      doc.setParagraphAttributes(0, el.getEndOffset(), normalSet, false);

      Color commentGreen = new Color(60, 190, 80);
      StyleConstants.setForeground(comSet, commentGreen);
      StyleConstants.setBold(comSet, false);

      Color keyPink = new Color(230, 0, 110);
      StyleConstants.setForeground(keySet, keyPink);
      StyleConstants.setBold(keySet, false);

      Color bracketBlue = new Color(70, 0, 220);
      StyleConstants.setForeground(brSet, bracketBlue);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(255, 140, 0);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false );
   }

   /**
    * Synthax coloring
    */  
   private class Coloring {

      /**
       * Colors synthax
       * <p>
       * If called by the document listener's update methods, that is if
       * isTextModify is true, only the current line is modified. 
       * If called when textModify is false the entire passed in String is
       * modified. For block comments always the entire document is modified
       */
      void color(String in, int pos) {    
         String chunk;
         if (isTextModify) {
            chunk = Finder.currLine(in, pos);
            pos = in.lastIndexOf("\n", pos) + 1;
         }
         else {
            chunk = in;
         }

         // positions of previous block comment start and next block comment end
         int indBlockStart = Finder.indLastBlockStart(in, pos, blockCmntStart,
               blockCmntEnd);
         int indBlockEnd   = Finder.indNextBlockEnd(in, pos, blockCmntStart,
               blockCmntEnd);

         // if cursor is not inside a block comment or using block comments 
         // is disbled
         if ((indBlockStart == -1 || indBlockEnd == -1) || !useBlockCmnt) {

            // first set back to black
            backInBlack(chunk.length(), pos);

            // keywords
            for (int i = 0; i < keywords.length; i++) {
               keys(chunk, keywords[i], keySet, pos);
            }

            // brackets
            for (int i = 0; i < Keywords.BRACKETS.length; i++) {
               brackets(chunk, Keywords.BRACKETS[i], pos);
            }

            // String literals:
            // if the entire document is to be scanned text is plitted in
            // lines because string literals cannot span several lines.
            if (useStringLit) {
               if (!isTextModify) {
                  if (chunk.replaceAll("\n", "").length() > 0) {
                     String[] chunkArr = chunk.split("\n");
                     int[] startOfLines = Finder.startOfLines(chunkArr);
                     for (int i = 0; i < chunkArr.length; i++) {
                        stringLiterals(chunkArr[i], startOfLines[i] + pos);
                     }
                  }
               }
               //
               // if only a line is scanned
               else {
                  stringLiterals(chunk, pos);
               }
            }

            // line comments
            if (useLineCmnt) {
               lineComments(chunk, pos);
            }
         }

         // always the entire document  
         if (useBlockCmnt) {               
            blockComments(in);       
         }       
      }

      void keys(String in, String query, SimpleAttributeSet set, int pos) {
         int index = 0;
         int nextPos = 0;
         while (index != -1) {
            index = in.indexOf(query, index + nextPos);
            if (index != -1) {
               boolean ok = !constrainWord || Finder.isWord(in, query, index);
               if (ok) {
                  doc.setCharacterAttributes(index + pos, query.length(),
                        set, false);
               }
            }  
            nextPos = 1; 
         }
      }

      private void brackets(String in, String query, int pos) {
         int index = 0;
         int nextPos = 0;

         while (index != -1) {
            index = in.indexOf(query, index + nextPos);
            if (index != -1) {
               doc.setCharacterAttributes(index + pos, 1, brSet, false);
            }
            nextPos = 1;
         }
      }

      private void stringLiterals(String in, int pos) {
         int indStart = 0;
         int indEnd = 0;
         int nextPos = 1;
         while ( indStart != -1 && indEnd != -1 ) {
            indStart = in.indexOf( "\"", indEnd + nextPos );
            if ( indStart != -1 ) {
               indEnd = in.indexOf( "\"", indStart + 1 );
               if ( indEnd != -1 ) {
                  int length = indEnd - indStart;
                  doc.setCharacterAttributes( indStart + pos, length + 1,
                        strLitSet, false );
               }    
            }
            nextPos = 2;
         }
      }

      private void lineComments(String in, int pos) {
         int lineComInd = 0;
         int nextPos = 0;
         while (lineComInd != -1) {
            lineComInd = in.indexOf(lineCmnt, lineComInd + nextPos );
            if (lineComInd != -1 && !Finder.isInQuotes( in, lineComInd)) {
               int lineEndInd = in.indexOf("\n", lineComInd + 1);
               int length;
               if (lineEndInd != -1) {
                  length = lineEndInd - lineComInd;
               }
               else {
                  length = in.length() - lineComInd;
               }
               doc.setCharacterAttributes(lineComInd + pos, length,
                     comSet, false);
            }
            nextPos = 1;
         }
      }

      private void blockComments(String in) {
         int indStart = 0;
         int nextPos = 0;

         while (indStart != -1) {
            indStart = in.indexOf(blockCmntStart, indStart + nextPos);
            if (indStart != -1 && !Finder.isInQuotes(in, indStart)) {       
               int indEnd = in.indexOf(blockCmntEnd, indStart + 1);
               if (indEnd != -1 && !Finder.isInQuotes(in, indEnd)) {
                  int indNextStart = in.substring
                        (indStart + 1, indEnd).indexOf(blockCmntStart, 0);

                  if (indNextStart == -1) {
                     int length = indEnd - indStart + blockCmntEnd.length();
                     doc.setCharacterAttributes(indStart, length, comSet, false);
                     //
                     // maybe we want to outcomment a part of an existing block
                     if (isTextModify) {
                        uncommentBlock(in, indEnd + 2);
                        uncommentBlock(in, indStart - 2);
                     }
                  }
               }
            }         
            nextPos = 1;
         }
      }

      private void uncommentBlock(String in, int pos) {

         // positions of previous block comment start and next block comment end
         int indBlockStart = Finder.indLastBlockStart(in, pos, blockCmntStart,
               blockCmntEnd);
         int indBlockEnd   = Finder.indNextBlockEnd(in, pos, blockCmntStart,
               blockCmntEnd);

         if (indBlockStart != -1 && indBlockEnd == -1) {
            String toUncomment = in.substring(indBlockStart, pos);
            enableTextModify(false);
            color(toUncomment, indBlockStart);
            enableTextModify(true);
         }
         else if (indBlockEnd != -1 && indBlockStart == -1) {
            String toUncomment = in.substring(pos, indBlockEnd + blockCmntEnd.length());
            enableTextModify(false);
            color(toUncomment, pos);
            enableTextModify(true);
         }
      }
   } //class Coloring
}//class Text