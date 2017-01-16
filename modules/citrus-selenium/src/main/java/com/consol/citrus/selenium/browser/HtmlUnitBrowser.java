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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.consol.citrus.selenium.client.WebClientConfiguration;
import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class HtmlUnitBrowser extends AbstractBrowser {

    /**
     * @param config WebClientConfiguration object.
     */
    public HtmlUnitBrowser(WebClientConfiguration config) {
        super(config);
        webDriver.manage().timeouts().implicitlyWait(config.getHtmlUnitTimeout(), TimeUnit.SECONDS);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#getElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement getElement(By by) {
        return internalFindElementBy(by, 1, webClientConfiguration.getHtmlUnitTimeout());
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#findVisibleElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findVisibleElement(By by) {
        WebElement element = findVisibleElement(by, webClientConfiguration.getMaxRetries(), webClientConfiguration.getHtmlUnitTimeout());
        if (element == null) {
            handleElementError(by);
        }
        return element;
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#takeScreenshot(java.lang.String, java.lang.String)
     */
    @Override
    public void takeScreenshot(String testcaseName, String testcaseStep) {
        /* HTML unit driver does not support taking screenshots (but instead we store a page-source) */
        writeHTMLDataToFile(webDriver.getPageSource(), testcaseName, testcaseStep);
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#acceptAlertBox()
     */
    @Override
    public String acceptAlertBox() {
        //no alert box supported for HTML Unit Driver.
        return null;
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#cancelAlertBox()
     */
    @Override
    public String cancelAlertBox() {
        //no alert box supported for HTML Unit Driver.
        return null;
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#setInput(org.openqa.selenium.By, java.lang.String)
     */
    @Override
    public void setInput(By by, String value) {
        performSetInput(by, value, findElement(by));
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createLocalWebDriver()
     */
    @Override
    protected WebDriver createLocalWebDriver() {
        return new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createDesiredCapabilities(com.consol.citrus.selenium.client.WebClientConfiguration)
     */
    @Override
    protected DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config) {
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return capabilities;
    }

}
