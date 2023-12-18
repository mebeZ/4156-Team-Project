const video = document.getElementById('camera-stream');

const captureButton = document.getElementById('capture-btn');
const uploadButton = document.getElementById('upload-btn');

const imageCapture = document.getElementById('image-capture');
const canvas = document.getElementById('photo-preview');
const context = canvas.getContext('2d');
const imageDisplay = document.getElementById('image-preview');

const imageUploadForm = document.getElementById('image-upload-form');
const fetchImageButton = document.getElementById("fetch-image-button");

let selectedImageName = null;

/*
Modify the fetch button text to match the clicked dropdown item @element
*/
function changeDropdownTitle(element) {
    var imageName = element.innerText;
    var button = document.getElementById("dropdownMenuButton");
    button.innerHTML = imageName;
    selectedImageName = imageName;
}

// When the OK button is pressed, fetch the requested image and display it to the client
fetchImageButton.addEventListener('click', function() {
    if (selectedImageName == null) {
        throw new Error("Image not selected"); 
    }
    getImageUrl = "http://localhost:8080/getImage?selectedImageName="+selectedImageName;
    console.log("Url = " + getImageUrl); // Successfully prints this
    fetch(getImageUrl)
    .then(response => response.blob())
    .then(imgBlob => {
        var blobUrl = URL.createObjectURL(imgBlob);
        //imageDisplay.src = blobUrl;
        var img = new Image();
        img.src = blobUrl;
        img.onload = function() {
            context.drawImage(img, 0, 0, canvas.width, canvas.height);
        }
    })
    .catch(error => {
        console.error("Error fetching the image", error);
    });
});

// Fill the camera-stream element with video from the Webcam if there is one
if (navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(function (stream) {
            video.srcObject = stream;
        })
        .catch(function (error) {
            displayMessage('Something went wrong accessing the camera!', true);
        });
}

let capturedBlob = null; // To hold the blob after capturing the photo

// When the user clicks on 'Capture Photo', get the current frame from the WebCam video and draw it to the canvas
captureButton.addEventListener('click', function() {
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    // Gets current frame of the video and draws it onto the canvas
    context.drawImage(video, 0, 0, canvas.width, canvas.height);
    // Converts the content of the canvas to a Blob (Binary Large Object) which represents the image in PNG format
    canvas.toBlob(function(blob) {
        capturedBlob = blob;
        imageCapture.style.display = 'block'; // Render the captured image div visible 
        imageUploadForm.style.display = 'block'; // Render the upload form visible
    }, 'image/png');
});

// When the user submits the Upload Photo form (by clicking on the 'Upload Photo' button), upload the captured image by making a POST request to /upload controller with the image passed in as form data
imageUploadForm.addEventListener('submit', function(event) {
    // Prevent default form submission behavior
    event.preventDefault();

    imageCapture.style.display = 'none'; // Render the captured image div invisible 
    imageUploadForm.style.display = 'none'; // Render the upload form invisible
    
    // Retrieve the filename from the form
    const imageName = document.getElementById("image-name").value + '.png';
    console.log("Image name: ", imageName);
    if (capturedBlob) {
        const uploadData = new FormData();
        uploadData.append('file', capturedBlob, imageName); // 'file' corresponds to @RequestParam("file") in the /upload controller
        uploadData.append('token', accessToken); // 'token' corresponds to @RequestParam("token") in the /upload controller
        fetch('http://localhost:8080/upload', {
            method: 'POST',
            body: uploadData
        })
        .then(response => {
            if(response.ok) {
                const eyeData = new FormData();
                eyeData.append('name', imageName); // 'name' corresponds to @RequestParam("name") in the /eye-color controller
                eyeData.append('token', accessToken); // 'token' corresponds to @RequestParam("token") in the /eye-color controller
            }
            throw new Error('Network response failed.');
        })
        .then(message => {
            console.log("Response from /upload controller: ", message);
        })
        .catch(error => {
            console.log("Error: ", error);
        });
    } else {
        console.log("No image captured to upload");
    }
})