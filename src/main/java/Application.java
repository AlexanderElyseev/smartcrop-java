import com.github.quadflask.smartcrop.CropResult;
import com.github.quadflask.smartcrop.Options;
import com.github.quadflask.smartcrop.SmartCrop;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Application {
    public static void main(String args[]) throws IOException {
        Options options = new Options()
            .cropWidth(960).cropHeight(265)
            .maxScale(4f).minScale(1f).scaleStep(0.5f)
            .scoreDownSample(2);

        SmartCrop smartCrop = new SmartCrop(options);
        CropResult result = smartCrop.analyze(ImageIO.read(new File("/Users/alexandereliseev/Desktop/tQkigP2fItdzJWvtIhBvHxgs5yE.jpg")));
        ImageIO.write(result.resultImage, "png", new File("/Users/alexandereliseev/Desktop/result.png"));
        ImageIO.write(result.debugImage, "png", new File("/Users/alexandereliseev/Desktop/debugq.png"));
    }
}
