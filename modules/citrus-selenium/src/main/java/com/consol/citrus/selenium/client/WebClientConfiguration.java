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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.model.config.selenium.BrowserTypeEnum;

/**
 * Configuration for the Web client.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class WebClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String SEPARATOR = "/";
    
    private static final String CFG_SELENIUM_SERVER_URL = "selenium.server.url";
    private static final String CFG_SELENIUM_PLATFORM = "selenium.platform";
    private static final String CFG_SELENIUM_SCREENSHOT_STORAGE_LOCATION = "selenium.screenshot.location";
    private static final String CFG_SELENIUM_TIMEOUT_PAGELOAD = "selenium.timeout.pageload";
    private static final String CFG_SELENIUM_TIMEOUT_IMPLICIT = "selenium.timeout.implicit";
    private static final String CFG_SELENIUM_POLL_EVERY = "selenium.poll.every";
    private static final String CFG_SELENIUM_RETRIES_MAX = "selenium.retries.max";
    private static final String CFG_SELENIUM_RETRIES_TIMEOUT = "selenium.retries.timeout";
    private static final String CFG_SELENIUM_HTMLUNIT_DRIVER_TIMEOUT = "htmlunit.timeout";
    private static final String CFG_SELENIUM_EDGE_DELAY = "edge.delay";
    private static final String CFG_SELENIUM_IE_DELAY = "internetexplorer.delay";
    
    /* Set the amount of time to wait for a page load to complete before throwing a timeout. */
    private int pageloadTimeout = 60;
    /* set the amount of time to wait for an element when it cannot be found */
    private int implicitWait = 15;
    /* how many times the findElement method tries again to find the element */
    private int maxRetries = 3;
    /* the timeout per retry */
    private int retryTimeout = 6;
    /* override the selenium.timeout.implicit for htmlunit only */
    private int htmlUnitTimeout = 15;
    /* Edge click/goto_url action delay in milliseconds to prevent StaleElementReferenceExceptions. */
    private int tickEdge = 1000;
    /* Internet Explorer delay in milliseconds to ensure all window handles are present. */
    private int tickIE = 2000;
    /* poll every N seconds */
    private int pollInterval = 3;

    
    /* The default WebDriver is HtmlUnit. */
    private BrowserTypeEnum browserType = BrowserTypeEnum.HTML_UNIT;
    private String startUrl;
    private URL seleniumServerURL;
    private Platform platform;
    private String screenshotLocation;
    private String baseURL = "";
    private String citrusTestName = "unknown";
    private String citrusTestPackage;
    private Map<String, Object> contextVariables;
    

    /**
     * Gets the browserType.
     *
     * @return the browserType
     */
    public BrowserTypeEnum getBrowserType() {
        return browserType;
    }

    /**
     * Sets the browserType.
     *
     * @param browser
     */
    public void setBrowserType(String browser) {
        this.browserType = BrowserTypeEnum.fromValue(browser);
    }

    /**
     * @return the start URL.
     */
    public String getStartUrl() {
        return startUrl;
    }

    /**
     * @param url Set the start URL.
     */
    public void setStartUrl(String url) {
        this.startUrl = url;
    }
    
    /**
     * @param seleniumServerUrl Selenium Server URL to use.
     */
    public void setSeleniumServerUrl(String seleniumServerUrl) {
        try {
            seleniumServerURL = new URL(seleniumServerUrl);
        } catch (MalformedURLException e) {
            logger.error("Invalid Selenium URL value: " + seleniumServerUrl, e);
            throw new CitrusRuntimeException("Invalid URL value supplied");
        }
    }
    
    /**
     * @return Selenium Server URL or NULL
     */
    public URL getSeleniumServerUrl() {
        return seleniumServerURL;
    }
    
    /**
     * @return Selenium platform or NULL.
     */
    public Platform getSeleniumPlatform() {
        return platform;
    }
    
    /**
     * @param platformValue Selenium Platform name
     * @throws CitrusRuntimeException if platform is not valid.
     */
    public void setSeleniumPlatform(String platformValue) {
        try {
            this.platform = Platform.fromString(platformValue);
            logger.info("Using Platform: " + this.platform.toString());
        } catch (WebDriverException e) {
            logger.error("Invalid platform value: " + platformValue, e);
            throw new CitrusRuntimeException("Invalid platform value supplied");
        }
    }
    
    /**
     * @return Screenshot location.
     */
    public String getScreenshotLocation() {
        return screenshotLocation;
    }
    
    /**
     * @param location The screenshot location to use.
     */
    public void setScreenshotLocation(String location) {
        screenshotLocation = location;
    }
    
    /**
     * @return The base URL for the test case.
     */
    public String getBaseUrl() {
        return baseURL;
    }
    
    /**
     * @param url The base URL to be used for the test case.
     */
    public void setBaseUrl(String url) {
        baseURL = url;
    }
    
    /**
     * @return the pageloadTimeout
     */
    public int getPageloadTimeout() {
        return pageloadTimeout;
    }

    /**
     * @param pageLoadTimeoutCfg the pageloadTimeout to set
     */
    public void setPageloadTimeout(String pageLoadTimeoutCfg) {
        pageloadTimeout = toInt(pageLoadTimeoutCfg, pageloadTimeout);
    }

    /**
     * @return the implicitWait
     */
    public int getImplicitWait() {
        return implicitWait;
    }

    /**
     * @param implicitWaitCfg the implicitWait to set in seconds
     */
    public void setImplicitWait(String implicitWaitCfg) {
        implicitWait = toInt(implicitWaitCfg, implicitWait);
    }

    /**
     * @return the maxRetries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * @param maxRetriesCfg the maxRetries to set
     */
    public void setMaxRetries(String maxRetriesCfg) {
        maxRetries = toInt(maxRetriesCfg, maxRetries);
    }

    /**
     * @return the retryTimeout
     */
    public int getRetryTimeout() {
        return retryTimeout;
    }

    /**
     * @param retryTimeoutCfg the retryTimeout to set in seconds
     */
    public void setRetryTimeout(String retryTimeoutCfg) {
        retryTimeout = toInt(retryTimeoutCfg, retryTimeout);
    }

    /**
     * @return the htmlUnitTimeout
     */
    public int getHtmlUnitTimeout() {
        return htmlUnitTimeout;
    }

    /**
     * @param htmlUnitTimeoutCfg the htmlUnitTimeout to set in seconds
     */
    public void setHtmlUnitTimeout(String htmlUnitTimeoutCfg) {
        htmlUnitTimeout = toInt(htmlUnitTimeoutCfg, htmlUnitTimeout);
    }

    /**
     * @return the tickEdge
     */
    public int getTickEdge() {
        return tickEdge;
    }

    /**
     * @param edgeTickDelayCfg the tickEdge to set in milliseconds.
     */
    public void setTickEdge(String edgeTickDelayCfg) {
        tickEdge = toInt(edgeTickDelayCfg, tickEdge);
    }

    /**
     * @return the tickIE
     */
    public int getTickIE() {
        return tickIE;
    }

    /**
     * @param ieDelayCfg the tickIE to set in milliseconds.
     */
    public void setTickIE(String ieDelayCfg) {
        tickIE = toInt(ieDelayCfg, tickIE);
    }

    /**
     * @return the pollInterval
     */
    public int getPollInterval() {
        return pollInterval;
    }

    /**
     * @param pollEveryCfg the pollInterval to set in seconds
     */
    public void setPollInterval(String pollEveryCfg) {
        pollInterval = toInt(pollEveryCfg, pollInterval);
    }
    
    /**
     * @return the citrusTestName
     */
    public String getCitrusTestName() {
        return citrusTestName;
    }
    
    /**
     * @return the citrusTestPackage
     */
    public String getCitrusTestPackage() {
        return citrusTestPackage;
    }

    /**
     * Using dynamic configuration.
     * @param context TestContext containing the dynamic configuration as variables.
     * @param startURL Start URL.
     */
    public void updateConfiguration(TestContext context, String startURL) {
        
        contextVariables = context.getVariables();

        baseURL = resolveBaseUrl(startURL);
        logger.info("Using base URL: " + this.baseURL);
        
        configureTimeouts(contextVariables);
        
        String seleniumServerURLCfg = (String)contextVariables.get(CFG_SELENIUM_SERVER_URL);
        if (!StringUtils.isEmpty(seleniumServerURLCfg)) {
            logger.info("Using config param '{}' with value: {}", CFG_SELENIUM_SERVER_URL, seleniumServerURLCfg);
            setSeleniumServerUrl(seleniumServerURLCfg);
        }

        String platformValue = (String)contextVariables.get(CFG_SELENIUM_PLATFORM);
        if (!StringUtils.isEmpty(platformValue)) {
            logger.info("Using config param '{}' with value: {}", CFG_SELENIUM_PLATFORM, platformValue);
            setSeleniumPlatform(platformValue);
        }
        
        citrusTestName = (String) contextVariables.get(Citrus.TEST_NAME_VARIABLE);
        logger.info("Using {}: {}", Citrus.TEST_NAME_VARIABLE, citrusTestName);
        
        citrusTestPackage = (String) contextVariables.get(Citrus.TEST_PACKAGE_VARIABLE);
        logger.info("Using {}: {}", Citrus.TEST_PACKAGE_VARIABLE, citrusTestPackage);
        
        setScreenshotLocation(createScreenshotLocation((String) contextVariables.get(CFG_SELENIUM_SCREENSHOT_STORAGE_LOCATION)));
        logger.info("Using {}: {}", CFG_SELENIUM_SCREENSHOT_STORAGE_LOCATION, screenshotLocation);
    }
    
    /**
     * @param name Configuration item name
     * @return The value as String.
     */
    public String getConfigurationItem(String name) {
        return (String)contextVariables.get(name);
    }

    /**
     * @return Context Variables.
     */
    public Map<String, Object> getContextVariables() {
        return Collections.unmodifiableMap(contextVariables);
    }
    
    private String createScreenshotLocation(String screenshotStorageLocation) {
        String location = screenshotStorageLocation;
        if (StringUtils.isEmpty(location)) {
            location = citrusTestPackage;
        }
        if (!location.endsWith(File.separator)) {
            location += File.separator;
        }
        return location;
    }
    
    private String resolveBaseUrl(String startURL)
    {
        try {
            new URL(startURL);

            /*
             * the value is a fully specified URL, i.e
             * webadmin.base.url=https://localhost:8443, then set
             * baseURL=pageUrl
             * 
             * <web:start url="${webadmin.base.url}">
             * 
             */

            return createBaseURL(startURL);
            
        } catch (MalformedURLException e) {
            /*
             * if the value is not a fully specified URL, i.e
             * selfmgmt.url=selfmgmt, then we reach this part.
             */

            /*
             * <web:start url="${selfmgmt.url}"/>
             * 
             * the base URL should be: start-url + url
             * 
             * url = selfmgmt
             * 
             * start-url = (userwebsites.base.url=http://localhost:8080)
             * 
             * baseURL = ${start-url}/${selfmgmt}
             */
            return createBaseURLWithPath(startURL);
        }
    }
    
    /**
     * Construct a base URL that is made out of the following parts: - Start URL
     * - optional Path separator - Web application path
     * 
     * This baseURL is then used by navigateTo to construct a fully qualified
     * URL
     * 
     * @param webApplicationPath application context path
     */
    private String createBaseURLWithPath(String webApplicationPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(startUrl);
        if (!startUrl.endsWith(SEPARATOR) && !webApplicationPath.startsWith(SEPARATOR)) {
            sb.append(SEPARATOR);
        }
        sb.append(webApplicationPath);

        return createBaseURL(sb.toString());
    }

    /**
     * Construct a base URL.
     * 
     * This method overwrites the value passed in as start-url
     *
     * @see clientConfiguration.getStartUrl()
     * @param url Fully qualified URL
     */
    private String createBaseURL(String configUrl) {
        if (!configUrl.endsWith(SEPARATOR)) {
            return configUrl + SEPARATOR;
        }
        return configUrl;
    }
    
    private int toInt(String value, final int defaultValue) {
        if (!StringUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Value is not a number: " + value, e);
            }
        }
        return defaultValue;
    }

    private void configureTimeouts(Map<String, Object> citrusVariables) {
        setPageloadTimeout((String)citrusVariables.get(CFG_SELENIUM_TIMEOUT_PAGELOAD));
        logger.info(CFG_SELENIUM_TIMEOUT_PAGELOAD + " is set to " + pageloadTimeout + " seconds");
        
        setImplicitWait((String) citrusVariables.get(CFG_SELENIUM_TIMEOUT_IMPLICIT));
        logger.info(CFG_SELENIUM_TIMEOUT_IMPLICIT + " is set to " + implicitWait + " seconds");
        
        setMaxRetries((String) citrusVariables.get(CFG_SELENIUM_RETRIES_MAX));
        logger.info(CFG_SELENIUM_RETRIES_MAX + " is set to " + maxRetries + " retries");
        
        setRetryTimeout((String) citrusVariables.get(CFG_SELENIUM_RETRIES_TIMEOUT));
        logger.info(CFG_SELENIUM_RETRIES_TIMEOUT + " is set to " + retryTimeout + " seconds");

        setPollInterval((String) citrusVariables.get(CFG_SELENIUM_POLL_EVERY));
        logger.info(CFG_SELENIUM_POLL_EVERY + " is set to " + pollInterval + " seconds");
        
        setHtmlUnitTimeout((String) citrusVariables.get(CFG_SELENIUM_HTMLUNIT_DRIVER_TIMEOUT));
        logger.info(CFG_SELENIUM_HTMLUNIT_DRIVER_TIMEOUT + " is set to " + htmlUnitTimeout + " seconds");
        
        setTickEdge((String) citrusVariables.get(CFG_SELENIUM_EDGE_DELAY));
        logger.info(CFG_SELENIUM_EDGE_DELAY + " is set to " + tickEdge + " milliseconds");
        
        setTickIE((String) citrusVariables.get(CFG_SELENIUM_IE_DELAY));
        logger.info(CFG_SELENIUM_IE_DELAY + " is set to " + tickIE + " milliseconds");
    }

}
