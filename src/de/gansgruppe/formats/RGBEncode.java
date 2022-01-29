/*
Copyright (c) 2021-22, GansGruppe & Associates

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.gansgruppe.formats;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * RGBEncode is a Tool for saving data in the form
 * of an PNG file.
 * <p>
 * PART OF THE GFC.
 *
 * @author 0x1905
 * @author 0x0
 */
public class RGBEncode {
	public static void main(String[] args) throws IOException {
		String file = assembleStringFromArray(0, args, ";", false);
		boolean encode = Boolean.parseBoolean(args[file.split(" ").length]);

		if (encode) {
			System.out.print("DATA> ");
			Scanner s = new Scanner(System.in);
			String text = s.nextLine();

			char[] chars = text.toCharArray();
			byte[] bytes = new byte[chars.length];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) chars[i];
			}

			ImageIO.write(createImage(bytes), "png", new File(System.getProperty("user.dir"), file));
		} else {
			for (byte b : loadImageGetData(file)) {
				System.out.println((char) b);
			}
		}
	}

	/**
	 * Creates an Image from Provided Data in Magenta Tint.
	 *
	 * @param data Data to be encoded
	 * @return The Data Encoded into a Image
	 */
	public static BufferedImage createImage(byte[] data) {
		BufferedImage img = new BufferedImage((int) Math.ceil(Math.sqrt(data.length)), (int) Math.ceil(Math.sqrt(data.length)), BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				try {
					Color c = new Color(
							data[x + y * img.getHeight()] & 0xFF,
							0,
							data[x + y * img.getHeight()] & 0xFF);
					img.setRGB(y, x, c.getRGB());
				} catch (ArrayIndexOutOfBoundsException e) {
					img.setRGB(y, x, new Color(255, 255, 255).getRGB());
				}
			}
		}

		return img;
	}

	/**
	 * Encodes data into an Image {@link de.gansgruppe.formats.RGBEncode#createImage(byte[])}
	 * and saves it to file.
	 *
	 * @param data       Data to be encoded
	 * @param outputFile The file to be saved to
	 * @throws IOException
	 */
	public static void createImageSave(byte[] data, String outputFile) throws IOException {
		ImageIO.write(createImage(data), "png", new File(outputFile));
	}

	/**
	 * Loads an Image with encoded Data from file and
	 * reads the data.
	 *
	 * @param file The File to be loaded
	 * @return The loaded data.
	 */
	public static byte[] loadImageGetData(String file) throws IOException {
		InputStream is;
		BufferedImage img = ImageIO.read(new FileInputStream(file));
		return getDataFromImage(img);
	}

	/**
	 * Gets Data encoded in a Buffered Image.
	 *
	 * @param img The image to load the data from
	 * @return The Data encoded in the Image
	 */
	public static byte[] getDataFromImage(BufferedImage img) {
		LinkedList<Byte> data = new LinkedList<>();

		if (img == null) {
			throw new NullPointerException("Image is NULL!");
		}

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				if (img.getRGB(y, x) == Color.WHITE.getRGB()) continue;
				data.add((byte) img.getRGB(y, x));
			}
		}

		byte[] out = new byte[data.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = data.get(i);
		}

		return out;
	}

	private static String assembleStringFromArray(int index, String[] words, String terminator, boolean removeFirst) {
		String out = "";

		for (int i = index; i < words.length; i++) {
			out += words[i];
			if (words[i].endsWith(terminator)) break;
			out += " ";
		}
		return out.substring(removeFirst ? 1 : 0, out.length() - 1);
	}
}
