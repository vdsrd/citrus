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

import org.springframework.util.StringUtils;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * Action to start the web client.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class StartAction extends AbstractWebAction {

    private String url;
    
    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting the WebClient.");
        }
        
        String confUrl = context.replaceDynamicContentInString(url);
        if (StringUtils.isEmpty(confUrl)) {
            logger.error("No required 'url' available");
            throw new CitrusRuntimeException("No required 'url' available");
        }

        webClient.getWebClientConfiguration().updateConfiguration(context, confUrl);
                
        webClient.start();
        
        webClient.getBrowser().navigateTo(webClient.getWebClientConfiguration().getBaseUrl());
    }
        
    /**
     * @return URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url Set the URL.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
}
