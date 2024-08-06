/*
 * click-v1.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

click_groupsOrderedByKey = function () {
    const groups = document.querySelectorAll('[data-click-group]');
    return Array.from(groups).sort((a, b) => a.dataset.clickGroup.localeCompare(b.dataset.clickGroup));
}

// string with all selected values, ordered by group key
getAnswerString = function () {
    const groups = click_groupsOrderedByKey();
    return groups.map(group => {
        const children = group.children;
        for (let i = 0; i < children.length; i++) {
            if (children[i].style.display !== "none") {
                return children[i].dataset.clickValue;
            }
        }
        return '?';
    }).join('');
}

// initialize the task by displaying the correct group elements
initializeTask = function (userid, taskid, answerAsString) {
    if (answerAsString !== '') {
        const groups = click_groupsOrderedByKey();
        let index = 0;
        for (group of groups) {
            const selected = answerAsString.charAt(index);
            index++;
            for (element of group.children) {
                if (element.dataset.clickValue === selected) {
                    element.style.display = element.dataset.cachedDisplayMode;
                } else {
                    element.style.display = "none";
                }
            }
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {

    selectNext = function (group) {
        // find first child that is not hidden and hide it
        let i = 0;
        const children = group.children;
        const length = children.length;
        while (i < length && children[i].style.display === "none") {
            i++;
        }
        // find next child and show it
        if (i !== length) {
            children[i].style.display = "none";
            if (group.dataset.clickSkipFirst === "true") {
                i = i % (length-1) + 1;
            } else {
                i = (i + 1) % length;
            }
        } // else case should not happen...
        children[i].style.display = children[i].dataset.cachedDisplayMode;
    }

    const groups = document.querySelectorAll("[data-click-group]")
    for(const group of groups) {
        group.addEventListener("click", function (e) {
            selectNext(group);
        })
        // cache display mode for every element and then hide it
        for (el of group.children) {
            el.dataset.cachedDisplayMode = getComputedStyle(el).display;
            el.style.display = "none";
        }
        // but unhide the first element
        const first = group.children[0];
        first.style.display = first.dataset.cachedDisplayMode;
    }
});