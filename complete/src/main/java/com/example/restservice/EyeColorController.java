package com.example.restservice;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.HighGui;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;


import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EyeColorController {

	private static final String name = "name: %s";
	private static final String eyeColor = "color: %s";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/eyeColor")
	public EyeColor getColor(@RequestParam(value = "name", defaultValue = "World") String name) {

		String eyeColor = "Brown";

		return new EyeColor(String.format(eyeColor, eyeColor), String.format(name, name));
	}

	public void detectFace(String fpath){
		Imgcodecs imageCodecs = new Imgcodecs();
		Mat imgMatrix = imageCodecs.imread(fpath);
		CascadeClassifier faceCascade = new CascadeClassifier();
		MatOfRect faces = new MatOfRect();
		faceCascade.detectMultiScale(imgMatrix, faces);

		float borderThickness = 5;

		for (Rect face : faces.toList()) {
			Imgproc.rectangle(
					imgMatrix,
					new Point(face.x, face.y),
					new Point(face.x + face.width, face.y + face.height),
					// Images are stored in BGR order in OpenCV2
					new Scalar(0, 0, 255), // A red outline
                    (int) borderThickness
            );
		}
		// Display the detected faces until a key is pressed
		HighGui.imshow("Detected faces", imgMatrix);
		HighGui.waitKey();
	}
}
