package eg.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import java.util.List;
import java.util.Arrays;

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
 * The starting of an external process
 */
public class ProcessStarter {

   private final ConsolePanel consPnl;

   private String workingDir = System.getProperty("user.home");
   private String workingDirName = new File(workingDir).getName();
   private String previousCmd = "";
   private int caretPos = 0;
   private String consoleText = "";
   private int apparentExitVal = 0;
   private boolean isActive = false;
   private Process process;
   private Runnable kill;

   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public ProcessStarter(ConsolePanel consPnl) {
      this.consPnl = consPnl;
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
   }

   /**
    * Starts a process in this working directory
    *
    * @param cmd  the start command in which the arguments are separated
    * by single spaces
    */
   public void startProcess(String cmd) {
      apparentExitVal = 0;
      if (!isProcessEnded()) {
         return;
      }
      List<String> cmdList = Arrays.asList(cmd.split(" "));
      consoleText = "<<Run: " + cmd + ">>\n";
      consPnl.setText(consoleText);
      caretPos = consoleText.length();
      setConsoleActive(true);
      consPnl.focus();
      EventQueue.invokeLater(() -> {
         try {            
            ProcessBuilder pb
                  = new ProcessBuilder(cmdList).redirectErrorStream(true);
            pb.directory(new File(workingDir));
            process = pb.start();            
            PrintWriter out = new PrintWriter(process.getOutputStream());
            new CaptureInput(out).execute();
            sendOutput(out);
            correctCaret();
         }
         catch(IOException e) {
            setConsoleActive(false);
            consPnl.appendText(
                  "<<Error: cannot find " + cmd 
                  + " in the directory " + workingDir + ">>\n");
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
         Dialogs.warnMessage(
               "The currently running process must"
               + " be quit firstly to start a new process.");
      }
      return isEnded;
   }

   //
   //--private--//
   //

   private void sendOutput(PrintWriter out) {
      KeyListener keyListener = new KeyAdapter() {             
         @Override
         public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
               String output = consPnl.getText().substring(caretPos);
               out.println(output);
               out.flush();                
            }
         }
         @Override
         public void keyReleased(KeyEvent e) {
            if (consPnl.getText().length() < caretPos) {
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
         if (e.getDot() < caretPos) {
            EventQueue.invokeLater(() -> {
               consPnl.setCaret(consPnl.getText().length());
            });
         }
      };
      consPnl.addCaretListen(caretListener);
   }

   private void startNewCmd() {
      String cmd = Dialogs.textFieldInput(
            "Enter a system command that is executed in the current"
            + " working directory (" + workingDirName + ")",
            "Run", previousCmd);
 
      if (cmd != null && cmd.length() > 0) {
         consPnl.enableRunBt(false);
         startProcess(cmd);
      }
      else {
         consPnl.enableRunBt(false);
      }
      previousCmd = cmd;
   }

   private void startPreviousCmd() {
      startProcess(previousCmd);
   }

   private void endProcess() {
      if (process != null) {
         kill = () -> {
             process.destroy();
             apparentExitVal = -1;
         };
         new Thread(kill).start();
      }
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
   
   private class CaptureInput extends SwingWorker<Void, String> {
      PrintWriter out; // to close after program exited
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(isr);
      
      private CaptureInput(PrintWriter out) {
         this.out = out;
      }

      @Override
      protected Void doInBackground() throws Exception {
         try {
            int cInt;
            char c;
            while ((cInt = reader.read()) != -1) {
                c = (char) cInt;
                consPnl.appendText(String.valueOf(c));
                consoleText = consPnl.getText();
                caretPos = consoleText.length();
                consPnl.setCaret(caretPos);
            } 
            int exitVal = process.waitFor();
            if (exitVal == 0) {
               consPnl.appendText(
                     "\n<<Process ended normally (exit value = "
                     + exitVal + ")>>\n");
            }
            else {
               if (apparentExitVal == -1) {
                  consPnl.appendText(
                        "\n<<Process aborted>>\n");
               }
               else {
                  consPnl.appendText(
                        "\n<<Process ended with error (exit value = "
                        + exitVal + ")>>\n"); 
               }
            }
         }
         catch (IOException | InterruptedException e) {
            FileUtils.logStack(e);
         }
         finally {
            consPnl.setCaret(consPnl.getText().length());
            process = null;
            setConsoleActive(false);
            out.close();
            try {
               reader.close();
            }
            catch (IOException e) {
               FileUtils.logStack(e);
            }
         }
         return null;
      }
   }
}
