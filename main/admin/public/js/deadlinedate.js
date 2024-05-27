// Thanks to chatGPT and copilot

function isSameDay(date1, date2) {
    return date1.getFullYear() === date2.getFullYear() &&
        date1.getMonth() === date2.getMonth() &&
        date1.getDate() === date2.getDate();
}

/* Convert the given Java Instant to a date or time, depending on whether it is today or not.
 */
function displayDateTime(timestamp, locale) {
    // Convert UTC instant to a date
    const dateToCheck = new Date(timestamp);
    const currentDate = new Date();

    // Check if the date is today
    if (isSameDay(currentDate, dateToCheck)) {
        // Display time in the user's time zone and locale
        return dateToCheck.toLocaleTimeString(locale, {timeStyle: 'medium'});
    } else {
        // Display the date
        return dateToCheck.toLocaleDateString(locale, {dateStyle: 'medium'});
    }
}

/* Fill in all spans with a class of "deadline-date" with the date or time of the timestamp stored in
   the data-timestamp attribute and the locale in the data-locale attribute. */
function fillInDeadlineDates() {
    const deadlineDates = document.getElementsByClassName('deadline-date');
    for (const deadlineDate of deadlineDates) {
        deadlineDate.textContent = displayDateTime(
            parseInt(deadlineDate.dataset.timestamp),
            deadlineDate.dataset.locale
        );
    }
}

// do the above after the page is loaded
window.addEventListener('load', fillInDeadlineDates);



