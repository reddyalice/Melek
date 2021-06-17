package com.alice.mel;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.io.File;

import static org.bytedeco.opencv.global.opencv_core.cvFlip;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

public class Watch implements Runnable{
    final int INTERVAL = 100;///you may use interval
    CanvasFrame canvas = new CanvasFrame("Web Cam");
    @Override
    public void run() {
        //new File("images").mkdir();

        FrameGrabber grabber = new OpenCVFrameGrabber(0); // 1 for next camera
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img;
        int i = 0;
        try {
            grabber.start();

            while (canvas.isActive()) {
                Frame frame = grabber.grab();
                img = converter.convert(frame);

                //the grabbed frame will be flipped, re-flip to make it right
                //cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise

                //save
                //cvSaveImage("images" + File.separator + (i++) + "-aa.jpg", img);

                canvas.showImage(frame);

                //Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Watch gs = new Watch();
        Thread th = new Thread(gs);
        th.start();
    }
}
