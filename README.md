# 4156-Team-Project
The group project for COMS 4156: Advanced Software Engineering
### Team Members
- Zachary C Hine zh2305
- Peinan Zhou pz2308
- Yonghan Xie yx2853
- Wenchao Zhai wz2602
## Getting Started
- `git clone` the project.
- `Open` the project with IntelliJ IDE. The IDE should be able to detect the Maven configuration
file (`pom.xml`) and prompt to configure Maven.
- Our project is build with Spring Boot, run the restService in IDLE, which will start running the server at http://localhost:8080

- You must first create a client in order to use the API by making a post request to /add-client with a user specified accessToken as a query parameter: i.e. /add-client?accessToken={client_access_token} will add a new client with access_token of 'client_access_token' and an empty image_path to the Client table of the db
- Then, you can upload a photo to the service by navigating on your browser to: http://localhost:8080/photo?accessToken={client_access_token}, clicking on the 'Capture Photo' button, and then entering a name for your image and uploading it by clicking on the 'Upload Photo' button. This will upload the image to the service's filesystem, update the Client table's image_path field to point to the location of the image ('/images/face-images/{img_name}), and update the Image table with an entry id={image_number} and image_path={image_path}.

## Unit Testing
- Unit tests are in `/src/test/java/com/example
/imaging/`
- Run test classes under the directory.

## Continuous Integration
We are using GitHub Actions for CI. 
- will run `mvn build` and `mvn test` to build the project and run all 
  tests. 
Bugfinder
- CodeQL to run static bug analysis on the project. The job will 
  run `github/codeql-action/analyze@v2` to checkout bug

# Code Coverage

To see code coverage, we used Jacoco \
use commands: \
`mvn clean test jacoco:report`\
Otherwise, clean and test, then generate jacoco report in maven tools
Then, open **../target/site/jacoco/com.example.bugyourspot.reservation/index.html**

the branch coverage is shown in the documentation


## API Testing
- Our API entry points are tested using Postman.
- After running the server at http://localhost:8080, start the test

## API Endpoints
`/pose-angle`
- **Method**: GET
- **Request Body**: name: A string parameter representing the name of the image file.
- **Expected Response**:
- Response body is a JSON object containing the image name and the yaw angle.
  - Response structure:
  {
  "name": "name_of_the_image_file",
  "yawAngle": calculated_yaw_angle
}
  - 400: e.g. file not found
  - 500:IOException, e.g. no face detected

`/eye-color`
- **Method**: GET
- **Request Body**: name: A string parameter specifying the name of the image file for eye color prediction.
- **Expected Response**:
- Response body is a JSON object containing the person's name and their predicted eye color.
  - Response structure:
 {
  "name": "name_of_the_person",
  "eyeColor": predicted_eye_color
}
  - 400: e.g. file not found
  - 500:IOException, e.g. no face detected

## Style Checker
- We are using the CheckStyle plugin on IntelliJ to check for potential style warning/errors.
