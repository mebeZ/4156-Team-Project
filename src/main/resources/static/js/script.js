const video = document.getElementById('camera-stream');

const captureButton = document.getElementById('capture-btn');
const uploadButton = document.getElementById('upload-btn');
const cancelButton = document.getElementById('cancel-btn')

const imageCapture = document.getElementById('image-capture');
const canvas = document.getElementById('photo-preview');
const context = canvas.getContext('2d');
const imageDisplay = document.getElementById('image-preview');

const imageUploadForm = document.getElementById('image-upload-form');
const fetchImageButton = document.getElementById("fetch-image-button");

const uploadContainer = document.getElementById("upload-container");
const fetchContainer = document.getElementById("fetch-container");

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
        // Draw the fetched image
        img.onload = function() {
            context.drawImage(img, 0, 0, canvas.width, canvas.height);
        }
        // Render the drawing of the fetched image visible
        imageCapture.style.display = 'block';
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
    }, 'image/png');
    // Show and hide the required div elements
    imageCapture.style.display = 'block'; // Render the image-capture div visible 
    uploadContainer.style.display = 'block'; // Render the upload-container div visible
    fetchContainer.style.display = 'none'; // Render the fetch-container div invisible
});

// When the user submits the Upload Photo form (by clicking on the 'Upload Photo' button), upload the captured image by making a POST request to /upload controller with the image passed in as form data
imageUploadForm.addEventListener('submit', async function(event) {
    // Prevent default form submission behavior
    event.preventDefault();

    imageCapture.style.display = 'none'; // Render the captured image div invisible 
    uploadContainer.style.display = 'none'; // Render the upload form invisible
    fetchContainer.style.display = 'block'; // Render the fetch-container div visible

    // Retrieve the filename from the form
    const imageName = document.getElementById("image-name").value + '.png';
    console.log("Image name: ", imageName);
    if (capturedBlob) {
        const uploadData = new FormData();
        uploadData.append('file', capturedBlob, imageName); // 'file' corresponds to @RequestParam("file") in the /upload controller
        uploadData.append('token', accessToken); // 'token' corresponds to @RequestParam("token") in the /upload controller
        // Wait until the image has been uploaded to the db (or has started) before redirecting at the end of this function: 'await' enables the fetching of images that we've just uploaded
        await fetch('http://localhost:8080/upload', {
            method: 'POST',
            body: uploadData
        })
        .then(response => {
            console.log(response.body);
            if(response.ok) {
                const eyeData = new FormData();
                eyeData.append('name', imageName); // 'name' corresponds to @RequestParam("name") in the /eye-color controller
                eyeData.append('token', accessToken); // 'token' corresponds to @RequestParam("token") in the /eye-color controller
            } else {
                throw new Error('Network response failed.');
            }
        })
        .catch(error => {
            console.log("Error: ", error);
        });
    } else {
        console.log("No image captured to upload");
    }
    // Redirect to /upload in order to update the model to include the uploaded image so that it can be fetched
    window.location.href = "/upload?accessToken=" + encodeURIComponent(accessToken);
});

// For the CancelButton, we must hide the #upload-container and #image-capture divs and show the #fetch-container div
cancelButton.addEventListener('click', function() {
    fetchContainer.style.display = 'block';
    uploadContainer.style.display = 'none';
    imageCapture.style.display = 'none';
});