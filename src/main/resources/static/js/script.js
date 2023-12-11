const video = document.getElementById('camera-stream');
const canvas = document.getElementById('photo-preview');
const imageDisplay = document.getElementById('image-display');
const captureButton = document.getElementById('capture-btn');
const uploadButton = document.getElementById('upload-btn');
const fetchImageButton = document.getElementById('fetch-image-btn');
const messages = document.getElementById('messages');
const context = canvas.getContext('2d');

console.log("Hello world");

let capturedBlob = null; // To hold the blob after capturing the photo

if (navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(function (stream) {
            video.srcObject = stream;
        })
        .catch(function (error) {
            displayMessage('Something went wrong accessing the camera!', true);
        });
}

captureButton.addEventListener('click', function() {
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0, canvas.width, canvas.height);
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
        formData.append('image', capturedBlob, 'capture.png');
        fetch('http://localhost:8080/upload-image', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if(response.ok) {
                    return response.json();
                }
                throw new Error('Network response failed.');
            })
            .then(data => {
                displayMessage('Image uploaded successfully!', false, data.imageId);
            })
            .catch((error) => {
                displayMessage('Upload failed: ' + error.message, true);
            });
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