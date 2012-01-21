/*
 * Copyright (c) 2005 Aetrion LLC.
 */
package com.flickr4java.flickr.blogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.Response;
import com.flickr4java.flickr.Transport;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.util.XMLUtilities;

/**
 * Interface for working with Flickr blog configurations.
 *
 * @author Anthony Eden
 * @version $Id: BlogsInterface.java,v 1.14 2009/07/11 20:30:27 x-mago Exp $
 */
public class BlogsInterface {

    private static final String METHOD_GET_SERVICES = "flickr.blogs.getServices";
    private static final String METHOD_GET_LIST = "flickr.blogs.getList";
    private static final String METHOD_POST_PHOTO = "flickr.blogs.postPhoto";

    private String apiKey;
    private String sharedSecret;
    private Transport transportAPI;

    public BlogsInterface(String apiKey, String sharedSecret, Transport transport) {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.transportAPI = transport;
    }

    /**
     * Return a list of Flickr supported blogging services.
     *
     * This method does not require authentication.
     *
     * @return List of Services
     * @throws IOException
     * @throws SAXException
     * @throws FlickrException
     */
    public Collection<Service> getServices()
      throws IOException, SAXException, FlickrException {
        List<Service> list = new ArrayList<Service>();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_GET_SERVICES);
        parameters.put("api_key", apiKey);

        Response response = transportAPI.post(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        Element servicesElement = response.getPayload();
        NodeList serviceNodes = servicesElement.getElementsByTagName("service");
        for (int i = 0; i < serviceNodes.getLength(); i++) {
            Element serviceElement = (Element) serviceNodes.item(i);
            Service srv = new Service();
            srv.setId(serviceElement.getAttribute("id"));
            srv.setName(XMLUtilities.getValue(serviceElement));
            list.add(srv);
        }
        return list;
    }

    /**
     * Post the specified photo to a blog.  Note that the Photo.title and Photo.description are used for the blog entry
     * title and body respectively.
     *
     * @param photo The photo metadata
     * @param blogId The blog ID
     * @param blogPassword The blog password
     * @throws IOException
     * @throws SAXException
     * @throws FlickrException
     */
    public void postPhoto(Photo photo, String blogId, String blogPassword) throws IOException, SAXException, FlickrException {
    	Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_POST_PHOTO);
        parameters.put("api_key", apiKey);

        parameters.put("blog_id", blogId);
        parameters.put("photo_id", photo.getId());
        parameters.put("title", photo.getTitle());
        parameters.put("description", photo.getDescription());
        if (blogPassword != null) {
            parameters.put("blog_password", blogPassword);
        }

        Response response = transportAPI.post(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
    }

    /**
     * Post the specified photo to a blog.
     *
     * @param photo The photo metadata
     * @param blogId The blog ID
     * @throws IOException
     * @throws SAXException
     * @throws FlickrException
     */
    public void postPhoto(Photo photo, String blogId) throws IOException, SAXException, FlickrException {
        postPhoto(photo, blogId, null);
    }

    /**
     * Get the collection of configured blogs for the calling user.
     *
     * @return The Collection of configured blogs
     * @throws IOException
     * @throws SAXException
     */
    public Collection getList() throws IOException, SAXException, FlickrException {
        List blogs = new ArrayList();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_GET_LIST);
        parameters.put("api_key", apiKey);

        Response response = transportAPI.post(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }

        Element blogsElement = response.getPayload();
        NodeList blogNodes = blogsElement.getElementsByTagName("blog");
        for (int i = 0; i < blogNodes.getLength(); i++) {
            Element blogElement = (Element) blogNodes.item(i);
            Blog blog = new Blog();
            blog.setId(blogElement.getAttribute("id"));
            blog.setName(blogElement.getAttribute("name"));
            blog.setNeedPassword("1".equals(blogElement.getAttribute("needspassword")));
            blog.setUrl(blogElement.getAttribute("url"));
            blogs.add(blog);
        }
        return blogs;
    }
}