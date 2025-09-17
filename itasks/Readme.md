Interactive tasks
====

This folder contains JavaScript code that can be used by the task pages for implementing (interactive) tasks.
These files should be stored on the task server at top level directory, i.e., two folders 'up' with respect
to the task pages. They will typically be included in the task page header using a script tag like:

    <head>
        ...
        <script src="../../itasks-v1.js"></script>
    </head>

The following JavaScript files are included in this folder:
* `iframe-size.js`: handles the sizing of the internal frame containing the task page. Must be included as the last line
  of *every* task page, not only the interactive ones.
* `itasks-v1.js`: handles communication between the internal frame containing the task page and the surrounding
  page provided by the server. Must be included in the header of *every* interactive task page.

The following provide a 'library' for common types of interaction:
* `dnd-v1.js`: drag and drop 
* `click-v1.js`: click to change (rotate) selection 
* `mark-v1.js`: mark (select/deselect) items by clicking on them

The folder also contains some example HTML files that demonstrate how to use the JavaScript code.