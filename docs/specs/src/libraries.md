Programming languages, libraries and frameworks
===

The primary programming language for the project is Java.

Programming languages
* Most of the project is written in **Java**, version ≥ 17
* Some pages use **Javascript**, most of which is **Typescript** generated
* HTML page templates are written partly in (minimal) **Scala** as is common in the Play framework.

Database
* PostgreSQL 15 
* Extensive use of views, triggers and stored procedures <span class="new">written in Java?</span>
* Local Java framework (*DAOHelper*) that forms a thin layer above SQL.

Web framework
* [Play framework](https://www.playframework.com/) 2.8.* - Java version.
* (? [Play bootstrap support](https://adrianhurt.github.io/play-bootstrap/) <span class="new">Seems to be no longer maintained.</span>)

Web pages
* HTML 5 avoiding inclusion of too much JavaScript
* {:.new} Bootstrap CSS[^1]

Build system
* SBT, because the Play framework probably cannot easily be used in another way

Deployment
* Server needs to run UNIX. Version 1 uses bash scripts to unpack and adjust uploaded question folders
* {:.new} [Docker](https://www.docker.com/) would ease installation on multiple servers for multiple countries 

#### Footnotes

[^1]: 
      Whether we use bootstrap version 4 or 5 needs to be decided. There is currently no Play-bootstrap 
      support for version 5, but the contest system pages are probably simple enough not to require this.
      The contest _management_ system however, is more complicated, and e.g. needs decent error reporting
      in forms which might be more difficult to develop from scratch. On the other hand, bootstrap 5 has 
      support for right to left rendering of text and UI.