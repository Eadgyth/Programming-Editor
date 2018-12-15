package eg.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import java.awt.EventQueue;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import javax.swing.SwingWorker;

import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

//--Eadgyth--/
import eg.ui.ConsolePanel;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The starting of external system processes.
 * <p>
 * A process is run by either calling the method {@link #startProcess}
 * or by a command that is entered in a Dialog. Starting a process
 * requires to set a working directory.
 */
public class ProcessStarter {

   private final ConsolePanel consPnl;
   private final Runnable fileTreeUpdate;
   /*
    * Associates working directories with commands entered in the
    * dialog */
   private final HashMap<String, String> cmdMap = new HashMap<>();

   private String workingDir;
   private File fWorkingDir;
   private String workingDirName;
   private String previousCmd;
   private String consoleText = "";
   private boolean isAborted = false;
   private Process process;
   private PrintWriter out;
   private Runnable kill;

   /**
    * @param consPnl  the ConsolePanel
    * @param fileTreeUpdate  the updating of the file tree
    */
   public ProcessStarter(ConsolePanel consPnl, Runnable fileTreeUpdate) {
      this.consPnl = consPnl;
      this.fileTreeUpdate = fileTreeUpdate;
      consPnl.setCmdAct(e -> startNewCmd());
      consPnl.setRunAct(e -> startPreviousCmd());
      consPnl.setStopAct(e -> endProcess());
      consPnl.addKeyListener(output);
      consPnl.addCaretListener(caretCorrection);
   }

   /**
    * Sets the working directory where processes are started
    *
    * @param workingDir  the working directory
    */
   public void setWorkingDir(String workingDir) {
      this.workingDir = workingDir;
      fWorkingDir = new File(workingDir);
      workingDirName = fWorkingDir.getName();
      consPnl.enableSetCmdBt();
      if (cmdMap.containsKey(workingDir)) {
         previousCmd = cmdMap.get(workingDir);
      }
      else {
         previousCmd = "";
      }
      consPnl.enableRunBt(previousCmd.length() > 0);
   }

   /**
    * Starts a system process in this working directory. A warning dialog
    * is shown if it is tried to start a process while a previous process
    * is not yet terminated.
    *
    * @param cmd  the start command in which arguments are separated by
    * spaces
    * @param updateFileTree  true to update the file tree view, false
    * otherwise
    */
   public void startProcess(String cmd, boolean updateFileTree) {
      isAborted = false;
      if (!canStart()) {
         return;
      }
      List<String> cmdList = Arrays.asList(cmd.split(" "));
      consoleText = "Run: " + cmd;
      consPnl.setText("");
      consPnl.appendTextBr(consoleText);
      setConsoleActive(true);
      consPnl.focus();
      EventQueue.invokeLater(() -> {
         try {
            ProcessBuilder pb
                  = new ProcessBuilder(cmdList).redirectErrorStream(true);

            pb.directory(fWorkingDir);
            process = pb.start();
            out = new PrintWriter(process.getOutputStream());
            new CaptureInput(updateFileTree).execute();
         }
         catch(IOException e) {
            setConsoleActive(false);
            consPnl.appendTextBr(cmdNotFoundMsg(cmd));
         }
      });
   }
   
   //
   //--private--/
   //

   private boolean canStart() {
      boolean b = process == null || !process.isAlive();
      if (!b) {
         Dialogs.warnMessage(
               "A currently running process must be terminated first.");
      }
      return b;
   }

   private void startNewCmd() {
      String cmd = Dialogs.textFieldInput(enterCmdMsg(), "Run", previousCmd);
      if (cmd != null) {
         previousCmd = cmd;
         cmdMap.put(workingDir, cmd);
         if (cmd.length() > 0) {
            consPnl.enableRunBt(false);
            startProcess(cmd, true);
         }
         else {
            consPnl.enableRunBt(previousCmd.length() > 0);
         }
      }
   }

   private void startPreviousCmd() {
      startProcess(previousCmd, true);
   }

   private void endProcess() {
      if (process != null && process.isAlive()) {
         kill = () -> {
             process.destroy();
             isAborted = true;
         };
         new Thread(kill).start();
      }
   }

   private class CaptureInput extends SwingWorker<Void, String> {
      private InputStream is = process.getInputStream();
      private InputStreamReader isr = new InputStreamReader(is);
      private BufferedReader reader = new BufferedReader(isr);
      private boolean updateFileTree;
      
      CaptureInput(boolean updateFileTree) {
         this.updateFileTree = updateFileTree;
      }

      @Override
      protected Void doInBackground() {
         try {
            int cInt;
            char c;
            while ((cInt = reader.read()) != -1) {
               c = (char) cInt;
               consPnl.appendText(String.valueOf(c));
               consoleText = consPnl.getText();
               consPnl.setCaret(consoleText.length());
            }
            int exitVal = process.waitFor();
            setEndingMsg(exitVal);
         }
         catch (IOException | InterruptedException e) {
            FileUtils.log(e);
         }
         finally {
            try {
               reader.close();
               out.close();
            }
            catch (IOException e) {
               FileUtils.log(e);
            }
         }
         return null;
      }
      
      @Override
      protected void done() {
         consPnl.setCaret(consPnl.getText().length());
         setConsoleActive(process.isAlive());
         if (updateFileTree) {
            EventQueue.invokeLater(fileTreeUpdate);
         }
      }
   }

   private final KeyListener output = new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
         int key = e.getKeyCode();
         if (key == KeyEvent.VK_ENTER) {
            String output = consPnl.getText().substring(consoleText.length());
            out.println(output);
            out.flush();
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (consPnl.getText().length() < consoleText.length()) {
            consPnl.setText(consoleText);
         }
      }
   };

   private final CaretListener caretCorrection = new CaretListener() {

      @Override
      public void caretUpdate(CaretEvent e) {
         if (!consPnl.isActive()) {
            return;
         }
         if (e.getDot() < consoleText.length()
               || e.getMark() < consoleText.length()) {

            EventQueue.invokeLater(() -> {
               consPnl.setCaret(consPnl.getText().length());
            });
         }
      }
   };

   private void setConsoleActive(boolean isActive) {
      if (!isActive) {
         if (previousCmd.length() > 0) {
            consPnl.enableRunBt(!isActive);
         }
      }
      else {
         consPnl.enableRunBt(!isActive);
      }
      consPnl.setActive(isActive);
   }

   private void setEndingMsg(int exitVal) {
      if (exitVal == 0) {
         consPnl.appendText("\n");
         consPnl.appendTextBr(
               "Process ended normally (exit value = "
               + exitVal
               + ")");
      }
      else {
         if (isAborted) {
            consPnl.appendText("\n");
            consPnl.appendTextBr(
                  "Process aborted (exit value = "
                  + exitVal
                  + ")");
         }
         else {
            consPnl.appendText("\n");
            consPnl.appendTextBr(
                  "Process ended with error (exit value = "
                  + exitVal
                  + ")");
         }
      }
   }

   private String cmdNotFoundMsg(String cmd) {
      return
         "Error: cannot find "
         + cmd + " in the directory "
         + workingDir;
   }

   private String enterCmdMsg() {
      return
         "Enter a system command which is executed in the current"
          + " working directory (" + workingDirName + ")";
   }
}
