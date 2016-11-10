import eg.plugin.*;

/**
 * A template for a plugin.
 * EditorAccess has a method addToFunctionPanel(Component c, String title)
 * which adds a component to the right Panel of the Editor's split window and
 * a method getTextDocument() which returns a reference to the TextDocument
 * object in the currently selected tab.
 */ 
public class TemplatePlugin implements Pluggable {
 
  private EditorAccess acc;
 
  @Override
  public void setEditorAccess(EditorAccess acc) {
     this.acc = acc;
  }
 
  @Override
  public void start() {
     acc.addToFunctionPanel(new javax.swing.JLabel("Hello World"), "TemplatePlugin");
  }
} 