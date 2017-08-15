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
import java.awt.event.ActionEvent;

import javax.swing.SwingWorker;

import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.utils.FileUtils;


/**
 *  The starting of an external process
 */
public class ProcessStarter {

   private final ConsolePanel consPnl;

   private String workingDir = System.getProperty("user.home");
   private String workingDirTemp = workingDir;
   private String workingDirName = new File(workingDir).getName();
   private String previousCmd = "";
   private int caretPos = 0;
   private String display = "";
   private int apparentExitVal = 0;
   private boolean isActive = false;
   private Process process;
   private Runnable kill;

   /**
    * @param consPnl  the reference to the {@link ConsolePanel}
    */
   public ProcessStarter(ConsolePanel consPnl) {
      this.consPnl = consPnl;
      consPnl.setCmdAct(e -> startNewCmd());
      consPnl.runAct(e -> startPreviousCmd());
      consPnl.runEadAct(e -> runEadgyth());
      consPnl.stopAct(e -> endProcess());
   }

   /**
    * Sets the working directory
    *
    * @param workingDir  the working directory which a process is
    * started in
    */
   public void setWorkingDir(String workingDir) {
      this.workingDir = workingDir;
      File f = new File(workingDir);
      this.workingDirName = f.getName();
      workingDirTemp = workingDir;
   }

   /**
    * Starts a process in this working directory.
    * Is not guaranteed to work in all situations.
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
      display = "<<Run: " + cmd + ">>\n";
      consPnl.setText(display);
      caretPos = display.length();
      consPnl.setActive(true);
      isActive = true;
      consPnl.enableRunBt(false);
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
            consPnl.appendText(
                    "<<Error: cannot find " + cmd 
                  + " in the directory " + workingDir + ">>\n");
         }
      });
   }

   /**
    * Returns true if no process is currently running. A warning is
    * otherwise
    *
    * @return  if no process is currently running
    */
   public boolean isProcessEnded() {
      boolean isEnded = process == null;
      if (!isEnded) {
         JOptions.warnMessage(
               "A process is currently running."
               + " The process must be quit to start a new process.");
      }
      return isEnded;
   }

   //
   // private
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
               consPnl.setText(display);
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
               consPnl.setCaret(consPnl.getText().length()); // cannot be caretPos
            });
         }
      };
      consPnl.addCaretListen(caretListener);
   }

   /**
    * Starts a process in the working directory in which the start command
    * is entered in a dialog window
    */
   private void startNewCmd() {
      String cmd = JOptions.dialogRes(
               "Enter a system command that is executed in the current"
               + " working directory " + workingDirName + ".",
               "Run", previousCmd);
      if (cmd != null && cmd.length() > 0) {
         consPnl.enableRunBt(false);
         startProcess(cmd);
         previousCmd = cmd;
      }
   }

   private void startPreviousCmd() {
      startProcess(previousCmd);
   }

   private void runEadgyth() {
      workingDirTemp = workingDir;
      workingDir = System.getProperty("user.dir");
      System.out.println("Temporary working dir: " + workingDir);
      startProcess("java -jar Eadgyth.jar");
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
   
   private class CaptureInput extends SwingWorker<Void, String> { // why String?
      PrintWriter out; // to close after program exited
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(isr);
      
      CaptureInput(PrintWriter out) {
         this.out = out;
      }

      @Override
      protected Void doInBackground() throws Exception {
         try {
            int cInt = - 1;
            char c;
            while ((cInt = reader.read()) != -1) {
                c = (char) cInt;
                consPnl.appendText(String.valueOf(c));
                display = consPnl.getText();
                caretPos = display.length();
                consPnl.setCaret(caretPos);
            }
            int exitVal = -1;    
            exitVal = process.waitFor();
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
            consPnl.appendText("<<" + e.getMessage() + ">>\n");
            FileUtils.logStack(e);
         }
         finally {
            consPnl.setCaret(consPnl.getText().length());
            process = null;
            if (previousCmd.length() > 0) {
               consPnl.enableRunBt(true);
            }
            consPnl.setActive(false);
            isActive = false;
            /*
             * dir may have been changed to run Eadgyth */
            if (!workingDirTemp.equals(workingDir)) {
               workingDir = workingDirTemp;
            }
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
