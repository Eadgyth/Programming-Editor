# Java-Programming-Editor / Eadgyth
A text editor written in Java that helps coding (in Java, Perl, HTML) and that is extensible
by plugins which may be developed to perform specialized tasks with text files.
<p>
A particular goal is a very simple but flexible setting of a project and usage of already
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
1) Running interactive programs that then require reading from and writing to the started process
in the console panel are not guaranteed to work. Interactive programs in Java seem to
work fine but, for example, a Perl script does not unless the autoflushing of STDOUT is enabled
in the script itself.
<br>
2) The built in packing of a jar file bundles only .class files.
<br>
3) The printing to a printer is rudimentary and the font size is different from
the corresponding font size in other programs (e.g. Editor in Windows). 
<br>
<p>
PROBLEMS <br>
1) The undo has a problem with syntax coloring in combination with undoing several characters
at once. Therefore undoing several characters is not enabled.
<br>
2) Unexpected rendering of the UI happened when the system look and feel was selected
(Windows) and the program was idling for a longer period of time.
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

