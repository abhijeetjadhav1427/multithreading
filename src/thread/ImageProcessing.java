package thread;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageProcessing {
	private static final String SOURCE_FILE = "./resources/images.jpeg";
	private static final String DESTINATION_FILE = "./resources/imageResult.jpeg";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		BufferedImage original = ImageIO.read(new File(SOURCE_FILE));
		BufferedImage result = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		int start = (int)System.currentTimeMillis();
		
//		singleThreadedRecolor(original, result, 0, 0, original.getWidth(), original.getHeight());
		multiTjreadedRecolor(original, result, 4);
		
		int end = (int) System.currentTimeMillis();
		
		System.out.println(end-start);
		
		File outputFile = new File(DESTINATION_FILE);
		ImageIO.write(result, "jpeg", outputFile);
	}
	
	private static void multiTjreadedRecolor(BufferedImage original, BufferedImage result, int threadCount) throws InterruptedException {
		int width = original.getWidth();
		int height = original.getHeight()/threadCount;
		List<Thread> threads = new ArrayList<>();
		
		for(int i=0; i<threadCount; i++) {
			int x = 0;
			int y = i*height;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					recolorImage(original, result, x, y, width, height);
				}
			});
			threads.add(th);
		}
		
		for(Thread th: threads) {
			th.start();
		}
		for(Thread th: threads) {
			th.join();
		}
	}
	public static void singleThreadedRecolor(BufferedImage original, BufferedImage result, int leftCorner,
			int topCorner, int width, int height) {
		recolorImage(original, result, leftCorner, topCorner, width, height);
	}
	
	public static void recolorImage(BufferedImage original, BufferedImage result, int leftCorner,
			int topCorner, int width, int height) {
		for (int x = leftCorner; x < leftCorner + width && x < original.getWidth(); x++) {
			for (int y = topCorner; y < topCorner + height && y < original.getHeight(); y++) {
				recolorPixel(original, result, x, y);
			}
		}
	}

	public static void recolorPixel(BufferedImage original, BufferedImage result, int x, int y) {
		int rgb = original.getRGB(x, y);

		int red = getRed(rgb);
		int green = getGreen(rgb);
		int blue = getBlue(rgb);

		int newRed, newGreen, newBlue;
		if (isGrayColored(red, green, blue)) {
			newRed = Math.min(255, red + 10);
			newGreen = Math.max(0, green - 80);
			newBlue = Math.max(0, blue - 10);
		} else {
			newRed = red;
			newGreen = green;
			newBlue = blue;
		}
		
		int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
		setRGB(result, x, y, newRGB);
	}
	public static void setRGB(BufferedImage image, int x, int y, int rgb) {
		image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
	}

	public static boolean isGrayColored(int red, int green, int blue) {
		return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
	}

	public static int createRGBFromColors(int red, int green, int blue) {
		int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
	}

	public static int getRed(int rgb) {
		return (rgb & 0x00FF0000) >> 16;
	}

	public static int getGreen(int rgb) {
		return (rgb & 0x0000FF00) >> 8;
	}

	public static int getBlue(int rgb) {
		return rgb & 0x000000FF;
	}
}
