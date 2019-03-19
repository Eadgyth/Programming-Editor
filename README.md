<h4>GOAL</h4>
<p>
A text editor which can be used for coding and which can be easily set up to run code for
testing. The editor should be as simple as possible to use and be helpful, for example, to
write applications for home requirements or quick testing.
<br>
<h4>SHORT DESCRIPTION</h4>
<p>
To run code from within the editor firstly open a file from or newly save a file to the
directory that is to be defined as a project (the file my be saved in sub-directory of the
project directory, too). While a file of the project is open (and selected in the tab bar)
a project category is selected from the 'Project' menu. This opens a simple settings dialog.
The selectable categories so far are Java, Perl, Python, R and HTML.
<p>
Several projects may be defined in parallel. Switching between projects is done after
selecting a file that is part of the project to change to.
<br>
<h4>DOCUMENTATION</h4>
<p>
A more detailed description of the features, screenshots, a help site and code documentation
can be found <a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
<br>
<h4>REQUIREMENTS FOR TESTING</h4>
<p>
Running and compiling the program requires Java 8 or higher. Compiling and building an
executable jar file can be done by creating a Java project with existing sources in Netbeans
(using the src folder from the repository as sources directory).
<p> 
To try the program also the executable jar file in the
<a href="https://github.com/Eadgyth/Programming-Editor/releases">releases</a> site may be
used.
<p>
If this editor shall be used for compiling Java code by the built-in compile option it must
be made sure that it is run using the JRE contained in the JDK (and not the public JRE).
<p>
For using the built-in function to run code the path variables that point to the executables
of a programming language may have to be set in the OS (they have to under Windows).
<br>
<h4>LIMITATIONS</h4>
<p>
The editor includes a basic console to show output/error messages from a tested program
or from freely defined system commands. This console is interactive but interaction does
not work properly if a process buffers all the output until completion in the case that
output is not to the terminal of the OS (PERL, for example, but not Java). To display
output correctly block-buffering would have to be disabled if this option is available
for a language (by a corresponding command option which can be entered in the project
settings or by a switch in a script itself). Also, an application may be tested by starting
it in the terminal of the OS using the option to enter system commands.
<p>
If the built-in options to compile a Java project and to create a jar from it are used only
sources found in the project directory (or a sources sub-directory if given) are compiled.
Inclusion of external libraries is so far not supported.
<br>
<h4>ACKLOWLEDGEMENT</h4>
<p>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<br>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
