/*
 * dnd-v1.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

// auxiliary function returning an array with all drop sites ordered by key
dnd_dropSitesOrderedByKey = function () {
    const dropSites = document.querySelectorAll('div[data-dnd-drop]');
    return Array.from(dropSites).sort((a, b) => a.dataset.dndDrop.localeCompare(b.dataset.dndDrop));
}

// auxiliary function that moves a draggable element to a drop site, and if necessary, interchanges with the source
dnd_moveToDropSite = function (draggableElement, dropSite) {
    if (dropSite.hasChildNodes()) {
        const parentOfSource = draggableElement.parentElement;
        dropSite.appendChild(draggableElement);
        parentOfSource.appendChild(dropSite.firstChild);
    } else {
        dropSite.appendChild(draggableElement);
    }
}

// string with all values of the drop sites in order. If a drop site is empty, the value is '_'.
getAnswerString = function () {
    return dnd_dropSitesOrderedByKey().map(dropSite => dropSite.hasChildNodes() ? dropSite.firstChild.dataset.dndDrag : '_').join('');
}

// initialize the task by moving the draggable elements to the correct drop sites
initializeTask = function (userid, taskid, answerAsString) {
    if (answerAsString !== '') {
        const dropSites = dnd_dropSitesOrderedByKey();
        dropSites.forEach(function (dropSite, index) {
            const ch = answerAsString.charAt(index);
            if (ch !== '_') {
                const draggableElement = document.querySelector(`[data-dnd-drag="${ch}"]`);
                dnd_moveToDropSite(draggableElement, dropSite);
            }
        });
    }
}

// add listeners to draggable elements when the page is loaded
document.addEventListener('DOMContentLoaded', function () {

    const draggableElements = document.querySelectorAll('[data-dnd-drag]');
    for (const draggableElement of draggableElements) {
        draggableElement.draggable = true;
        const data = draggableElement.dataset.dndDrag;
        draggableElement.addEventListener('dragstart', function (e) {
            e.dataTransfer.setData('text/plain', data);
            e.dataTransfer.effectAllowed = 'move';
        });
    }

    const dropSites = document.querySelectorAll('div[data-dnd-drop]');
    for (const dropSite of dropSites) {
        dropSite.addEventListener('dragover', function (e) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
        });
        dropSite.addEventListener('dragenter', function (e) {
            dropSite.classList.add('dnd-accept-drop');
        });
        dropSite.addEventListener('dragleave', function (e) {
            dropSite.classList.remove('dnd-accept-drop');
        });
        dropSite.addEventListener('drop', function (e) {
            e.preventDefault();
            const data = e.dataTransfer.getData('text/plain');
            const draggedElement = document.querySelector(`[data-dnd-drag="${data}"]`);
            dropSite.classList.remove('dnd-accept-drop');
            dnd_moveToDropSite(draggedElement, dropSite);
        });
    }
});