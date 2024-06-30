/*
 * clock.js
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

const elem = document.getElementById('clock');

function updateClock() {
    let text;
    if (seconds <= 0) {
        elem.setAttribute('class', 'black');
        text='00:00';
    } else {
        elem.setAttribute('class', seconds <= red_limit ? 'red' : seconds <= orange_limit ? 'orange' : 'green');
        // format time as mm:ss
        const s = seconds % 60;
        const m = (seconds - s) / 60;
        text = (m < 10 ? '0' + m : m) + ':' + (s < 10 ? '0' + s : s);
    }
    document.getElementById('clock-text').innerText = text;
}

updateClock()
const countdown = setInterval(function () {
    seconds--;
    updateClock()
    if (seconds <= 0) {
        clearInterval(countdown);
    }
}, 1000);