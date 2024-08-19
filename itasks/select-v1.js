/*
 * select-v1.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */
select_selectsOrderedByKey = function () {
    const selects = document.querySelectorAll('[data-select]');
    return Array.from(selects).sort((a, b) => a.dataset.select.localeCompare(b.dataset.select));
}

getModelString = function () {
    return ''; // no page model
}

getAnswerString = function () {
    return select_selectsOrderedByKey().map(select => select.value).join('');
}

initializeTask = function(answer, model) {
    if (answer !== '') {
        const selects = select_selectsOrderedByKey();
        for (let index = 0; index < selects.length; index++) {
            selects[index].value = answer.charAt(index);
        }
    }
}
