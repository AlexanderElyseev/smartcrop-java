import com.github.quadflask.smartcrop.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Application {
    public static void main(String args[]) throws IOException {
        Options options = new Options()
            .cropWidth(960).cropHeight(265)
            .maxScale(5f).minScale(1f).scaleStep(0.5f)
            .scoreDownSample(10)
            .skinWeight(3).saturationWeight(1)
            .ruleOfThirds(true);

        BufferedImage source1 = ImageIO.read(new File("/Users/alexandereliseev/Desktop/tQkigP2fItdzJWvtIhBvHxgs5yE.jpg"));
        BufferedImage source2 = ImageIO.read(new File("/Users/alexandereliseev/Desktop/tPwpc9Uo1Fly50urDxfGWWk7H77.jpg"));
        BufferedImage source3 = ImageIO.read(new File("/Users/alexandereliseev/Desktop/mjAgh43dKjzjTYAnC59M9zxIKVf.jpg"));

//        Crop crop1 = new Crop(288, 250, 2880, 795);
//        Crop crop2 = new Crop(288, 260, 2880, 795);
//        Image image1 = new Image(source1, options);
//        System.out.printf("Crop: %s, score: %s\n", crop1.toString(), image1.score(crop1));
//        System.out.printf("Crop: %s, score: %s\n", crop2.toString(), image1.score(crop2));
//        ImageIO.write(image1.getCroppedImage(crop1), "png", new File("/Users/alexandereliseev/Desktop/result1-1.png"));
//        ImageIO.write(image1.getCroppedImage(crop2), "png", new File("/Users/alexandereliseev/Desktop/result1-2.png"));

        Crop crop21 = new Crop(192, 68, 1440, 397);
        Crop crop22 = new Crop(192, 200, 1440, 397);
        Image image2 = new Image(source2, options);
        System.out.printf("Crop: %s, score: %s\n", crop21.toString(), image2.score(crop21));
        System.out.printf("Crop: %s, score: %s\n", crop22.toString(), image2.score(crop22));
        ImageIO.write(image2.getCroppedImage(crop21), "png", new File("/Users/alexandereliseev/Desktop/result2-1.png"));
        ImageIO.write(image2.getCroppedImage(crop22), "png", new File("/Users/alexandereliseev/Desktop/result2-2.png"));

        SmartCrop smartCrop = new SmartCrop(options);
        CropResult result1 = smartCrop.analyze(source1);
        ImageIO.write(result1.resultImage, "png", new File("/Users/alexandereliseev/Desktop/result1.png"));
        ImageIO.write(result1.debugImage, "png", new File("/Users/alexandereliseev/Desktop/debug1.png"));

        CropResult result2 = smartCrop.analyze(source2);
        ImageIO.write(result2.resultImage, "png", new File("/Users/alexandereliseev/Desktop/result2.png"));
        ImageIO.write(result2.debugImage, "png", new File("/Users/alexandereliseev/Desktop/debug2.png"));

        CropResult result3 = smartCrop.analyze(source3);
        ImageIO.write(result3.resultImage, "png", new File("/Users/alexandereliseev/Desktop/result3.png"));
        ImageIO.write(result3.debugImage, "png", new File("/Users/alexandereliseev/Desktop/debug3.png"));
    }
}
