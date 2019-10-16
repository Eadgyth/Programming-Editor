<h3>Eadgyth Programming-Editor</h3>
<p>
This is a text editor which can be used for coding and which can be easily set up to run
code by using built-in functions. The goal is that setting up a coding project is as simple
as possible and that the editor is helpful, for example, to write applications for home
requirements. So far, project categories are Java (worked out most), Perl, Python, R and
HTML.
<br>
<p>
A more detailed description of the features, screenshots, a help site and code documentation
can be found <a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
<p> 
To try the program the executable jar file in a
<a href="https://github.com/Eadgyth/Programming-Editor/releases">release</a> may be used.
<br>
<br>
<img src="docs/images/ExampleProject.png" width="700"/><br><br>
<h4>REQUIREMENTS</h4>
<p>
Running and compiling the program requires Java 8 or higher. Building an executable jar
file can be done, for example, after creating a Java project with existing sources in
Netbeans using the src folder from the repository as sources directory.
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
or from freely defined system commands and also allows to run interactive command-line
programms. However, the console does not work as expected if a process buffers its
output until completion in the case that the output is not to the terminal of the OS
(PERL, for example, but not Java). To display output correctly block-buffering would
have to be disabled if this option is available for a language (by a corresponding
command option which can be entered in the project settings or by a switch in a script
itself).
<br>
<h4>ACKLOWLEDGEMENT</h4>
<p>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<br>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
