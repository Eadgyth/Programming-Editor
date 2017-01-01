package eg.plugin;
 
import java.io.File;
import java.io.IOException;
import java.util.List;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.document.TextDocument;

/**
 * Class can start a plugin of type {@code Pluggable}
 */
public class PluginStarter {
   
   private final MainWin mw;
   private final EditorAccess acc;
   private List<Pluggable> plugins = null;
   
   public PluginStarter(MainWin mw) {
      this.mw = mw;
      acc = new EditorAccess(mw);
   }
   
   /**
    * Sets in this EditorAcces the reference to an object of
    * {@code TextDocument}
    * @param txtDoc  the {@link TextDocument} in the selected tab
    */
   public void setTextDocument(TextDocument txtDoc) {
      acc.setTextDocument(txtDoc);
   }
 
   /**
    * Starts the ith plugin of listed plugins
    * @param iPlugin  the ith plugin from a list of plugins
    * @throws java.io.IOException  if a plugin could not be loaded
    */
   public void startPlugin(int iPlugin) throws IOException {
      if (plugins == null ) {
         plugins = PluginLoader.loadPlugins(new File("./Plugins"));
      }
      plugins.get(iPlugin).setEditorAccess(acc);
      plugins.get(iPlugin).start();
   }
}