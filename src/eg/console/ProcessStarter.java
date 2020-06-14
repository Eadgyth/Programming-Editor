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

   private final Console cons;
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
    * @param cons  the reference to Console
    * @param fileTreeUpdate  the updating of the file tree
    */
   public ProcessStarter(Console cons, Runnable fileTreeUpdate) {
      this.cons = cons;
      this.fileTreeUpdate = fileTreeUpdate;
      cons.setEnterCmdAct(e -> startNewCmd());
      cons.setRunAct(e -> startPreviousCmd());
      cons.setStopAct(e -> endProcess());
      cons.addKeyListener(sendOutput);
      cons.addCaretListener(caretCorrection);
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
      cons.enableEnterCmdBt();
      if (cmdMap.containsKey(workingDir)) {
         previousCmd = cmdMap.get(workingDir);
      }
      else {
         previousCmd = "";
      }
      cons.enableRunBt(previousCmd.length() > 0);
   }

   /**
    * Runs the specified system command in this working
    * directory.
    * {@link Console} is used to show output/error from the
    * started process and to send input to it. The file tree
    * is updated after the process has ended. If it is tried
    * to start a process while another task uses the console a
    * warning dialog is shown and the process is not started.
    *
    * @param cmd  the start command in which arguments are
    * separated by spaces
    */
   public void startProcess(String cmd) {
      isAborted = false;
      if (!cons.setUnlockedAndActive()) {
         return;
      }
      cons.enableRunBt(false);
      cons.focus();
      cons.setText("");
      cons.appendTextBr("Run:");
      consoleText = cons.getText();
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
               cons.appendTextBr(cmdNotFoundMsg(cmd));
               lockConsole();
            });
            Thread.currentThread().interrupt();
         }
         finally {
            if (out != null) {
               out.close();
            }
            EventQueue.invokeLater(() -> cons.keepActive(
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
         if (!cmd.isEmpty()) {
            startProcess(cmd);
         } else {
            cons.enableRunBt(false);
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
            cons.appendText(str);
            consoleText = cons.getText();
         }
      }

      @Override
      protected void done() {
    	if (exitVal == 0) {
            cons.appendText("\n");
            cons.appendTextBr(
                  "Process ended normally (exit value = "
                  + exitVal
                  + ")");
         }
         else {
            if (isAborted) {
               cons.appendText("\n");
               cons.appendTextBr(
                     "Process aborted (exit value = "
                     + exitVal
                     + ")");
            }
            else {
               cons.appendText("\n");
               cons.appendTextBr(
                     "Process ended with error (exit value = "
                     + exitVal
                     + ")");
            }
         }
         lockConsole();
         fileTreeUpdate.run();
      }
   }

   private final KeyListener sendOutput = new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
         int key = e.getKeyCode();
         if (key == KeyEvent.VK_ENTER) {
            String output = cons.getText().substring(consoleText.length());
            out.println(output);
            out.flush();
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (cons.getText().length() < consoleText.length()) {
            cons.setText(consoleText);
         }
      }
   };

   private final CaretListener caretCorrection = new CaretListener() {

      @Override
      public void caretUpdate(CaretEvent e) {
         if (process == null || !process.isAlive()) {
            return;
         }
         if (e.getDot() < consoleText.length()
               || e.getMark() < consoleText.length()) {

            EventQueue.invokeLater(() -> cons.setCaret(cons.getText().length()));
         }
      }
   };

   private void lockConsole() {
      cons.setLocked();
      cons.enableRunBt(!previousCmd.isEmpty());
   }

   private String cmdNotFoundMsg(String cmd) {
      return
         "Failed to run "
         + cmd
         + " in the current project directory "
         + workingDir;
   }

   private String enterCmdMsg() {
      return
         "Enter a system command to run in the current"
         + " project directory ("
         + workingDirName
         + ")";
   }
}
