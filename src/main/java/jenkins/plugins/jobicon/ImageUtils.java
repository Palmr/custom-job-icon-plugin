/*
 *     Copyright 2012 Jean-Christophe Sirot <sirot@chelonix.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jenkins.plugins.jobicon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility functions for images.
 *
 * @author Jean-Christophe Sirot, Nick Palmer
 */
class ImageUtils
{
	private ImageUtils()
	{
	}

	/**
	 * Resize the image from an InputStream to a specific height/width size
	 *
	 * @param in Image InputStream
	 * @param out OutputStream to write the resized image to
	 * @param size size of the output image height/width in pixels
	 * @throws IOException Thrown in the image cannot be read/written to the in/out streams
	 */
	static void resize(InputStream in, OutputStream out, int size)
		throws IOException {
		BufferedImage originalImage = ImageIO.read(in);
		BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = resizedImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setComposite(AlphaComposite.Src);

		g.drawImage(originalImage, 0, 0, size, size, null);

		g.dispose();


		ImageIO.write(resizedImage, "png", out);
	}
}
