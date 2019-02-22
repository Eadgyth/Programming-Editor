GOAL<br>
A text editor which can be used for coding and which can easily set up to run code for
testing by built-in functions. It should remain as simple as possible and be useful, for
example, to write applications for home requirements or also quick testing.
<p>
SHORT DESCRIPTION<br>
To use built-in functions for running code a file is opened or newly saved and a project
is defined by selecting a project category in the 'Project' menu. This opens a simple
settings dialog. Project categories so far are Java, Perl, Python, R and HTML. A "generic"
project may be as well defined to run self-chosen system commands. In a Java project,
sources which maybe organized in packages can be compliled and packed in an executable
Java-archive (jar). The program allows to set several projects which can be switched between
in parallel and projects can be retrieved after a new start. The program includes a basic
console, a file explorer and a basic syntax highlighting.
<p>
DOCUMENTATION<br>
A more detailed description of the features, screenshots, a help site and code documentation
can be found <a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
<p>
REQUIREMENTS FOR TESTING<br>
Running and compiling the program requires Java 8 or higher. The executable jar file in the
folder 'JarAndPreferences' was made after compilation with JDK 8. Compliling and creating a
a jar can be done with this program itself or by setting a Java project with existing sources
in Netbeans (using the src folder in the repository). Compiling with a java version higher than
8 is also possible but some causes for compiler warnings are not yet removed.
<p>
If the program shall be used for compiling Java code by the built-in compile option it must be
made sure that the program is run using the JRE contained in the JDK (and not the public JRE).
<p>
For using the built-in function to run code the path variables that point to the executables
of a programming language may have to be set in the OS (they have to under Windows).
<p>
LIMITATIONS<br>
Among the countless limitations some need mention:
<ul>
<li>The editor includes a basic console to show output/error messages from a tested program
    or also from freely defined system commands. This console is interactive (input can be
    entered) but interaction does not work properly in the case that a process buffers all
    the output until completion if it is not to the terminal of the OS. Therefore output
    may be unexpected. This can be checked by disabling block-buffering (or enbaling line
    buffering) if this option is available for a language (by a command option or a variable
    in a script itself).</li>
<li>The compilation of java files and the creation of an executable jar file cannot include
    external libraries if the built-in options for compiling and creating a jar are selected.</li>
<li>I tested the program on Windows. There are some problems with the graphical appearance
    on a high dpi screen. I would be grateful for feedback, especially from somebody who may
    have tried it on other platforms (m.bussiek@web.de).</li>
</ul>
<p>
ACKLOWLEDGEMENT<br>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<p>
LICENSE: MIT, see LICENSE<br>
