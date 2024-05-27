/** onclick handler that use ajax to store a generated password in a text field
 * @param {string} id - id of the text field
 * @param url - url to the password generator
 */
function generatePassword(id, url) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.send()
    xhr.onload = function () {
        document.getElementById(id).value = xhr.responseText;
    }
}