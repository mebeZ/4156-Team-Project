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
- Alternatively, Go to repository project directory in the terminal, and run the following command:
- For macOS/Linux:
`./mvnw spring-boot:run`

- Windows:
`.mvnw.cmd spring-boot:run`
- Our project is build with Spring Boot, Go to [RestService.java](src/main/java/com/example/imaging/RestService.java), run class RestService, which will start running the server at http://localhost:8080

## Unit Testing
- Unit tests are in `/src/test/java/com/example
/imaging/`
- Run test classes under the directory. `mvn test`

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
