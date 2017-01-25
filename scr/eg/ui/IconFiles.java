package eg.ui;

import java.io.File;
import javax.swing.ImageIcon;

/**
 * Static ImageIcons for buttons etc.
 * <p>
 * Next to a few home-made icons
 * the majority is from the Tango Desctop Project; see
 * <a href="http://tango.freedesktop.org/Tango_Icon_Library">Tango Project</a> 
 */
public class IconFiles {

   private final static String F_SEP = File.separator;
    
   private final static String PROGRAM_DIR
         = System.getProperty("user.dir");
   private final static String TANGO_DIR
         = PROGRAM_DIR + F_SEP + "Resources"
         + F_SEP + "Tango" + F_SEP;
   private final static String EAD_DIR
         = PROGRAM_DIR + F_SEP + "Resources"
         + F_SEP + "EadIcons" + F_SEP;

   // Tango icons
   public final static ImageIcon OPEN_ICON
         = new ImageIcon(TANGO_DIR + "document-open.png");
   public final static ImageIcon CLOSE_ICON
         = new ImageIcon(TANGO_DIR + "document-close.png");
   public final static ImageIcon SAVE_ICON
         = new ImageIcon(TANGO_DIR + "document-save.png");
   public final static ImageIcon UNDO_ICON
         = new ImageIcon(TANGO_DIR + "edit-undo.png");
   public final static ImageIcon REDO_ICON
         = new ImageIcon(TANGO_DIR + "edit-redo.png");
   public final static ImageIcon CUT_ICON
         = new ImageIcon(TANGO_DIR + "edit-cut.png");
   public final static ImageIcon COPY_ICON
         = new ImageIcon(TANGO_DIR + "edit-copy.png");
   public final static ImageIcon PASTE_ICON
         = new ImageIcon(TANGO_DIR + "edit-paste.png");
   public final static ImageIcon INDENT_ICON
         = new ImageIcon(TANGO_DIR + "format-indent-more.png");
   public final static ImageIcon OUTDENT_ICON
         = new ImageIcon(TANGO_DIR + "format-indent-less.png");
   public final static ImageIcon STOP_PROCESS_ICON
         = new ImageIcon(TANGO_DIR + "process-stop.png");
   public final static ImageIcon CLEAR_ICON
         = new ImageIcon(TANGO_DIR + "edit-clear.png");
   public final static ImageIcon REFRESH_ICON
         = new ImageIcon(TANGO_DIR + "view-refresh.png");
         
   // Eadgyth icons, drawn for this project
   public final static ImageIcon RUN_ICON
         = new ImageIcon(EAD_DIR + "run.png" );
   public final static ImageIcon RUN_CMD_ICON
         = new ImageIcon(EAD_DIR + "runCons.png" );
   public final static ImageIcon COMPILE_ICON
         = new ImageIcon(EAD_DIR + "compile.png");
   public final static ImageIcon EADGYTH_ICON
         = new ImageIcon(EAD_DIR + "EadgythIcon.png");
}
