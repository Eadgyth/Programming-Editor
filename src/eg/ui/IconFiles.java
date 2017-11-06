package eg.ui;

import java.io.File;
import javax.swing.ImageIcon;

//--Eadgyth--//
import eg.Preferences;

/*
 * Static ImageIcons for buttons etc.
 * <p>
 * Next to a few home-made icons the majority is from the
 * Tango Desktop Project; see
 * <a href="http://tango.freedesktop.org/Tango_Icon_Library">Tango Project</a>
 */
public class IconFiles {

   private final static String F_SEP = File.separator;
   private final static String ICONS_DIR
         = System.getProperty("user.dir") + F_SEP + "Resources" + F_SEP;
   private final static String TANGO_DIR;
   private final static String EAD_DIR;
   
   static {
      Preferences prefs = new Preferences();
      prefs.readPrefs();
      if ("22 x 22".equals(prefs.getProperty("iconSize"))) {
         TANGO_DIR = ICONS_DIR + "Tango" + F_SEP + "large" + F_SEP;
         EAD_DIR = ICONS_DIR + "EadIcons" + F_SEP + "large" + F_SEP;
      }
      else {
         TANGO_DIR = ICONS_DIR + "Tango" + F_SEP + "small" + F_SEP;
         EAD_DIR = ICONS_DIR + "EadIcons" + F_SEP + "small" + F_SEP;
      }
   }

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
   public final static ImageIcon INFO_ICON
         = new ImageIcon(TANGO_DIR + "dialog-information.png");
   public final static ImageIcon ERROR_ICON
         = new ImageIcon(TANGO_DIR + "dialog-error.png");
   public final static ImageIcon WARNING_ICON
         = new ImageIcon(TANGO_DIR + "dialog-warning.png");

   // Eadgyth icons
   public final static ImageIcon CHANGE_PROJ_ICON
         = new ImageIcon(EAD_DIR + "changeProj.png" );
   public final static ImageIcon RUN_ICON
         = new ImageIcon(EAD_DIR + "run.png" );
   public final static ImageIcon RUN_CMD_ICON
         = new ImageIcon(EAD_DIR + "runCons.png" );
   public final static ImageIcon COMPILE_ICON
         = new ImageIcon(EAD_DIR + "compile.png");
   public final static ImageIcon EADGYTH_ICON_16
         = new ImageIcon(ICONS_DIR + "EadIcons" + F_SEP
         + "small" + F_SEP + "EadgythIcon.png");
   public final static ImageIcon EADGYTH_ICON_SET
         = new ImageIcon(EAD_DIR + F_SEP + "EadgythIcon.png");
}
