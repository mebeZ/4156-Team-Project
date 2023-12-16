function changeDropdownTitle(element) {
    var imageName = element.innerText;
    var button = document.getElementById("dropdownMenuButton");
    button.innerHTML = imageName;
}