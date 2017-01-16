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
package com.consol.citrus.selenium.browser;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.consol.citrus.selenium.client.WaitForDocumentReady;
import com.consol.citrus.selenium.client.WebClientConfiguration;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class InternetExplorerBrowser extends AbstractBrowser {
    
    private static final String SCRIPT_ACCEPT_CERT = "var el = document.getElementById(\"overridelink\"); if(el) el.click();";
    
    /**
     * @param config WebClientConfiguration object.
     */
    public InternetExplorerBrowser(WebClientConfiguration config) {
        super(config);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createLocalWebDriver()
     */
    @Override
    protected WebDriver createLocalWebDriver() {
        return new InternetExplorerDriver();
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createDesiredCapabilities(com.consol.citrus.selenium.client.WebClientConfiguration)
     */
    @Override
    protected DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config) {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        return capabilities;
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#delay2()
     */
    @Override
    protected void delay2() {
        try {
            TimeUnit.MILLISECONDS.sleep(webClientConfiguration.getTickIE());
        } catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Sleep interrupted", e);
            }
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#waitForDocumentReady()
     */
    @Override
    protected void waitForDocumentReady() {
        try {
            /* wait until document is ready */
            WebDriverWait wdw = new WebDriverWait(webDriver, webClientConfiguration.getRetryTimeout());
            wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.SECONDS);
            wdw.until(WaitForDocumentReady.isReady(webDriver));
        } catch (TimeoutException e) {
            /*
             * failed to get the document ready state, give a warning about
             * it
             */
            logger.warn("Timeout while waiting for document.readyState", e);
        }
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#waitForPageLoad()
     */
    @Override
    protected void waitForPageLoad() {
        super.waitForPageLoad();
        
        /* handle certificate error message and accept certificate */
        tick();
        if (logger.isDebugEnabled()) {
            logger.debug("Handling Certificate Error message in IE (if any)");
        }
        ((JavascriptExecutor) webDriver).executeScript(SCRIPT_ACCEPT_CERT);
        
        super.waitForPageLoad();
    }
}
