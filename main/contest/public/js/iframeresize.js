/*
 * iframeresize.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

/* Javascript needed by rasbeb2 to determine the size of the remote iframes.
   cf. https://www.viget.com/articles/using-javascript-postmessage-to-talk-to-iframes/
 */

function setIframeHeight(id, height) {
    const ifrm = document.getElementById(id);
    if (ifrm) {
        ifrm.style.visibility = 'hidden';
        ifrm.style.height = "10px"; // reset to minimal height in case going from longer to shorter doc
        ifrm.style.visibility = 'visible';

        ifrm.style.visibility = 'hidden';
        ifrm.style.height = +height + 40 + "px";
        ifrm.style.visibility = 'visible';
    }
}

handleSizingResponse = function (e) {
    // TODO check origin ?
    const data = e.data.split(':')
    if (data[0] === 'sizing') {
        setIframeHeight(data[1], data[2]);
    } else {
        console.log("Unknown message: " + e.data);
    }
}

function postSize (iframeId) {
    const iframe = document.getElementById(iframeId);
    if (iframe) { // may also be called when a question was not yet uploaded
        iframe.contentWindow.postMessage('sizing:' + iframe.id, '*');
    }
}

window.addEventListener('message', handleSizingResponse, false);
