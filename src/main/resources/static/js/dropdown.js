const submitButton = document.getElementById("submitButton");
var selectedImageName = null;


/*
Modify the button text to match the clicked dropdown item @element
*/
function changeDropdownTitle(element) {
    var imageName = element.innerText;
    var button = document.getElementById("dropdownMenuButton");
    button.innerHTML = imageName;
    selectedImageName = imageName;
}

submitButton.addEventListener('click', function() {
    if (selectedImageName != null) {
        console.log(selectedImageName);
    }
});