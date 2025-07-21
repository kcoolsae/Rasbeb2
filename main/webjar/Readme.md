Rasbeb2 - webjar
===

Constructs the webjar with fonts and css that are common to the web apps in `main`. 
**Important** Not everything needed for building the webjar is stored in the repository - see below.

### Preliminary setup - Google fonts

Before building the webjar, the following Fira Sans fonts should be downloaded from Google, 
converted to `.woff` and to `.woff2` format
and stored in the directory `.../webjar/public/fonts`. These font files are not be stored in the repository.

    FiraSans-Italic.woff FiraSans-Italic.woff2
    FiraSans-Regular.woff FiraSans-Regular.woff2
    FiraSans-Medium.woff FiraSans-Medium.woff2
    FiraSans-MediumItalic.woff FiraSans-MediumItalic.woff2

### Preliminary setup

To build the webjar you need the bootstrap distribution and additional the `sass` command. Both
can be installed using `npm`, as follows

From the `webjar` folder, do

    mkdir -p bootstrap 
    cd bootstrap
    npm install bootstrap@v5.3.1
    sudo npm install -g sass

To test, from the `webjar` folder, do

    ./customize-bootstrap.sh

This should generate the following files

    public/css/rasbeb2-bootstrap.min.css
    public/css/rasbeb2-bootstrap.min.css.map
    public/js/bootstrap.bundle.min.js

These are not stored in the repository, but will be put into the 
webjar.

### Build with sbt

The `Compile` task of SBT rebuilds the bootstrap CSS-files whenever
any source file is changed. **TODO** Only look at the files in the `scss` directory?
