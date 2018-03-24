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
 * The starting of a external processes
 */
public class ProcessStarter {

   private final ConsolePanel consPnl;
   /*
    * Accociates working directories with commands entered in the dialog */
   private final HashMap<String, String> cmdMap = new HashMap<>();   
   private String workingDir = System.getProperty("user.home");
   private String workingDirName = new File(workingDir).getName();
   private String previousCmd = "";
   /*
    * The text set in the console by a process' output; initially the
    * displayed start command*/
   private String consoleText = "";
   private boolean isAborted = false;
   private boolean isActive = false;
   private Process process;
   private PrintWriter out;
   private Runnable kill;

   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public ProcessStarter(ConsolePanel consPnl) {
      this.consPnl = consPnl;
      cmdMap.put(workingDir, previousCmd);
      consPnl.setCmdAct(e -> startNewCmd());
      consPnl.setRunAct(e -> startPreviousCmd());
      consPnl.setStopAct(e -> endProcess());
   }

   /**
    * Sets the working directory where processes are started
    *
    * @param workingDir  the working directory
    */
   public void setWorkingDir(String workingDir) {
      this.workingDir = workingDir;
      File f = new File(workingDir);
      this.workingDirName = f.getName();
      if (cmdMap.containsKey(workingDir)) {
         previousCmd = cmdMap.get(workingDir);
         consPnl.enableRunBt(previousCmd.length() > 0);
      }
      else {
         previousCmd = "";
         consPnl.enableRunBt(false);
      }     
   }

   /**
    * Starts a process in this working directory
    *
    * @param cmd  the start command in which the arguments are separated
    * by spaces
    */
   public void startProcess(String cmd) {
      isAborted = false;
      if (!isProcessEnded()) {
         return;
      }
      List<String> cmdList = Arrays.asList(cmd.split(" "));
      consoleText = "<<Run: " + cmd + ">>\n";
      consPnl.setText(consoleText);
      setConsoleActive(true);
      consPnl.focus();
      EventQueue.invokeLater(() -> {
         try {
            ProcessBuilder pb
                  = new ProcessBuilder(cmdList).redirectErrorStream(true);
            pb.directory(new File(workingDir));
            process = pb.start();
            out = new PrintWriter(process.getOutputStream());
            new CaptureInput().execute();
            sendOutput(out);
            correctCaret();
         }
         catch(IOException e) {
            setConsoleActive(false);
            consPnl.appendText(cmdNotFoundMsg(cmd));
         }
      });
   }

   /**
    * Returns if no process is currently running
    *
    * @return  true if no process is running, false otherwise
    */
   public boolean isProcessEnded() {
      boolean isEnded = process == null;
      if (!isEnded) {
         Dialogs.warnMessage(PROCESS_RUNNING_MSG);
      }
      return isEnded;
   }

   //
   //--private--/
   //

   private void startNewCmd() {
      String cmd = Dialogs.textFieldInput(enterCmdMsg(), "Run", previousCmd);
      if (cmd != null) {
         previousCmd = cmd;
         cmdMap.put(workingDir, cmd);
         if (cmd.length() > 0) {
            consPnl.enableRunBt(false);
            startProcess(cmd);
         }
         else {
            consPnl.enableRunBt(previousCmd.length() > 0);
         }       
      }
   }

   private void startPreviousCmd() {
      startProcess(previousCmd);
   }
   
   private void endProcess() {
      if (process != null) {
         kill = () -> {
             process.destroy();
             isAborted = true;
         };
         new Thread(kill).start();
      }
   }

   private class CaptureInput extends SwingWorker<Void, String> {
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(isr);

      @Override
      protected Void doInBackground() throws Exception {
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
            FileUtils.logStack(e);
         }
         finally {
            consPnl.setCaret(consPnl.getText().length());
            process = null;
            setConsoleActive(false);
            try {
               reader.close();
               out.close();
            }
            catch (IOException e) {
               FileUtils.logStack(e);
            }
         }
         return null;
      }
   }
   
   private void sendOutput(PrintWriter out) {
      KeyListener keyListener = new KeyAdapter() {

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
      consPnl.addKeyListen(keyListener);
   }

   private void correctCaret() {
      CaretListener caretListener = (CaretEvent e) -> {
         if (!isActive) {
             return;
         }
         if (e.getMark() < consoleText.length()) {
            EventQueue.invokeLater(() -> {
               consPnl.setCaret(consPnl.getText().length());
            });
         }
      };
      consPnl.addCaretListener(caretListener);
   }
   
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
      this.isActive = isActive;
   }
   
   private void setEndingMsg(int exitVal) {
      if (exitVal == 0) {
         consPnl.appendText( "\n<<Process ended normally (exit value = "
               + exitVal + ")>>\n");
      }
      else {
         if (isAborted) {
            consPnl.appendText("\n<<Process aborted (exit value = "
                  + exitVal + ")>>\n");
         }
         else {
            consPnl.appendText("\n<<Process ended with error (exit value = "
                  + exitVal + ")>>\n");
         }
      }
   }
   
   //
   // Strings for messages
   
   private final static String PROCESS_RUNNING_MSG
         = "A currently running process must be quit before"
         + " a new process can be started.";
   
   private String cmdNotFoundMsg(String cmd) {
      return
         "<<Error: cannot find " + cmd + " in the directory "
         + workingDir + ">>\n";
   }
   
   private String enterCmdMsg() {
      return
         "Enter a system command which is executed in the current"
          + " working directory (" + workingDirName + ")";
   }
}
