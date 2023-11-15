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

## Testing
- Unit tests are in `/src/test/java/com/example
/imaging/`

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
  - In case of an error (e.g., no faces detected, file not found), the application might log the error and terminate

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
  - The service terminates on encountering an error, such as failure in eye detection.

## Style Checker
- We are using the CheckStyle plugin on IntelliJ to check for potential style warning/errors.
