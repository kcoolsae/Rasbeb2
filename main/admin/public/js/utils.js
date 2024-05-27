
/* attached to the 'number of rows' selector in a paged table */

function submitNewPageSize(url, size) {
    let form = document.createElement("form");
    form.setAttribute("method", "POST");
    form.setAttribute("action", url);
    let hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "pageSize");
    hiddenField.setAttribute("value", size);
    form.appendChild(hiddenField);
    document.body.appendChild(form); // is this needed?
    form.submit();
}

/* used in conjuction with updateDeleteField template */
function updateDeleteFieldSwitch(element) {
    /* form(span, input, span(button,button)::hidden, span(button,button)::visible) */

    let parent = element.parentElement.parentElement;
    let siblings = parent.children;
    let zero = element.parentElement === siblings[3];
    let one = !zero;

    if (zero) {
        const parentWidth = parent.parentElement.getBoundingClientRect().width;
        siblings[1].style.width = (parentWidth - 60) + "px";
    }

    siblings[0].hidden = zero;
    siblings[1].hidden = one;
    siblings[2].hidden = one;
    siblings[3].hidden = zero;

}

/* on click handler for the checkbox in the header. Use as follows:
<pre>
    <input type="checkbox" onclick="updateAllCheckboxesInTable(this);">
</pre>
    @param checkbox the box that governs the state of all other checkboxes in the table
 */
function updateAllCheckboxesInTable(checkbox) {
    const checked = checkbox.checked;
    // find table ancestor of this element
    let table = checkbox.parentElement;
    while (table.tagName.toUpperCase() !== "TABLE") {
        table = table.parentElement;
    }
    // check/uncheck all checkboxes in this table
    table.querySelectorAll("input[type=checkbox]").forEach((ch) => ch.checked = checked);
}

/* File upload form handling. Attach to search button */
function uploadSearchButtonOnClick(button) {
    button.closest(".file-browser").querySelector("input[type=file]").click();
}

/* File upload form handling. Attach to file input field */
function uploadFileInputOnChange(input) {
    let ancestor = input.closest(".file-browser");
    ancestor.querySelector(".file-browser-text").value = input.value.replace(/C:\\fakepath\\/i, ''); // remove fakepath
    ancestor.querySelector(".file-browser-upload").classList.remove("invisible");
}
