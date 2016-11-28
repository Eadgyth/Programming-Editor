import eg.plugin.*;

/**
 * A template for a plugin
 */ 
public class TemplatePlugin implements Pluggable {
 
  private EditorAccess acc;
  private javax.swing.JLabel exampleComponent;
  
  @Override
  public void setEditorAccess(EditorAccess acc) {
     this.acc = acc;
  }
 
  @Override
  public void start() {
     if (exampleComponent == null) {
        exampleComponent = new javax.swing.JLabel("Hello, World");
     }
     acc.addToFunctionPanel(exampleComponent, "TemplatePlugin");
  }
} 