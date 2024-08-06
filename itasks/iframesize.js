/* Javascript needed in all Bebras pages to allow communication of iframe size.
   cf. https://www.viget.com/articles/using-javascript-postmessage-to-talk-to-iframes/

   add the following script tag to the bottom of the page:
   <script src="../../iframesize.js"></script>
 */

respondToSizingMessage = function (e) {
    // e.data is the string sent by the origin with postMessage.
    if (e.data.startsWith('sizing:')) {
        e.source.postMessage(e.data + ':' + document.body.scrollHeight
            + ':' + document.body.scrollWidth, e.origin);
    }
}

// we have to listen for 'message'
window.addEventListener('message', respondToSizingMessage, false);
