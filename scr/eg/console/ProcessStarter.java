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


import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import java.io.Reader;

//--Eadgyth--//
import eg.utils.JOptions;

/**
 *  The starting of an external process
 */
public class ProcessStarter {

   private final ConsolePanel cw;

   private String workingDir = System.getProperty("user.home");
   private String workingDirTemp = workingDir;
   private String workingDirName = new File(workingDir).getName();
   private String previousCmd = "";
   /*
    * The caret position at the end of text after reading input from
    * the process */ 
   private int caretPos = 0;
   /*
    * The text after reading input from the process */
   private String display = "";
   private int apparentExitVal = 0;
   private boolean isActive = false;
   private Process process;
   private Thread runProcess;
   private Thread runInput;
   private Runnable kill;

   /**
    * @param cw  the reference to the ConsolePanel Object in whose
    * text area a prosess writes to or reads from and which provides
    * buttons to which actions events are added
    */
   public ProcessStarter(ConsolePanel cw) {
      this.cw = cw;
      cw.setCmdAct(e -> startNewCmd());
      cw.runAct(e -> startPreviousCmd());
      cw.runEadAct(e -> runEadgyth());
      cw.stopAct(e -> endProcess());
   }

   /**
    * Adds to this the working directory for a started process
    * @param workingDir  the working directory for a started
    * process
    */
   public void addWorkingDir(String workingDir) {
      this.workingDir = workingDir;
      File f = new File(workingDir);
      this.workingDirName = f.getName();
      workingDirTemp = workingDir;
   }

   /**
    * Starts a process in this working directory.
    * Is not guaranteed to work in all situations.
    * @param cmd  the start command in which the arguments are separated
    * by single spaces
    */
   public void startProcess(String cmd) {
      apparentExitVal = 0;
      if (!isProcessEnded()) {
         return;
      }

      runProcess = new Thread() {
         @Override
         public void run() {  
            List<String> cmdList = Arrays.asList(cmd.split(" "));
            display = "<<Run: " + cmd + ">>\n";
            cw.setText(display);
            caretPos = display.length();
            cw.setActive(true);
            isActive = true;
            cw.enableRunBt(false);
            cw.focus();
         
            try {
               ProcessBuilder pb
                  = new ProcessBuilder(cmdList).redirectErrorStream(true);
               pb.directory(new File(workingDir));
               process = pb.start();
               PrintWriter processOutput
                     = new PrintWriter(process.getOutputStream());
               captureInput();

               KeyListener keyListener = new KeyAdapter() {             
                  @Override
                  public void keyPressed(KeyEvent e) { 
                     int key = e.getKeyCode();
                     if (key == KeyEvent.VK_ENTER) {
                        String output = cw.getText().substring(caretPos);
                        processOutput.println(output);
                        processOutput.flush();
                     }
                  }
                  public void keyReleased(KeyEvent e) {
                     if (cw.getText().length() < caretPos) {
                        cw.setText(display);
                     }
                  }
               };
               cw.addKeyListen(keyListener);

               CaretListener caretListener = (CaretEvent e) -> {
                   if (!isActive) {
                       return;
                   }
                   if (e.getDot() < caretPos) {
                       EventQueue.invokeLater(() -> {
                           cw.setCaret(cw.getText().length()); // cannot be caretPos
                       });
                   }
               };
               cw.addCaretListen(caretListener);
            }
            catch(IOException ioe) {
               cw.appendText("<<Error: cannot run " + cmd 
                  + " in the directory " + workingDir + ">>\n");
               System.out.println(ioe.getMessage());  
            }
         }
      };
      runProcess.start();
   }

   /**
    * Returns true if no process is currently running. A warning is shown if
    * a process is running
    * @return  if a process has terminated
    */
   public boolean isProcessEnded() {
      boolean isEnded = process == null;
      if (!isEnded) {
         JOptions.warnMessage("A currently running process must be quit first");
      }
      return isEnded;
   }

   /**
    * Destroys the current process
    */
   public void endProcess() {
      if (process != null) {
         kill = () -> {
             process.destroy();
             apparentExitVal = -1;
         };
         new Thread(kill).start();
      }
   }

   //
   //--private methods--//
   //

   /**
    * The output of the process
    */
   private void captureInput() {
      //System.out.println("Number of active threads: " + Thread.activeCount());
      runInput = new Thread() {
         InputStream is = process.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         Reader read = new BufferedReader(isr);

         @Override
         public void run() {
            try {
               int cInt = - 1;
               char c;    
               while ((cInt = read.read()) != -1) {
                  c = (char) cInt;
                  cw.appendText(String.valueOf(c));
                  display = cw.getText();
                  caretPos = display.length();
                  cw.setCaret(caretPos);
               }
               int exitVal = -1;    
               exitVal = process.waitFor();
               if (exitVal == 0) {
                  cw.appendText("<<Process ended normally (exit value = "
                        + exitVal + ") >>\n");
               }
               else {
                  if (apparentExitVal == -1) {
                     cw.appendText("\n<<Process aborted>>\n");
                  }
                  else {
                     cw.appendText("\n<<Process ended with error (exit value = "
                           + exitVal + ")>>\n");
                  }
               }
            }
            catch (IOException | InterruptedException ioe) {
               cw.appendText("<<" + ioe.getMessage() + ">>\n");
               ioe.printStackTrace();
            }
            finally {
               cw.setCaret(cw.getText().length());
               process = null;
               if (previousCmd.length() > 0) {
                  cw.enableRunBt(true);
               }
               cw.setActive(false);
               isActive = false;
               /*
                * In this version Eadgyth may have been started */
               if (!workingDirTemp.equals(workingDir)) {
                  workingDir = workingDirTemp;
               }
               try {
                  read.close();
               }
               catch (IOException ioe) {
                  ioe.printStackTrace();
               }
            }
         }
      };
      runInput.start();
   }

   /**
    * Starts a process in the working directory in which the start command
    * is entered in a dialog window
    */
   private void startNewCmd() {
      String cmd = JOptions.dialogRes(
               "Run a system command in the working directory '"
               + workingDirName +"'",
               "Command", previousCmd);
      if (cmd != null && cmd.length() > 0) {
         startProcess(cmd);
         previousCmd = cmd;
         cw.enableRunBt(false);
      }
      else {
         cw.enableRunBt(false);
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
}