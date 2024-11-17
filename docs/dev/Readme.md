Rasbeb 2 - developer documentation
===

The Rasbeb 2 repo consists of the following folders

* `docs/specs` Original specifications document (august 2023) - can be transformed into an
   HTML document using Jekyll. Installed (may 2024) at https://rasbeb2.ugent.be/
* `docs/dev` Developer documentation (= this folder)
* `docs/help` Documentation intended for the end user. One folder per language. Can be viewed at (May 2024)
  https://bebras.ugent.be/ but need probably be adapted to your specific setup, e.g., the index page
  currently refers to Belgian servers.
* `main` Source code of the application(s). Managed by SBT. Consists of several subproject folders.
  * `database` Scripts to build the database
  * `contest` The contest application (pupil's view)
  * `admin` The management application (teacher's/organiser's view)
  * `common` Code common to both applications. Also contains the files with the translations
    to various languages
  * `db` Database layer of the applications
  * `webjar` Shared CSS and Javascript code, bundled in a webjar. Based on bootstrap 5.3
  * (The `project` and `target` directories are part of the SBT build system.)
* `itasks` JavaScript code for use in interactive tasks

### Prerequisites

For running the system
* PostgreSQL database server, version 15.6 or higher
* Java runtime environment, version 21 or higher
* Web server, e.g., Apache
* gnuplot executable
* a script for uploading questions (example provided)

For development
* All of the above, plus
* Java development environment, e.g., as integrated in IntelliJ IDEA
* SBT, version 1.9.2 or higher
* Jekyll, including the following plugins: `jekyll-optional-front-matter`, `jekyll-relative-links`
* Bootstrap 5.3
* Several external libraries, as specified in the various `build.sbt` files. Most of these will
  be downloaded automatically by SBT, with the following exceptions:
  * `be.ugent.caagt:play-utils:1.1`
  * `be.ugent.caagt:daohelper:1.1.13`
  
  which are not yet publicly available and must be installed in your local repository. See
  [additional libraries](caagt-libraries.md).

### Deployment

The contest management system consists of five services that can be deployed on the same server
(as is currently the case in Belgium) or on separate servers. 
* A PostgreSQL database server containing the database for the system
* A standard web server that contains the HTML pages for the tasks and the feedback pages
* A standard web server that contains the user documentation pages. (Usually the same as above.)
* A web server with the student view of the application
* A web server with the teacher and organiser view of the application

In addition, the Play framework on which the web applications are built, should allow the
web applications to be run on a cluster of servers, but this has not been tried.

To deploy 'from scratch' several configuration steps are needed, the database needs to be created and some scripts need to
be adapted to your specific setup. [Here](deploy.md) is a rough outline of the steps to be taken - **TODO** test this and provide more details.