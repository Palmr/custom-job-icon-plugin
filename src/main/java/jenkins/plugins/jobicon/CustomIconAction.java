/*
 *     Copyright 2011 Jean-Christophe Sirot <sirot@chelonix.com>
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

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This action add {@code /customIcon/} to the job URL space and serve the
 * icon image.
 *
 * This action accepts the query parameter {@code size} with these
 * acceptable values {@code 16x16}, {@code 24x24} and {@code 32x32}. If this
 * query parameter is present the icon image is resized before being served.
 *
 * @author Jean-Christophe Sirot
 */
public class CustomIconAction implements Action
{
	private final Job job;
	private final Map<Integer, byte[]> cache;

	/**
	 * Creates a new {@code CustomIconAction}.
	 *
	 * @param job the owner job
	 */
	public CustomIconAction(Job job)
	{
		this.job = job;
		this.cache = new HashMap<Integer, byte[]>();
	}

	public String getIconFileName()
	{
		return null;
	}

	public String getDisplayName()
	{
		return "Custom Icon";
	}

	public String getUrlName()
	{
		return "customIcon";
	}

	/**
	 * Handles the action call.
	 *
	 * @param req  the stapler request
	 * @param rsp  the stapler response
	 */
	public void doDynamic(StaplerRequest req, StaplerResponse rsp)
			throws IOException, ServletException, InterruptedException
	{
		CustomIconProperty prop = (CustomIconProperty) job.getProperty(CustomIconProperty.class);
		FilePath fpath = Jenkins.getInstance().getRootPath()
				.child("userContent").child(CustomIconProperty.PATH)
				.child(prop.iconfile);
		URL url = fpath.toURI().toURL();
		InputStream in;
		String size = req.getParameter("size");
		if ("16x16".equals(size)) {
			in = new ByteArrayInputStream(resize(url, 16));
		} else if ("24x24".equals(size)) {
			in = new ByteArrayInputStream(resize(url, 24));
		} else if ("32x32".equals(size)) {
			in = new ByteArrayInputStream(resize(url, 32));
		} else {
			in = url.openStream();
		}
		rsp.serveFile(req, in, 0, 0, -1, fpath.getName());
	}

	/**
	 * Resizes the icon image or gets it from cache.
	 *
	 * @param url  the original image URL
	 * @param size  the requested size
	 * @return  the resized image data
	 */
	private byte[] resize(URL url, int size) throws IOException
	{
		if (cache.get(size) != null) {
			return cache.get(size);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageUtils.resize(url.openStream(), out, size);
		cache.put(size, out.toByteArray());
		return out.toByteArray();
	}
}
