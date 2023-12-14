const video = document.getElementById('camera-stream');
const canvas = document.getElementById('photo-preview');
const imageDisplay = document.getElementById('image-display');
const captureButton = document.getElementById('capture-btn');
const uploadButton = document.getElementById('upload-btn');
const fetchImageButton = document.getElementById('fetch-image-btn');
const messages = document.getElementById('messages');
const context = canvas.getContext('2d');

//console.log("Hello world");

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
        imageDisplay.style.display = 'block';
        imageDisplay.src = URL.createObjectURL(blob);
        uploadButton.style.display = 'inline-block';
    }, 'image/png');
});

uploadButton.addEventListener('click', function() {
    if (capturedBlob) {
        const formData = new FormData();
        formData.append('file', capturedBlob, 'capture.png'); // 'file' corresponds to @RequestParam("file") in the /upload controller
        formData.append('token', accessToken); // 'token' corresponds to @RequestParam("token") in the /upload controller
        fetch('http://localhost:8080/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if(response.ok) {
                return response.text();
            }
            throw new Error('Network response failed.');
        })
        .then(message => {
            console.log("Response from /upload controller: ", message);
        })
        .catch(error => {
            console.log("Error: ", error);
        });
        //.then(data => {
        //    displayMessage('Image uploaded successfully!', false, data.imageId);
        //})
        //.catch((error) => {
        //    displayMessage('Upload failed: ' + error.message, true);
        //});
    } else {
        displayMessage('No image captured to upload.', true);
    }
});

fetchImageButton.addEventListener('click', function() {
    var imageId = prompt("Enter the image ID to fetch:");
    fetch('/image/' + imageId)
        .then(response => {
            if (response.ok) {
                return response.blob();
            } else {
                throw new Error('Network response failed.');
            }
        })
        .then(blob => {
            imageDisplay.style.display = 'block';
            imageDisplay.src = URL.createObjectURL(blob);
            displayMessage('Image fetched successfully!', false, imageId);
        })
        .catch(e => {
            displayMessage('Failed to fetch image: ' + e, true);
        });
});

function displayMessage(msg, isError, imageId) {
    if (imageId) {
        msg += " Image ID: " + imageId;
    }
    messages.textContent = msg;
    messages.className = isError ? 'message error' : 'message';
}