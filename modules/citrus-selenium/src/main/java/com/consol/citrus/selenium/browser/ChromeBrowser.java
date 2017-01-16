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

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.consol.citrus.selenium.client.WebClientConfiguration;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class ChromeBrowser extends AbstractBrowser {

    /**
     * @param config WebClientConfiguration object.
     */
    public ChromeBrowser(WebClientConfiguration config) {
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
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createLocalWebDriver()
     */
    @Override
    protected WebDriver createLocalWebDriver() {
        return new ChromeDriver();
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createDesiredCapabilities(com.consol.citrus.selenium.client.WebClientConfiguration)
     */
    @Override
    protected DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config) {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return capabilities;
    }

}
