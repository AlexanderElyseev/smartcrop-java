package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;

class Image {
    private BufferedImage bufferedImage;
    final int width, height;
    private int[] data;

    Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width * height];
        for (int i = 0; i < this.data.length; i++)
            data[i] = 0xff000000;
    }

    Image(BufferedImage bufferedImage) {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        this.bufferedImage = bufferedImage;
        this.data = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
    }

    int[] getRGB() {
        return data;
    }
}
