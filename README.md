<h3>Eadgyth Programming Editor</h3>
<p>
A text and code editor written in Java. It features an easy project setup to run code from within the editor with built-in support for Java, C#, Python and R. Project settings persist across restarts and range from simple script execution to structured Java projects. A 'Custom Commands' category additionally allows defining own compile, run, or build commands.
<p>
The editor can be tried out by downloading the JAR in a <a href="https://github.com/Eadgyth/Programming-Editor/releases">release</a>.
 
It is run with:

```
java -jar Eadgyth.jar
```

<p>
The program can be built in Netbeans or Eclipse (or with a build of Eadgyth itself) using the src directory in the repository. The name of the main file is eg.Eadgyth.java.
<p>
Features & Screenshots: https://eadgyth.github.io/Programming-Editor/
<h4>REQUIREMENTS</h4>
<ul>
<li>JDK 8 or higher to run and build the editor.</li>
<li>To compile Java code by the built-in compile function, the editor must be run with the JRE contained in a JDK (not an issue with Java 11+).</li>
<li>To run C#, Python, or R, the executable paths must be present in your system PATH. To compile and run C# code, the editor internally calls the dotnet command. This requires a .NET SDK to be installed. A minimalistic .csproj file is generated automatically upon compilation.</li>
<li>:information_source: As of v1.2.1, UTF-8 encoded source files are preferred for reliable execution (or compilation). An option to convert files to UTF-8 is available. When opening non-UTF-8 files, the system's default or a selectable encoding is used as a fallback. Previous versions rely on the JVM default encoding only.</li>
</ul>

<h4>LIMITATIONS</h4>
<ul>
<li>Syntax highlighting is simple (coloring of keywords, sigils, tags, string literals, line and block comments) and supports Java, C#, Pythyon, R, HTML, XML, PHP CSS and JavaScript. While generally performant, opening or closing multiline string litererals can cause noticeable delays.</li>
<li>The dark backgrounds are applied to the main window only. Menus, dialogs et cetera are so far excluded.</li>
<li>The built-in console may appear frozen when a process buffers its output. This potentially affects own commands specified in the project category "Custom Commands". This issue can often be resolved by enabling auto-flushing via a command-line switch or script command if the language supports it.</li>
</ul>
<h4>CREDITS & ACKNOWLEDGEMENTS</h4>
<ul>
<li>William Gilreath for his advice and comments</li>
<li>The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.</li>
<li>The dark-blue background theme uses the <a href="https://github.com/dracula/dracula-theme">Dracula Colors</a> and colors for the other dark backgrounds were found starting with these colors.</li>
</ul>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
<p>
<h4>SCREENSHOT</h4>
<p>
<img src="docs/images/SimpleInteractivePyExample.png" width="600"/><br><br>
