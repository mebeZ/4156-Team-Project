const submitButton = document.getElementById("submitButton");
const imageDisplay = document.getElementById("imageDisplay");
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

// When the OK button is pressed, fetch the requested image and display it to the client
submitButton.addEventListener('click', function() {
    if (selectedImageName == null) {
        throw new Error("Image not selected"); 
    }
    getImageUrl = "http://localhost:8080/getImage?selectedImageName="+selectedImageName;
    console.log("Url = " + getImageUrl);
    fetch(getImageUrl)
    .then(response => response.blob())
    .then(imgBlob => {
        imageDisplay.src = URL.createObjectURL(imgBlob);
    })
    .catch(error => {
        console.error("Error fetching the image", error);
    });
});