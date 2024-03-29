package eg.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.EventQueue;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import javax.swing.SwingWorker;

//--Eadgyth--/
import eg.syntax.SyntaxUtils;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The starting of external system processes
 */
public class ProcessStarter {

   private static final String START_MESSAGE = ">> Run:\n";
   private static final String TRUNCATE_MESSAGE = ">> NOTE: The output was truncated\n";

   private final Console cons;
   private final Runnable fileTreeUpdate;
   /*
    * Associates working directories with commands entered in the
    * dialog */
   private final HashMap<String, String> cmdMap = new HashMap<>();

   private String startMsg = "";
   private String workingDir;
   private File fWorkingDir;
   private String workingDirName;
   private String previousCmd;
   private String consoleText = "";
   private volatile boolean isAborted = false;
   private Process process;
   private volatile int exitVal;
   private PrintWriter out;

   /**
    * @param cons  the Console
    * @param fileTreeUpdate  the updating of the file tree
    */
   public ProcessStarter(Console cons, Runnable fileTreeUpdate) {
      this.cons = cons;
      this.fileTreeUpdate = fileTreeUpdate;
      cons.setEnterCmdAct(e -> startNewCmd());
      cons.setRunAct(e -> startPreviousCmd());
      cons.setStopAct(e -> endProcess());
      cons.addKeyListener(sendOutput);
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
    * Runs the specified system command with this working directory
    * as described for {@link #startProcess(String)}.
    *
    * @param cmd  the start command in which arguments are separated
    * by spaces and arguments with spaces quoted
    * @param startMsg  an initial message that describes the command
    * and that will be formatted such that it starts with two closing
    * angle brackets and ends with a colon followed by the line
    * separator. Default (if null or the empty string) is 'Run'.
    */
   public void startProcess(String cmd, String startMsg) {
      if (startMsg != null && !startMsg.isEmpty()) {
         this.startMsg = ">> " + startMsg + ":\n";
      }
      startProcess(cmd);
      this.startMsg = "";
   }

   /**
    * Runs the specified system command with this working directory.
    * <p>
    * {@link Console} is used to show output/error from the started
    * process and to send input to it. The file tree is updated after
    * the process has ended. If it is tried to start a process while
    * another task uses the console a warning dialog is shown and
    * the process is not started.
    *
    * @param cmd  the start command in which arguments are separated
    * by spaces and arguments with spaces quoted
    */
   public void startProcess(String cmd) {
      isAborted = false;
      process = null;
      if (!cons.setUnlockedActive()) {
         return;
      }
      cons.enableRunBt(false);
      String msg = startMsg.isEmpty() ? START_MESSAGE : startMsg;
      cons.setText(msg);
      consoleText = cons.getText();
      List<String> cmdList = cmdList(cmd);
      ProcessBuilder pb = new ProcessBuilder(cmdList).redirectErrorStream(true);
      pb.directory(fWorkingDir);
      new Thread(() -> {
         try {
            process = pb.start();
            new CaptureInput().execute();
            out = new PrintWriter(process.getOutputStream());
            exitVal = process.waitFor();
            EventQueue.invokeLater(cons::setInactive);
         }
         catch (IOException | InterruptedException e) {
            EventQueue.invokeLater(() -> {
               cons.appendTextBr(cmdNotFoundMsg(cmd));
               cons.setInactive();
               lockConsole();
            });
            Thread.currentThread().interrupt();
         }
         finally {
            if (out != null) {
               out.close();
            }
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

   private List<String> cmdList(String cmd) {
      List<String> l = new ArrayList<>();
      int i = 0;
      int prev = 0;
      while (i != -1) {
         i = cmd.indexOf(' ', i);
         if (i != -1) {
            if (!SyntaxUtils.isQuoted(cmd, i)) {
               l.add(cmd.substring(prev, i).trim());
               prev = i;
            }
            i++;
         }
      }
      if (i == -1) {
         l.add(cmd.substring(prev).trim());
      }
      return l;
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
            StringBuilder sb = new StringBuilder();
            while ((cInt = reader.read()) != -1 && !isAborted) {
               c = (char) cInt;
               sb.append(String.valueOf(c));
               if (c == '\n' || sb.length() == 200 || !reader.ready()) {
                  publish(sb.toString());
                  sb.setLength(0);
                  Thread.sleep(1);
               }
            }
         }
         catch (IOException | InterruptedException e) {
            endProcess();
            EventQueue.invokeLater(() -> {
               cons.setInactive();
               lockConsole();
               FileUtils.log(e);
            });
            Thread.currentThread().interrupt();
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
            if (consoleText.length() > 300000) {
               String sub = consoleText.substring(150000);
               cons.setText(START_MESSAGE + TRUNCATE_MESSAGE + sub);
            }
            cons.appendText(str);
            consoleText = cons.getText();
         }
      }

      @Override
      protected void done() {
         cons.appendText("\n");
    	   if (exitVal == 0) {
            cons.appendTextBr(
                  "Process ended normally (exit value: "
                  + exitVal
                  + ")");
         }
         else {
            if (isAborted) {
               cons.appendTextBr(
                     "Process aborted (exit value: "
                     + exitVal
                     + ")");
            }
            else {
               cons.appendTextBr(
                     "Process ended with error (exit value: "
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
         if (!cons.isUnlockedActive()) {
            return;
         }
         correctLength();
         if (cons.caretPosition() < consoleText.length()) {
            cons.setCaret(cons.getText().length());
         }
         if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String output = cons.getText().substring(consoleText.length());
            consoleText = cons.getText();
            out.println(output);
            out.flush();
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (!cons.isUnlockedActive()) {
            return;
         }
         correctLength();
      }

      private void correctLength() {
         if (cons.getText().length() < consoleText.length()) {
            cons.setText(consoleText);
         }
      }
   };

   private void endProcess() {
      if (process != null && process.isAlive()) {
         process.destroy();
         isAborted = true;
      }
   }

   private void lockConsole() {
      cons.enableRunBt(!previousCmd.isEmpty());
      cons.setLocked();
   }

   private String cmdNotFoundMsg(String cmd) {
      return
         "Failed to run "
         + cmd
         + " with the current project directory "
         + workingDir;
   }

   private String enterCmdMsg() {
      return
         "Enter a system command to run with the current"
         + " project directory ("
         + workingDirName
         + ")";
   }
}
