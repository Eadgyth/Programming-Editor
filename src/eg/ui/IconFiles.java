package eg.ui;

import java.io.File;
import javax.swing.ImageIcon;

//--Eadgyth--//
import eg.Preferences;

/*
 * Static <code>ImageIcons</code>
 */
public class IconFiles {

   private final static IconBuilder ICONS = new IconBuilder();
   private final static String ICONS_DIR = "icons/";
   private final static String TANGO_DIR;
   private final static String EAD_DIR;
   
   static {
      Preferences prefs = Preferences.readProgramPrefs();
      prefs.readPrefs();
      if ("22 x 22".equals(prefs.getProperty("iconSize"))) {
         TANGO_DIR = ICONS_DIR + "Tango/large/";
         EAD_DIR = ICONS_DIR + "EadIcons/large/";
      }
      else {
         TANGO_DIR = ICONS_DIR + "Tango/small/";
         EAD_DIR = ICONS_DIR + "EadIcons/small/";
      }
   }

   // Tango icons
   public final static ImageIcon OPEN_ICON
         = ICONS.createIcon(TANGO_DIR + "document-open.png");
   public final static ImageIcon CLOSE_ICON
         = ICONS.createIcon(TANGO_DIR + "document-close.png");
   public final static ImageIcon SAVE_ICON
         = ICONS.createIcon(TANGO_DIR + "document-save.png");
   public final static ImageIcon UNDO_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-undo.png");
   public final static ImageIcon REDO_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-redo.png");
   public final static ImageIcon CUT_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-cut.png");
   public final static ImageIcon COPY_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-copy.png");
   public final static ImageIcon PASTE_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-paste.png");
   public final static ImageIcon INDENT_ICON
         = ICONS.createIcon(TANGO_DIR + "format-indent-more.png");
   public final static ImageIcon OUTDENT_ICON
         = ICONS.createIcon(TANGO_DIR + "format-indent-less.png");
   public final static ImageIcon STOP_PROCESS_ICON
         = ICONS.createIcon(TANGO_DIR + "process-stop.png");
   public final static ImageIcon CLEAR_ICON
         = ICONS.createIcon(TANGO_DIR + "edit-clear.png");
   public final static ImageIcon REFRESH_ICON
         = ICONS.createIcon(TANGO_DIR + "view-refresh.png");
   public final static ImageIcon INFO_ICON
         = ICONS.createIcon(TANGO_DIR + "dialog-information.png");
   public final static ImageIcon ERROR_ICON
         = ICONS.createIcon(TANGO_DIR + "dialog-error.png");
   public final static ImageIcon WARNING_ICON
         = ICONS.createIcon(TANGO_DIR + "dialog-warning.png");

   // Eadgyth icons
   public final static ImageIcon CHANGE_PROJ_ICON
         = ICONS.createIcon(EAD_DIR + "changeProj.png" );
   public final static ImageIcon RUN_ICON
         = ICONS.createIcon(EAD_DIR + "run.png" );
   public final static ImageIcon RUN_CMD_ICON
         = ICONS.createIcon(EAD_DIR + "runCons.png" );
   public final static ImageIcon COMPILE_ICON
         = ICONS.createIcon(EAD_DIR + "compile.png");
   public final static ImageIcon EADGYTH_ICON_16
         = ICONS.createIcon(ICONS_DIR + "EadIcons/small/EadgythIcon.png");
}
