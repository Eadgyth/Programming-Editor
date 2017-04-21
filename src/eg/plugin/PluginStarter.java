package eg.plugin;
 
import java.io.File;
import java.io.IOException;
import java.util.List;

//--Eadgyth--//
import eg.ui.FunctionPanel;
import eg.document.TextDocument;

/**
 * Class can start a plugin of type {@code Pluggable}
 */
public class PluginStarter {
   
   private final FunctionPanel functPnl;
   private final EditorAccess acc;
   private List<Pluggable> plugins = null;
   
   public PluginStarter(FunctionPanel functPnl) {
      this.functPnl = functPnl;
      acc = new EditorAccess(functPnl);
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
    * @param ithPlugin  the ith plugin from a list of plugins
    * @throws IOException  if a plugin could not be loaded
    */
   public void startPlugin(int ithPlugin) throws IOException {
      if (plugins == null ) {
         plugins = PluginLoader.loadPlugins(new File("./Plugins"));
      }
      plugins.get(ithPlugin).setEditorAccess(acc);
      plugins.get(ithPlugin).start();
   }
}
