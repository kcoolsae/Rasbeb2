/*
 * origin-interactive.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

/** Java script for communication between the rasbeb server and the internal
 * task frame, for interactive tasks.
 */

/** Send initial data to the task */
function initializeTask(iframe, answerAsString, modelAsString) {
    iframe.contentWindow.postMessage(
        'init:' + answerAsString + ':' + modelAsString, '*'
    );
}

/**
 * Request the receiving end to send the answer back. The answer will be handled
 * by the function receiveAnswer(json) which should be defined in the origin page
 */
function requestAnswer(iframe) {
    iframe.contentWindow.postMessage('getAnswer', '*');
}

/**
 * Request the receiving end to send the page model back. The model will be handled
 * by the function receiveModel(json) which should be defined in the origin page
 */
function requestModel(iframe) {
    iframe.contentWindow.postMessage('getModel', '*');
}

function handleITaskResponse(e) {
    // e.data is the string sent by the remote with postMessage.
    if (e.data.startsWith('answer:')) {
        receiveAnswer(e.data.substring(7));
    } else if (e.data.startsWith('model:')) {
        receiveModel(e.data.substring(6));
    }
}

window.addEventListener('message', handleITaskResponse, false);