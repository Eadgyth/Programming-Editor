# Java-Programming-Editor / Eadgyth
A text editor written in Java that helps coding (in Java, Perl, HTML) and that is extensible
by plugins which may be developed to perform specialized tasks with text files.
<p>
A further goal is a very simple but flexible setting of a project and usage of already
configured projects.
<p>
REQUIREMENTS FOR TESTING <br>
Running the program requires JAVA 8 (JRE 8). I did not have the opportunity to test
the program on operating systems other than windows Vista, 7 and 10.<p>
The program must be found in the same folder as the two .properties files and the Resources
folder, just like in the 'EadgythProgram' folder in this repository.<p>
Plugins must be stored in the folder 'Plugins' in the same folder as the program and be available
as single jar files.<p>
To compile the program the source files (packages) must be placed in a directory that also
includes the ".properties" files and the 'Resources' folder.<p>
A guide how to configure a project and some other info is found in the Resources
folder (Help.html) and is adressable from the '?' menu. 
<p>
IDEA FOR FURTHER DEVELOPEMENT<br>
1) More types of projects may be added. A type of project is defined by the interface
'ProjectActions' in the 'projects' package.<br>
2) To develop plugins that can do specialized work with text files. Presently, a
plugin would implement the interface 'Pluggable' in the 'plugins' package. This can be
implemented to have access to the text document that is in the selected tab and that can
have a graphical view in the main window.
<p>
DOCUMENTATION <br>
The javadoc of the program is found in this repository (Docs/Ead-JavaDoc).
<p>
LIMITATIONS <br>
1) The Undo/redo is a place holder for a real undo/redo as it simply undoes/redoes single
characters (pasted/deleted text is undone/redone as a whole though).
<br>
2) The console has a function to run system commands (defined the class 'ProcessStarter'
in the package 'eg.console'). Interactive programs that then require reading from and
writing to the started process by typing in the console are not guaranteed to work.
Interactive programs in Java seem to work fine but a Perl script does not unless the
autoflushing of STDOUT is enabled in the script, for example.
<br>
3) The built in packing of a jar file bundles only .class files.
<br>
<p>
PROBLEMS <br>
4) The rendering of the GUI has gone wrong after the computer was in sleep mode when
the system look and feel was selected.
<br>
5)There can be problems with the syntax coloring and with undo/rodo although it is not
clear if these happen in the most recent commit.
<br>
<p>
CONTACT<br>
Malte Bussiek<br>
m.bussiek@web.de<br>
<p>
CONTRIBUTORS<br>
I hope for contributions and critiques.<br>
<p>
LICENSE: MIT, see LICENSE<br>
<p>
SCREENSHOT<br>
Under Windows 10, setting the system look and feel<br>
<img src="Screenshots/Windows10SystemLAF.png" width="600"/>

