<h2>Documentation</h2>
<ul>
<li><a href="docs/help/help.html">Help to use Eadgyth Programming-Editor</a></li>
<li><a href="docs/javadoc/index.html">Overview of the code (javadoc)</a></li>
</ul>
<br>
<h2>Features</h2>
<h4>Text editing</h4>
<ul>
<li>Working with several open files in tabs. The tabbar may be hidden to work
    with a single document.</li>
<li>Enabling and disabling word-wrapping (word-wrapping not in combination with
    line-numbering, however).</li>
<li>Undo/redo including the undoing of a "replace all" action.</li>
<li>A basic find/replace function.</li>
<li>Clearing end-of-line (trailing) white spaces.</li>
<li>An "exchange editor" pane to edit text in a separate view and to facilitate
    the exchange of text within a file or between files</li>
<li>A basic syntax highlighting for Java, Perl, R, HTML, Javascript and CSS
    (not quite perfect, especially not for HTML).</li>
<li>A basic auto-indentation.</li>
<li>Block-wise increase or decrease of the indentation.</li>
</ul>
<br>
<h4>Testing source code</h4>
<ul>
<li>A simple and flexible assignment of files as project.</li>
   <ul>
   <li>A project may be simply the location of (source) files.</li>
   <li>The program also works with a given directory structure of a project.
       This may include folders for source files or for executable files.
       Also packages and sub-packages are taken into account in a Java project.</li>
   <li>A number of projects can be assigned and one can easily switch between
       projects.</li>
   <li>Retrieval of already defined projects after newly starting the program.</li>
   </ul>
<li>Pre-defined actions in a project by simply using menu selections or buttons.</li>
   <ul>
   <li>Compiling/testing Java code and bundling a Java program in an executable
       jar file. Non-Java files may be included in a compilation and in a jar file.</li>
   <li>Testing a Perl script.</li>
   <li>Testing an R script.</li>
   <li>Viewing Html code in the default web browser.</li>
   </ul>
<li>A basic console to view the output and error messages after compiling a Java
    program or during testing a Java, R or Perl program. If the tested program asks
    for input this can be entered in the text area of the console (for a limitation
    when an interactive Perl script is tested see
    <a href="help/help.html#PerlProject">Testing a Perl script</a>.</li>
<li>Running self-chosen system commands in the current working directory. The output
    of the process is shown in the console area. Such commands may be used for actions
    that are not pre-defined in a project category. Also a generic project without
    any pre-defined actions or dependency on a certain file type may be assigned</li>
</ul>
<h4>Other</h4>
<ul>
  <li>A file explorer that shows the files of the project that is set active.
      It offers to open files, delete files and to create directories</i>
</ul>
</ul>
<h4>Screenshots</h4>
<p>A demo Java project run in the program</p>
<img src="docs/images/Windows10SystemLAF.png" width="600"/>
<p>Simple editor view</p>
<img src="docs/images/SimpleEditorView.png" width="400"/>

