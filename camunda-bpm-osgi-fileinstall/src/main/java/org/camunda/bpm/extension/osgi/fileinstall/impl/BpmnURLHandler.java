/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.osgi.fileinstall.impl;

import org.osgi.service.url.AbstractURLStreamHandlerService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A URL handler to transform a BPMN xml definition into an osgi bundle
 *
 * @author <a href="gnodet@gmail.com">Guillaume Nodet</a>
 */
public class BpmnURLHandler extends AbstractURLStreamHandlerService {

    private static Logger logger = Logger.getLogger(BpmnURLHandler.class.getName());

    private static String SYNTAX = "bpmn: bpmn-xml-uri";

    private URL bpmnXmlURL;

    /**
     * Open the connection for the given URL.
     *
     * @param url the url from which to open a connection.
     * @return a connection on the specified URL.
     * @throws IOException if an error occurs or if the URL is malformed.
     */
    @Override
    public URLConnection openConnection(URL url) throws IOException {
        if (url.getPath() == null || url.getPath().trim().length() == 0) {
            throw new MalformedURLException("Path can not be null or empty. Syntax: " + SYNTAX );
        }
        bpmnXmlURL = new URL(url.getPath());

        logger.log(Level.FINE, "BPMN xml URL is: [" + bpmnXmlURL + "]");
        return new Connection(url);
    }

    public URL getBpmnXmlURL() {
        return bpmnXmlURL;
    }

    public class Connection extends URLConnection {

        public Connection(URL url) {
            super(url);
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                new BpmnTransformer().transform(bpmnXmlURL, os);
                os.close();
                return new ByteArrayInputStream(os.toByteArray());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error opening spring xml url", e);
                throw (IOException) new IOException("Error opening spring xml url").initCause(e);
            }
        }
    }

}
