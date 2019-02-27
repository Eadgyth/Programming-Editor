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
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The starting of external system processes
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
   private int exitVal;
   private Process process;
   private PrintWriter out;

   /**
    * @param consPnl  the reference to ConsolePanel
    * @param fileTreeUpdate  the updating of the file tree
    */
   public ProcessStarter(ConsolePanel consPnl, Runnable fileTreeUpdate) {
      this.consPnl = consPnl;
      this.fileTreeUpdate = fileTreeUpdate;
      consPnl.setEnterCmdAct(e -> startNewCmd());
      consPnl.setRunAct(e -> startPreviousCmd());
      consPnl.setStopAct(e -> endProcess());
      consPnl.addKeyListener(Output);
      consPnl.addCaretListener(CaretCorrection);
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
      consPnl.enableEnterCmdBt();
      if (cmdMap.containsKey(workingDir)) {
         previousCmd = cmdMap.get(workingDir);
      }
      else {
         previousCmd = "";
      }
      consPnl.enableRunBt(previousCmd.length() > 0);
   }

   /**
    * Starts a system process in this working directory. The file tree
    * is updated after the process has ended. If it is tried to start a
    * process while while another task uses the console a dialog is shown
    * and the process is not startet.
    *
    * @param cmd  the start command in which arguments are separated by
    * spaces
    */
   public void startProcess(String cmd) {
      isAborted = false;
      if (!consPnl.setUnlockedAndActive()) {
         return;
      }
      consPnl.enableRunBt(false);
      consPnl.focus();
      consoleText = "Run " + cmd;
      consPnl.setText("");
      consPnl.appendTextBr(consoleText);
      new Thread(() -> {
         try {
            List<String> cmdList = Arrays.asList(cmd.split(" "));
            ProcessBuilder pb
                  = new ProcessBuilder(cmdList).redirectErrorStream(true);

            pb.directory(fWorkingDir);
            process = pb.start();
            out = new PrintWriter(process.getOutputStream());
            new CaptureInput().execute();
            exitVal = process.waitFor();
         }
         catch (IOException | InterruptedException e) {
            EventQueue.invokeLater(() -> {
               consPnl.appendTextBr(cmdNotFoundMsg(cmd));
               lockConsole();
            });
         }
         finally {
            if (out != null) {
               out.close();
            }
            EventQueue.invokeLater(() -> consPnl.keepActive(
                  process != null && process.isAlive()));
         }
      }).start();
   }

   //
   //--private--/
   //

   private void startNewCmd() {
      String cmd = Dialogs.textFieldInput(enterCmdMsg(), "Run", previousCmd);
      if (cmd != null) {
         cmd = cmd.trim();
         previousCmd = cmd;
         cmdMap.put(workingDir, cmd);
         if (cmd.length() > 0) {
            startProcess(cmd);
         }
      }
   }

   private void startPreviousCmd() {
      startProcess(previousCmd);
   }

   private void endProcess() {
      if (process != null && process.isAlive()) {
         new Thread(() -> {
             process.destroy();
             isAborted = true;
         }).start();
      }
   }

   private class CaptureInput extends SwingWorker<Void, String> {
      private final InputStream is = process.getInputStream();
      private final InputStreamReader isr = new InputStreamReader(is);
      private final BufferedReader reader = new BufferedReader(isr);

      @Override
      protected Void doInBackground() {
         try {
            int cInt;
            char c;
            while ((cInt = reader.read()) != -1) {
               c = (char) cInt;
               String s = String.valueOf(c);
               publish(s);
            }
         }
         catch (IOException e) {
            FileUtils.log(e);
         }
         finally {
            try {
               reader.close();
            }
            catch (IOException e) {
               FileUtils.log(e);
            }
         }
         return null;
      }

      @Override
      protected void process(List<String> s) {
         for (String str : s) {
            consPnl.appendText(str);
            consoleText = consPnl.getText();
         }
      }

      @Override
      protected void done() {
         setEndingMsg(exitVal);
         lockConsole();
         EventQueue.invokeLater(fileTreeUpdate);
      }
   }

   private final KeyListener Output = new KeyAdapter() {

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

   private final CaretListener CaretCorrection = new CaretListener() {

      @Override
      public void caretUpdate(CaretEvent e) {
         if (process == null || !process.isAlive()) {
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
   
   private void lockConsole() {
      consPnl.setLocked();
      consPnl.enableRunBt(previousCmd.length() > 0);
   }

   private String cmdNotFoundMsg(String cmd) {
      return
         "Failed to run "
         + cmd + " in the current working directory "
         + workingDir;
   }

   private String enterCmdMsg() {
      return
         "Enter a system command which is executed in the current"
         + " working directory ("
         + workingDirName
         + ")";
   }
}
