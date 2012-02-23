/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *  
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PonyBootstrapServlet extends HttpServlet {

    private static final long serialVersionUID = 6993633431616272739L;

    private static final Logger log = LoggerFactory.getLogger(PonyBootstrapServlet.class);

    private static final int BUFFER_SIZE = 4096;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        handlePonyResource(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        handlePonyResource(request, response);
    }

    private void handlePonyResource(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String contextPath = request.getContextPath();
        final String requestURI = request.getRequestURI();
        final String extraPathInfo = requestURI.replaceFirst(contextPath, "");
        if (extraPathInfo == null || extraPathInfo.isEmpty() || extraPathInfo.equals("/")) {
            log.info("Loading initial webpage ...");
            response.setContentType("text/html");
            handleRequest(request, response, "/index.html");
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Loading resource: " + extraPathInfo);
            }
            handleRequest(request, response, extraPathInfo);
        }
    }

    private void handleRequest(final HttpServletRequest request, final HttpServletResponse response, final String path) throws ServletException, IOException {
        // Try to load from webapp context
        InputStream inputStream = getServletContext().getResourceAsStream(path);
        String type;

        if (inputStream == null) {
            // Try to load from jar
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final String jarPath = path.substring(1, path.length());
            inputStream = classLoader.getResourceAsStream(jarPath);
            if (inputStream == null) {
                if (path.equals("/index.html")) {
                    inputStream = new ByteArrayInputStream(generateIndexPage());
                } else {
                    log.error("Failed to load resource: " + request.getPathInfo());
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }

            type = new MimetypesFileTypeMap().getContentType(new File(jarPath));
        } else {
            type = new MimetypesFileTypeMap().getContentType(new File(path));
        }

        response.setContentType(type);
        copy(inputStream, response.getOutputStream());
    }

    public static int copy(final InputStream in, final OutputStream out) throws IOException {
        try {
            int byteCount = 0;
            final byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {
            try {
                in.close();
            } catch (final IOException ex) {}
            try {
                out.close();
            } catch (final IOException ex) {}
        }
    }

    private byte[] generateIndexPage() {
        final StringBuilder builder = new StringBuilder();
        // <link href="css/sample.css" type="text/css" rel="stylesheet" />

        builder.append("<!doctype html>");
        builder.append("<html>");
        builder.append("<head>");
        builder.append("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
        builder.append("    <title>PonyTerminal</title>");
        builder.append("    <script type=\"text/javascript\" src=\"ponyterminal/ponyterminal.nocache.js\"></script>");
        builder.append("    <link rel=\"stylesheet/less\" type=\"text/css\" href=\"css/ponysdk.less\">");
        builder.append("    <script src=\"css/less.js\" type=\"text/javascript\"></script>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("    <iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>");
        builder.append("    <div id=\"loading\">loading pony application</div>");
        builder.append("    <noscript>");
        builder.append("        <div style=\"width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif\">");
        builder.append("            Your web browser must have JavaScript enabled");
        builder.append("            in order for this application to display correctly.");
        builder.append("        </div>");
        builder.append("    </noscript>");
        builder.append("</body>");
        builder.append("</html>");

        return builder.toString().getBytes();
    }
}
