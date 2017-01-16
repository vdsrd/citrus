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
package com.consol.citrus.selenium.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.citrus.selenium.browser.ChromeBrowser;
import com.consol.citrus.selenium.browser.EdgeBrowser;
import com.consol.citrus.selenium.browser.FirefoxBrowser;
import com.consol.citrus.selenium.browser.HtmlUnitBrowser;
import com.consol.citrus.selenium.browser.IBrowser;
import com.consol.citrus.selenium.browser.IBrowserActions;
import com.consol.citrus.selenium.browser.InternetExplorerBrowser;
import com.consol.citrus.selenium.browser.SafariBrowser;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class WebClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private WebClientConfiguration clientConfiguration;
    private IBrowser browser;

    /**
     * Default constructor using client configuration.
     * 
     * @param clientConfiguration
     */
    public WebClient(WebClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    /**
     * Default constructor initializing client configuration.
     */
    public WebClient() {
        this(new WebClientConfiguration());
    }

    /**
     * @return WebClient Configuration object set by Spring context.
     */
    public WebClientConfiguration getWebClientConfiguration() {
        return clientConfiguration;
    }
        
    /**
     * @return Browser actions.
     */
    public IBrowserActions getBrowserActions()
    {
        return (IBrowserActions)browser;
    }
    
    /**
     * @return Browser.
     */
    public IBrowser getBrowser()
    {
        return browser;
    }

    /**
     * Starting the WebClient.
     */
    public void start() {
        this.browser = createBrowser();
    }

    /**
     * Stop the web driver
     */
    public void stop() {
        if (browser != null) {
            browser.stop();
        }
    }
    
    private IBrowser createBrowser() {
        
        if (logger.isDebugEnabled()) {
            logger.debug("We are opening a web browser of type {}", clientConfiguration.getBrowserType());
        }
        
        switch (clientConfiguration.getBrowserType()) {
            case FIREFOX:
                return new FirefoxBrowser(clientConfiguration);
            case CHROME:
                return new ChromeBrowser(clientConfiguration);
            case EDGE:
                return new EdgeBrowser(clientConfiguration);
            case INTERNET_EXPLORER:
                return new InternetExplorerBrowser(clientConfiguration);
            case SAFARI:
                return new SafariBrowser(clientConfiguration);
            case HTML_UNIT:
            default:
                return new HtmlUnitBrowser(clientConfiguration);
        }
    }
}
