package eg.ui;

import java.io.File;
import javax.swing.ImageIcon;

/**
 * Static ImageIcons for buttons etc. Next to a few home-made icons
 * the majority is from the Tango Desctop Project; see
 * <a href="http://tango.freedesktop.org/Tango_Icon_Library">Tango Project</a> 
 */
public class IconFiles {

   private final static String FILE_SEP = File.separator;
    
   private final static String PROGRAM_DIR
         = System.getProperty("user.dir");
   private final static String TANGO_DIR
         = PROGRAM_DIR + FILE_SEP + "Resources"
         + FILE_SEP + "Tango" + FILE_SEP;
   private final static String EAD_DIR
         = PROGRAM_DIR + FILE_SEP + "Resources"
         + FILE_SEP + "EadIcons" + FILE_SEP;

   // Tango icons
   public final static ImageIcon saveIcon
         = new ImageIcon(TANGO_DIR + "document-save.png");
   public final static ImageIcon openIcon
         = new ImageIcon(TANGO_DIR + "document-open.png");
   public final static ImageIcon closeIcon
         = new ImageIcon(TANGO_DIR + "document-close.png");
   public final static ImageIcon undoIcon
         = new ImageIcon(TANGO_DIR + "edit-undo.png" );
   public final static ImageIcon redoIcon
         = new ImageIcon(TANGO_DIR + "edit-redo.png" );
   public final static ImageIcon indentIcon
         = new ImageIcon(TANGO_DIR + "format-indent-more.png");
   public final static ImageIcon outdentIcon
         = new ImageIcon(TANGO_DIR + "format-indent-less.png");
   public final static ImageIcon stopProcessIcon
         = new ImageIcon(TANGO_DIR + "process-stop.png");
   public final static ImageIcon clearIcon
         = new ImageIcon(TANGO_DIR + "edit-clear.png");
   public final static ImageIcon refreshIcon
         = new ImageIcon(TANGO_DIR + "view-refresh.png");
   public final static ImageIcon newFolderIcon
         = new ImageIcon(TANGO_DIR + "folder-new.png");
         
   // Eadgyth icons, drawn for this project
   public final static ImageIcon runIcon
         = new ImageIcon(EAD_DIR + "run.png" );
   public final static ImageIcon runConsIcon
         = new ImageIcon(EAD_DIR + "runCons.png" );
   public final static ImageIcon eadgythIconSmall
         = new ImageIcon(EAD_DIR + "EadgythIconSmall.png");
   public final static ImageIcon compileIcon
         = new ImageIcon(EAD_DIR + "compile.png");
   public final static ImageIcon eadgythIcon
         = new ImageIcon(EAD_DIR + "EadgythIcon.png");
}