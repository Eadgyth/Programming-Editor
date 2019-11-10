I started to make a text editor when I started to learn Java for myself and wished that NotePad
had a compile and a run button. I knew that really versatile code editors and IDEs exist but
those were too big for me. I am still learning and struggling …

A goal is that it is as simple as possible to set up the editor to try out code (my "Hello
World" program) by using built-in functions and provide at the same time some flexibility to run
a real little coding project. The editor is written using itself, for example. The languages for
which a ‘run’ function is built-in are limited so far (Java, Perl, R, Python and HTML) but the
code for the program is somewhat easily extensible to add support other languages. A "generic"
project may be set as well. Here a system command can be specified, for example to run a batch
file in a given project directory or to not use the built-in function.

The github repository for the program also contains, well ... "releases". Anyway, these include
an executable jar file of the program which can be run by double clicking. The jar file is
named after the user name of the repo because that's for me a name for the program. Eadgyth is
an Old English version of the name Edit. I did the naming of the repo wrong, I think.

See also <a href="https://github.com/Eadgyth/Programming-Editor/blob/master/README.md">
README</a> for requirements.

<hr>
<h3>How to set up the editor to run code?</h3>
The program is set up for running source code by creating a "project". This is initially
just a working directory which distinguishes "project files" from "non-project files" and
which defines the directory where commands (built-in or self defined) are executed. Setting
a project (or changing to another project, see below) requires that a file that is
part of the project is open and also in the selected tab if multiple files are open. This
can be any file and does not even have to be a source file. It may also be found in a
sub-directory path relative to the project directory.
<br><br>
Here is an example for a 'Hello World' program in Java. The 'Hello World' program was saved as
HelloWorld.java in a directory named helloworld. The project settings are opened by selecting
'Open settings for...' and choosing the category Java:

<img src="images/opensettings.png" width="600"/>


In the project settings the name of the project directory and the name of the Java file had
to be entered. The other two fields remained emtpy because HelloWorld.java was not saved in
separate sub-directory for sources and the distination for the compiled class file (next step)
was just the project directory:

<img src="images/projectsettings.png" width="600"/>


After setting the project files can be viewed in the 'Project explorer'. It was also checked
to save the configuration to be able to reload the project when a project file is opened after
newly starting the editor. Then it was tried to compile the program. However, the console panel
that opened showed errors:

<img src="images/compile.png" width="600"/>


After correcting the code a new compilation generated the class file HelloWorld.class and the
program could be run:

<img src="images/run.png" width="600"/>

