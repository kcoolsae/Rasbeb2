/*
 * mark-v1.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2024-2025 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

elementsOrderedByMark = function () {
    const groups = document.querySelectorAll('[data-mark]');
    return Array.from(groups).sort((a, b) => a.dataset.mark.localeCompare(b.dataset.mark));
}

// string with all selected values, ordered by mark
getAnswerString = function () {
    const elements = elementsOrderedByMark();
    // list of elements that have class 'marked'
    return elements
        .filter((element) => element.classList.contains('marked'))
        .map((element) => element.dataset.mark)
        .join('');
}

getModelString = function () {
    return ''; // no page model
}

// initialize the task by marking the correct elements
initializeTask = function (answerAsString, modelAsString) {
    // no model, page is reconstructed from answer
    if (answerAsString !== '') {
        const elements = elementsOrderedByMark();
        for (element of elements) {
            if (answerAsString.includes(element.dataset.mark)) {
                element.classList.add('marked');
            } else {
                element.classList.remove('marked');
            }
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {

    markUnmark = function (element) {
        if (element.classList.contains('marked')) {
            element.classList.remove('marked');
        } else {
            element.classList.add('marked');
        }
    }

    const elements = document.querySelectorAll("[data-mark]")
    for (const element of elements) {
        element.addEventListener("click", function (e) {
            markUnmark(element);
        })
    }
});