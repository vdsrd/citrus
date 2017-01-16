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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.selenium.client.WaitForDocumentReady;
import com.consol.citrus.selenium.client.WebClientConfiguration;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class EdgeBrowser extends AbstractBrowser {

    private static final String SCRIPT_ACCEPT_CERT = "var el = document.getElementById(\"invalidcert_continue\"); if(el) el.click(); ";
    
    /**
     * @param config WebClientConfiguration object.
     */
    public EdgeBrowser(WebClientConfiguration config) {
        super(config);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#click(org.openqa.selenium.By, boolean)
     */
    @Override
    public void click(By by, boolean isClosing) {
        WebElement element = findElement(by);
        
        //first scroll down
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        
        super.performClick(by, isClosing, element);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#selectItem(org.openqa.selenium.By, java.lang.String)
     */
    @Override
    public void selectItem(By by, String item) {
    
        WebElement element = findElement(by);
    
        if (logger.isDebugEnabled()) {
            logger.debug("Select item '" + item + "' by " + by.toString());
        }
    
        element.sendKeys(item);
        element.click();
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#getCookieValue(java.lang.String)
     */
    @Override
    public String getCookieValue(String cookie) {
        //Prevent unknown error from WebDriverException when running on Edge
        ((JavascriptExecutor) webDriver).executeScript("return document.cookie;");
        
        return super.getCookieValue(cookie);
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#closeWindow()
     */
    @Override
    public void closeWindow() {
        String curHandle = webDriver.getWindowHandle();
        Set<String> handles = webDriver.getWindowHandles();
        handles.remove(curHandle);

        if (logger.isDebugEnabled()) {
            logger.debug("We are at handle " + curHandle);
        }

        List<String> list = new ArrayList<>(handles);
        String nextHandle = list.get(0);
        if (logger.isDebugEnabled()) {
            logger.debug("Returning to handle " + nextHandle);
        }

        try {
            webDriver.switchTo().window(nextHandle);
        } catch (NoSuchWindowException e) {
            throw new CitrusRuntimeException(e);
        }

        curHandle = webDriver.getWindowHandle();
        if (logger.isDebugEnabled()) {
            logger.debug("switch to prevous tab: " + curHandle);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createLocalWebDriver()
     */
    @Override
    protected WebDriver createLocalWebDriver() {
        return new EdgeDriver();
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createDesiredCapabilities(com.consol.citrus.selenium.client.WebClientConfiguration)
     */
    @Override
    protected DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config) {
        DesiredCapabilities capabilities = DesiredCapabilities.edge();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, false);
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "none");
        return capabilities;
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#tick()
     */
    @Override
    protected void tick() {
        /*
         * Edge specific; findElement: The element could already have been
         * expired, or it may still be present while the DOM is reloading and
         * findElement is returning a stale element
         */
        try {
            TimeUnit.MILLISECONDS.sleep(webClientConfiguration.getTickEdge());
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
            logger.debug("Handling Certificate Error message in Edge (if any)");
        }
        ((JavascriptExecutor) webDriver).executeScript(SCRIPT_ACCEPT_CERT);
        
        super.waitForPageLoad();
    }
}
