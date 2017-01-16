/*
 * Copyright 2016 the original author or authors.
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
package com.consol.citrus.selenium.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import org.openqa.selenium.By;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Action to set input fields.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class SetInputAction extends AbstractWebAction {

    private Map<By, String> fields;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (fields == null) {
            logger.error(this.getName() + " called with invalid arguments");
            throw new CitrusRuntimeException("Action " + this.getName() + "called with invalid arguments");
        }

        for (Map.Entry<By, String> entry: fields.entrySet()) {
            By by = entry.getKey();
            
            String value = context.replaceDynamicContentInString(entry.getValue());
            if (value.startsWith("classpath:")) {
                Resource res = new PathMatchingResourcePatternResolver().getResource(value);
                URL resUrl;
                try {
                    resUrl = res.getURL();
                } catch (IOException ex) {
                    throw new CitrusRuntimeException("Failed to copy resource to temporary file", ex);
                }

                if (ResourceUtils.isJarURL(resUrl)) {
                    String tempFileName = res.getFilename();
                    String tempPrefix = StringUtils.stripFilenameExtension(tempFileName);
                    String tempExtension = "." + StringUtils.getFilenameExtension(tempFileName);
                    File temp = null;
                    try {
                        temp = File.createTempFile(tempPrefix, tempExtension);
                        temp.deleteOnExit();
                        Files.copy(res.getInputStream(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        throw new CitrusRuntimeException("Failed to copy resource to temporary file", ex);
                    }
                    value = temp.getAbsolutePath();
                } else if (ResourceUtils.isFileURL(resUrl)) {
                    try {
                        value = res.getFile().getAbsolutePath();
                    } catch (IOException ex) {
                        throw new CitrusRuntimeException("Failed to translate resource name to file name", ex);
                    }
                } else {
                    throw new CitrusRuntimeException("Unknown resource type: " + value);
                }
            }

            webClient.getBrowser().setInput(by, value);
        }
    }

    /**
     * @return The fields.
     */
    public Map<By, String> getFields() {
        return fields;
    }

    /**
     * @param fields The fields to be set.
     */
    public void setFields(Map<By, String> fields) {
        this.fields = fields;
    }

}
