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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.selenium.client.AlertIsNotPresent;
import com.consol.citrus.selenium.client.WebClientConfiguration;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public abstract class AbstractBrowser implements IBrowser, IBrowserActions {
    
    protected WebDriver webDriver;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected WebClientConfiguration webClientConfiguration;

    /**
     * Default constructor using client configuration.
     * @param clientConfiguration WebClientConfiguration objects.
     */
    public AbstractBrowser(WebClientConfiguration clientConfiguration) {
        this.webClientConfiguration = clientConfiguration;
            
        if (clientConfiguration.getSeleniumServerUrl() != null) {
                webDriver = createRemoteWebDriver(clientConfiguration);
        } else {
                webDriver = createLocalWebDriver();
        }
        
        webDriver.manage().timeouts().implicitlyWait(clientConfiguration.getImplicitWait(), TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(clientConfiguration.getPageloadTimeout(), TimeUnit.SECONDS);
            
        webDriver.manage().window().maximize();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#acceptAlertBox()
     */
    @Override
    public String acceptAlertBox() {
        String message = null;
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, 5);
            wait.until(ExpectedConditions.alertIsPresent());

            Alert alert = webDriver.switchTo().alert();
            message = alert.getText();
            alert.accept();
            
            if (logger.isDebugEnabled()) {
                logger.debug("Alert message: " + message);
            }

            // ensure that alert will be gone
            verifyAlertBoxIsAbsent();
        } catch (NoAlertPresentException e) {
            logger.error("Cannot accept an unavalable alert dialog");
            throw new CitrusRuntimeException("No alert dialog present to accept", e);
        }
        return message;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#cancelAlertBox()
     */
    @Override
    public String cancelAlertBox() {
        String message = null;
        try {
            Alert alert = webDriver.switchTo().alert();
            message = alert.getText();
            alert.dismiss();
        } catch (NoAlertPresentException e) {
            logger.error("Cannot accept an unavalable alert dialog");
            throw new CitrusRuntimeException("No alert dialog present to accept", e);
        }
        return message;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#verifyAlertBoxIsPresent()
     */
    @Override
    public void verifyAlertBoxIsPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, 5);
            wait.until(ExpectedConditions.alertIsPresent());
            if (logger.isDebugEnabled()) {
                logger.debug("Alertbox dialog is present");
            }
        } catch (TimeoutException | NoAlertPresentException e) {
            throw new CitrusRuntimeException("Alert dialog not present", e);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#verifyAlertBoxIsAbsent()
     */
    @Override
    public void verifyAlertBoxIsAbsent() {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, webClientConfiguration.getRetryTimeout());
            wait.until(new AlertIsNotPresent().alertIsNotPresent(webDriver));
        } catch (TimeoutException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Alter box should have been closed within timeout period (s): " + webClientConfiguration.getRetryTimeout(), e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Alertbox is still present!");
            }
            throw new CitrusRuntimeException("Alertbox expected to be absent but is present");
        }
        // the alertbox is not present (as expected)
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#clearBrowserCache()
     */
    @Override
    public void clearBrowserCache() {
        webDriver.manage().deleteAllCookies();
        if (logger.isDebugEnabled()) {
            logger.debug("Cleared all cookies");
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#click(org.openqa.selenium.By, boolean)
     */
    @Override
    public void click(By by, boolean isClosing) {
        performClick(by, isClosing, findElement(by));
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#findElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findElement(By by) {
        WebElement element = internalFindElementBy(by, webClientConfiguration.getMaxRetries(), webClientConfiguration.getRetryTimeout());
        if (element == null) {
            handleElementError(by);
        }
        return element;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement getElement(By by) {
        return internalFindElementBy(by, 1, webClientConfiguration.getRetryTimeout());
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#findElements(org.openqa.selenium.By)
     */
    @Override
    public List<WebElement> findElements(By by) {
        if (logger.isDebugEnabled()) {
            logger.debug("Find elements " + by.toString());
        }

        List<WebElement> elements = webDriver.findElements(by);
        if (elements == null) {
            handleElementError(by);
        }
        return elements;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getCurrentUrl()
     */
    @Override
    public String getCurrentUrl() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get current browser url: " + webDriver.getCurrentUrl());
        }
        return webDriver.getCurrentUrl();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getPageSource()
     */
    @Override
    public String getPageSource() {
        return webDriver.getPageSource();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getPageTitle()
     */
    @Override
    public String getPageTitle() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get page title: " + webDriver.getTitle());
        }
        return webDriver.getTitle();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#navigateBack()
     */
    @Override
    public void navigateBack() {
        webDriver.navigate().back();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#navigateForward()
     */
    @Override
    public void navigateForward() {
        webDriver.navigate().forward();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#navigateTo(java.lang.String)
     */
    @Override
    public void navigateTo(String pageUrl) {
        URL url = null;

        try {
            url = new URL(pageUrl);

            if (logger.isDebugEnabled()) {
                logger.debug("navigate to: " + url.toString() + " in window " + webDriver.getWindowHandle());
            }

            performNavigateTo(url);
        } catch (MalformedURLException ex) {
            try {
                url = new URL(webClientConfiguration.getBaseUrl() + pageUrl);
            } catch (MalformedURLException e) {
                logger.error("Invalid URL: " + webClientConfiguration.getBaseUrl() + pageUrl, e);
                throw new CitrusRuntimeException(e);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("navigate to: " + url.toString());
            }

            performNavigateTo(url);
        }

        waitForPageLoad();

        tick();
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#newWindow()
     */
    @Override
    public void newWindow() {
        String curHandle = webDriver.getWindowHandle();
        int curNumWindows = webDriver.getWindowHandles().size();

        if (logger.isDebugEnabled()) {
            logger.debug("nextTab, there are currently " + curNumWindows + " window handles open. Current handle is "
                + curHandle + " (url=" + webDriver.getCurrentUrl() + ")");
        }

        ((JavascriptExecutor) webDriver).executeScript("window.open(\"\");");

        int expectedNumberOfWindows = curNumWindows + 1;

        WebDriverWait wdw = new WebDriverWait(webDriver, webClientConfiguration.getRetryTimeout());
        wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.SECONDS);
        wdw.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));

        Set<String> handles = webDriver.getWindowHandles();
        if (logger.isDebugEnabled()) {
            logger.debug("nextTab, there are now " + handles.size() + " window handles open.");
        }
        handles.remove(curHandle);

        List<String> list = new ArrayList<>(handles);

        int lastWindowNum = list.size() - 1;

        String nextHandle = list.get(lastWindowNum);

        try {
            webDriver.switchTo().window(nextHandle);
        } catch (NoSuchWindowException e) {
            throw new CitrusRuntimeException(e);
        }

        curHandle = webDriver.getWindowHandle();
        if (logger.isDebugEnabled()) {
            logger.debug("nextTab, current window handle is " + curHandle);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#closeWindow()
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
        
        webDriver.close();
        
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
     * @see com.consol.citrus.selenium.browser.IBrowser#switchWindow(java.lang.String, int)
     */
    @Override
    public void switchWindow(String urlpart, int expectedNumberOfWindows) {
        if (logger.isDebugEnabled()) {
            logger.debug("Requesting window with url '" + urlpart + "'");
        }

        delay(); /*
                  * do not enter the poll too fast, a window might still be
                  * closing
                  */

        WebDriverWait wdw = new WebDriverWait(webDriver, webClientConfiguration.getImplicitWait());
        wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.SECONDS);
        wdw.withMessage("Number of open windows do not match");
        wdw.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));

        /* now we have the expected number of windows */

        Set<String> handles = getWindowHandles();
        Iterator<String> iterator = handles.iterator();

        boolean found = false;

        while (iterator.hasNext()) {
            String subWindowHandler = iterator.next();
            try {
                /*
                 * switch to a window handle and look at the page url to see if
                 * this is the correct window
                 */
                webDriver.switchTo().window(subWindowHandler);
                delay2();
                if (logger.isDebugEnabled()) {
                    logger.debug("Switched to window handle " + subWindowHandler + " , url=" + webDriver.getCurrentUrl());
                }

                if (webDriver.getCurrentUrl().contains(urlpart)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found window: " + webDriver.getCurrentUrl());
                    }
                    found = true;
                    /* we have found the window */
                    break;
                }

            } catch (NoSuchWindowException e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("No such window handle: " + subWindowHandler, e);
                }
                
                /*
                 * in case the window we access does not exist anymore, only
                 * warn about it
                 */
                logger.warn("No such window handle " + subWindowHandler);
            }
        }

        /* throw an error if the window cannot be found */
        if (!found) {
            logger.error("No such window '" + urlpart + "'");
            throw new CitrusRuntimeException("No such window '" + urlpart + "'");
        }

    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#gotoFrame(java.lang.String)
     */
    @Override
    public void gotoFrame(String frame) {

        delay2();

        if (logger.isDebugEnabled()) {
            logger.debug("Goto frame: " + frame + " in window " + webDriver.getWindowHandle());
        }
        WebDriverWait wdw = new WebDriverWait(webDriver, webClientConfiguration.getImplicitWait());
        wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.SECONDS);
        wdw.ignoring(StaleElementReferenceException.class);
        wdw.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));

        // After frame is available and switched to it... switch to it again!
        // Otherwise the test at cfg001 will fail :-(
        try {
            webDriver.switchTo().parentFrame().switchTo().frame(frame);
            if (logger.isDebugEnabled()) {
                logger.debug("Switched to frame: '" + frame + "'");
            }
        } catch (NoSuchFrameException e) {
            logger.error("No such frame " + frame, e);
            throw new CitrusRuntimeException("No such frame: " + frame, e);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#refresh()
     */
    @Override
    public void refresh() {
        webDriver.navigate().refresh();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#selectItem(org.openqa.selenium.By, java.lang.String)
     */
    @Override
    public void selectItem(By by, String item) {

        WebElement element = findElement(by);

        if (logger.isDebugEnabled()) {
            logger.debug("Select item '" + item + "' by " + by.toString());
        }

        Select dropdown = new Select(element);
        dropdown.selectByValue(item);
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#hover(org.openqa.selenium.By)
     */
    @Override
    public void hover(By by) {
        if (logger.isDebugEnabled()) {
            logger.debug("Hover over element " + by.toString());
        }
        Actions builder = new Actions(webDriver);
        builder.moveToElement(findElement(by)).perform();
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#setInput(org.openqa.selenium.By, java.lang.String)
     */
    @Override
    public void setInput(By by, String value) {
        WebElement el = findElement(by);
        
        String inputValue = value;
        
        if (!"file".equals(el.getAttribute("type")))
            inputValue += "\t"; /* fire keyboard event by pressing TAB */
        
        performSetInput(by, inputValue, el);
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#stop()
     */
    @Override
    public void stop() {
        if (webDriver == null) {
            logger.warn("There is no web browser. Nothing will be stopped.");
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Stopping the WebClient");
            }
            closeWebDriver();
            webDriver = null;
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#validate(org.openqa.selenium.By, java.lang.String)
     */
    @Override
    public void validate(final By by, String expected) {
        String actual = null;
        WebElement foundElement = getElement(by);

        /* the element cannot be found and expected value is empty */
        if (foundElement == null && !StringUtils.hasText(expected)) {
            /* validationRule is set to EMPTY if expected is empty or null */
            validate(actual, expected);

            /*
             * and defaults to EQUALS when expected is set and no validationRule
             * is given, so continue
             */
        }

        /* the element is found and expected value is not empty */
        if (StringUtils.hasText(expected) && foundElement != null) {
            String tagName = foundElement.getTagName().toLowerCase();

            if ("select".equals(tagName)) {
                actual = getOptionsInSelect(foundElement);
                if (logger.isDebugEnabled()) {
                    logger.debug("validate tag '" + tagName + "' option list: " + actual);
                }
            } else {
                actual = findElement(by).getText();
                if (logger.isDebugEnabled()) {
                    logger.debug("validate tag '" + tagName + "' text: '" + actual + "'");
                }
            }

            if (actual != null) {
                /*
                 * ensure that actual does not have any leading or trailing
                 * whitespace (stated in Javadoc, but behaves differently on
                 * Edge)
                 */
                actual = actual.trim();
            }

            if (actual == null) {
                logger.warn("Value of element '" + by.toString() + " is not defined");
            }

            validate(actual, expected);
        } else {
            /* element is found but expected value is empty */
            /*
             * use value="@web:empty@" instead of value="" to test for an
             * existing element with no value
             */
            if (foundElement != null) {
                logger.error(
                        "Found unexpected element " + by + " (expected='" + expected + "',actual='" + actual + "') ");
                takeScreenshot();
                throw new CitrusRuntimeException(
                        "Found unexpected element " + by + " (expected= '" + expected + "', actual='" + actual + "')");
            }

        }

        /* the element is not found but a value was expected */
        if (foundElement == null && (expected != null && !expected.isEmpty())) {
            logger.error("Element {} not found but expected to find value '{}'", by, expected);
            takeScreenshot();
            throw new CitrusRuntimeException(
                    "Element " + by + " not found but expected to find value '" + expected + "'");
        }

    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getOptionsInSelect(org.openqa.selenium.WebElement)
     */
    @Override
    public String getOptionsInSelect(WebElement element) {
        Select dropdown = new Select(element);
        List<WebElement> options = dropdown.getOptions();
        List<String> optionValues = new ArrayList<>();
        for (WebElement option : options) {
            optionValues.add(option.getText());
        }
        return StringUtils.collectionToCommaDelimitedString(optionValues);
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#verifyPageURL(java.lang.String)
     */
    @Override
    public void verifyPageURL(String urlPath) {
        String absoluteURL = webClientConfiguration.getBaseUrl() + urlPath;
        if (!webDriver.getCurrentUrl().equals(absoluteURL)) {
            StringBuilder buildError = new StringBuilder("Expected page is <").append(absoluteURL)
                    .append("> but found ").append(webDriver.getCurrentUrl());
            String errorMessage = buildError.toString();
            logger.error(errorMessage);
            throw new CitrusRuntimeException(errorMessage);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#getCookieValue(java.lang.String)
     */
    @Override
    public String getCookieValue(String cookie) {

        Set<Cookie> cookies = webDriver.manage().getCookies();

        if (cookies.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("There are no cookies");
            }
            return null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Fetch cookie '" + cookie + "'");
        }

        Iterator<Cookie> it = cookies.iterator();
        while (it.hasNext()) {
            Cookie c = it.next();
            if (logger.isDebugEnabled()) {
                logger.debug("Cookie '" + c.getName() + "' = '" + c.getValue() + "'");
            }

            if (c.getName().equals(cookie)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Get Cookie '" + cookie + "' with value '" + c.getValue() + "'");
                }
                return c.getValue();
            }
        }

        return null;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#setCookie(java.lang.String, java.lang.String)
     */
    @Override
    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        webDriver.manage().addCookie(cookie);
        if (logger.isDebugEnabled()) {
            logger.debug("Add Cookie '" + name + "' with value '" + value + "'");
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#delCookie(java.lang.String)
     */
    @Override
    public void delCookie(String name) {
        webDriver.manage().deleteCookieNamed(name);
        if (logger.isDebugEnabled()) {
            logger.debug("Delete Cookie '" + name + "'");
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#findVisibleElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findVisibleElement(By by) {
        WebElement element = findVisibleElement(by, webClientConfiguration.getMaxRetries(), webClientConfiguration.getRetryTimeout());
        if (element == null) {
            handleElementError(by);
        }
        return element;
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowser#takeScreenshot(java.lang.String, java.lang.String)
     */
    @Override
    public void takeScreenshot(String testcaseName, String testcaseStep) {
        if (webDriver instanceof TakesScreenshot) {
            OutputType<byte[]> target = OutputType.BYTES;
            writePNGDataToFile(((TakesScreenshot) webDriver).getScreenshotAs(target), testcaseName, testcaseStep);
        }
    }

    /**
     * @see com.consol.citrus.selenium.browser.IBrowserActions#takeScreenshot()
     */
    @Override
    public void takeScreenshot() {
        takeScreenshot(webClientConfiguration.getCitrusTestName(), String.valueOf(System.currentTimeMillis()));
    }

    /**
     * @param e a webdriver exception
     */
    protected void extractScreenshot(WebDriverException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ScreenshotException) {
            String base64EncScreenshot = ((ScreenshotException)cause).getBase64EncodedScreenshot();
            if (base64EncScreenshot != null) {
                writePNGDataToFile(Base64.decodeBase64(base64EncScreenshot), webClientConfiguration.getCitrusTestName(), "exception");
            }
        }
    }

    /**
     * @param url The URL to navigate to.
     */
    protected void performNavigateTo(URL url) {
        try {
            webDriver.navigate().to(url);
        } catch (WebDriverException e) {
            logger.error("Cannot navigate to: " + url, e);
            takeScreenshot();
            throw new CitrusRuntimeException("Unable to navigate to: " + url);
        }
    }

    /**
     * @param by
     * @param isClosing True if the window has been closed.
     * @param element WebElement to perform click on.
     * @see By
     */
    protected void performClick(By by, boolean isClosing, WebElement element) {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Click element " + by.toString());
        }
    
        try {
            element.click();
    
            /*
             * after each click, look at at the document.readyState Not every
             * click results in a new page load, but when it does, this ensures
             * that the new document is completely loaded and ready This may
             * fail in situations where the browser window has recently closed
             * so only a warning is logged
             */
            if (!isClosing) {
                waitForDocumentReady();
            } else {
                /* delay execution */
                if (logger.isDebugEnabled()) {
                    logger.debug("Click '" + by.toString() + "' expected to close window handle, waiting for " + webClientConfiguration.getRetryTimeout()
                        + " seconds.");
                }
                delay();
            }
        } catch (WebDriverException e) {
            logger.error("Could not click on element: " + by.toString(), e);
            extractScreenshot(e);
            throw new CitrusRuntimeException("Unable to click on element: " + by.toString());
        }
    }

    /**
     * @param by
     * @param value Value to set.
     * @param el WebElement to perform set input on.
     */
    protected void performSetInput(By by, String value, WebElement el) {
        String inputValue = value;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Set input value '" + inputValue + "' on element " + by.toString() + ", value was previously '"
                + el.getAttribute("value") + "'");
        }
    
        try {
            el.clear();
            el.sendKeys(inputValue);
        } catch (WebDriverException e) {
            logger.error("Unable to set input on element: " + by.toString(), e);
            extractScreenshot(e);
            throw new CitrusRuntimeException("Unable to set input on element: " + by.toString());
        }
    }

    /**
     * Close the web driver
     */
    protected void closeWebDriver() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing the browser " + webDriver + " ...");
            }
            webDriver.quit();
        } catch (UnreachableBrowserException e) {
            // It happens for Firefox. It's ok: browser is already closed.
            logger.error("Browser is unreachable", e);
        } catch (WebDriverException e) {
            logger.error("Cannot close browser normally", e);
            extractScreenshot(e);
        }
    }

    /**
     * @param by
     * @param tryAgain
     * @param timeout
     * @return WebElement
     */
    protected WebElement findVisibleElement(By by, int retries, int timeout) {
        WebElement element = null;

        int tryAgain = retries;
        while (tryAgain > 0) {
            try {
                WebDriverWait wdw = new WebDriverWait(webDriver, timeout);
                element = webDriver.findElement(by);
                wdw.ignoring(StaleElementReferenceException.class);
                wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.MILLISECONDS);
                wdw.withMessage("Error while trying to find element " + by.toString());
                ExpectedConditions.visibilityOf(element);
                tryAgain = 0;
            } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
                tryAgain--;
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Did not find visible element: " + by.toString(), e);
                }
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Did not find visible element '" + by.toString() + "' at url " + webDriver.getCurrentUrl()
                        + "' due to: " + e.getMessage() + ", retries left: " + tryAgain);
                }
            } catch (ElementNotVisibleException e) {
                logger.error("Element " + by.toString() + " is not visible");
                throw new CitrusRuntimeException(e);
            } catch (WebDriverException e) {
                logger.error("Could not find visible element: " + by.toString(), e);
                extractScreenshot(e);
                throw new CitrusRuntimeException("Could not find visible element: " + by.toString());
            }
        }

        return element;
    }

    /**
     * Get a specific element from the loaded webpage.
     *
     * @param by Element identifier method.
     * @return WebElement.
     */
    protected WebElement internalFindElementBy(By by, int retries, int timeout) {
        WebElement element = null;

        int tryAgain = retries;
        while (tryAgain > 0) {
            try {
                WebDriverWait wdw = new WebDriverWait(webDriver, timeout);
                element = webDriver.findElement(by);
                wdw.ignoring(StaleElementReferenceException.class);
                wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.MILLISECONDS);
                wdw.withMessage("Error while trying to find element " + by.toString());
                wdw.until(ExpectedConditions.elementToBeClickable(element));
                tryAgain = 0;
            } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
                tryAgain--;
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Did not find element: " + by.toString(), e);
                }
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Did not find element '" + by.toString() + "' at url " + webDriver.getCurrentUrl()
                        + "' due to: " + e.getMessage() + ", retries left: " + tryAgain);
                }
            } catch (ElementNotVisibleException e) {
                logger.error("Element " + by.toString() + " is not visible");
                throw new CitrusRuntimeException(e);
            } catch (WebDriverException e) {
                logger.error("Could not find element: " + by.toString(), e);
                extractScreenshot(e);
                throw new CitrusRuntimeException("Could not find element: " + by.toString());
            }
        }

        return element;
    }

    /**
     * @param by 
     */
    protected void handleElementError(By by) {
        /* make a screenshot when this happens */
        takeScreenshot();
    
        throw new CitrusRuntimeException("Cannot find element '" + by.toString()  
            + "' on page '" + webDriver.getCurrentUrl() + "' with title '" + this.webDriver.getTitle() + "'");
    }

    /**
     * @return WebDriver
     */
    protected abstract WebDriver createLocalWebDriver();

    /**
     * @param config WebClientConfiguration
     * @return DesiredCapabilities
     */
    protected abstract DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config);

    /**
     * @param config WebClientConfiguration
     * @return RemoteWebDriver
     */
    protected RemoteWebDriver createRemoteWebDriver(WebClientConfiguration config) {
        DesiredCapabilities capabilities = createDesiredCapabilities(config);
        
        if (config.getSeleniumPlatform() != null) {
            capabilities.setPlatform(config.getSeleniumPlatform());
        }
        
        updateDesiredCapabilityConfiguration(capabilities);
        
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(config.getSeleniumServerUrl(), capabilities);
        remoteWebDriver.setFileDetector(new LocalFileDetector());
    
        return remoteWebDriver;
    }
    
    /**
     * @param capabilities DesiredCapabilities that can be updated.
     */
    protected void updateDesiredCapabilityConfiguration(DesiredCapabilities capabilities) {
        String configItemPrefix = webClientConfiguration.getBrowserType().toString() + ".capability.";
        for (Map.Entry<String, Object> entry: webClientConfiguration.getContextVariables().entrySet()) {
            if (entry.getKey().startsWith(configItemPrefix)) {
                String capabilityName = entry.getKey().substring(configItemPrefix.length());
                if (!StringUtils.isEmpty(capabilityName)) {
                    capabilities.setCapability(capabilityName, entry.getValue());
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Using '{}' capabilities: {}", webClientConfiguration.getBrowserType(), capabilities.asMap());
        }
    }

    /**
     * Write HTML data to file.
     * @param data data to write to file
     * @param testcaseName testcase name
     * @param testcaseStep testcase step name
     */
    protected void writeHTMLDataToFile(String data, String testcaseName, String testcaseStep) {
        File file = createOutputFileName(testcaseName, testcaseStep, "html");
    
        try {
            if (!file.createNewFile()) {
                logger.warn("Overwriting file: " + file.getName());
            }
    
            FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8, false);
            if (logger.isDebugEnabled()) {
                logger.debug("Data written to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Unable to write data to: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Write PNG data to file.
     * @param data data to write to file
     * @param testcaseName testcase name
     * @param testcaseStep testcase step name
     */
    protected void writePNGDataToFile(byte[] data, String testcaseName, String testcaseStep) {
        File file = createOutputFileName(testcaseName, testcaseStep, "png");
    
        try {
            if (!file.createNewFile()) {
                logger.error("Overwriting file: " + file.getName());
            }
    
            FileUtils.writeByteArrayToFile(file, data);
    
            if (logger.isDebugEnabled()) {
                logger.debug("Data written to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Unable to write data to: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Create an output file name using the screenshot location as directory.
     * @param testcaseName test case name which will be part of the file name.
     * @param testcaseStep test step which will be part of the filename.
     * @param ext extension of the file.
     * @return The File object.
     */
    protected File createOutputFileName(String testcaseName, String testcaseStep, String ext) {
        StringBuilder fullName = new StringBuilder();
        fullName.append(webClientConfiguration.getScreenshotLocation());
    
        /* ensure that the directory exists */
        File dir = new File(fullName.toString());
        if (!dir.exists()) {
            dir.mkdirs();
    
            if (!dir.isDirectory()) {
                throw new CitrusRuntimeException(fullName.toString() + " is not a directory");
            }
            if (!dir.exists()) {
                throw new CitrusRuntimeException(fullName.toString() + " does not exist");
            }
        }
    
        fullName.append(testcaseName);
        fullName.append("_");
        fullName.append(testcaseStep);
        fullName.append("_");
        fullName.append(webClientConfiguration.getBrowserType().name());
        fullName.append(".");
        fullName.append(ext);
        
        Path newFile = FileSystems.getDefault().getPath(fullName.toString());

        return newFile.toFile();
    }

    /**
     * Validate expected string against actual string, using a validation rule
     * 
     * @param actual
     * @param expected
     * @param validationRule
     */
    protected void validate(String actual, String expected) {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating expected '" + expected + "' against actual '" + actual + "'");
        } 
        
        String validationError = null;
        if (expected == null || expected.isEmpty()) {
            if (expected != null && !expected.isEmpty()) {
                validationError = "Validaton Error: For EMPTY validation not empty expected string is given: " + expected;
            } else if (actual != null && !actual.isEmpty()) { // handle null
                                                              // elements as
                                                              // empty element
                validationError = "Validaton Error: For EMPTY validation not empty element is found: " + actual;
            }
        } else if (!expected.equals(actual)) {
            validationError = "Validaton Error: For EQUALS validation expected <" + expected + "> and actual <"
                    + actual + "> do not match.";
        }
        
        if (validationError != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(validationError);
            }
            takeScreenshot();
            throw new CitrusRuntimeException(validationError);
        }
    }

    /**
     * Possible tick.
     */
    protected void tick() {
        //do nothing, can be overwritten when needed for specific browsers.
    }

    /**
     * Apply the retry timeout as delay.
     */
    protected void delay() {
        try {
            TimeUnit.SECONDS.sleep(webClientConfiguration.getRetryTimeout());
        } catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Sleep interrupted", e);
            }
        }
    }
    
    /**
     * Possible Delay.
     */
    protected void delay2() {
        //do nothing, can be overwritten when needed for specific browsers.
    }
    
    /**
     * Browser specific wait for document is ready.
     */
    protected void waitForDocumentReady() {
        //do nothing, can be overwritten when needed for specific browsers.
    }

    /**
     * Wait for page to be loaded.
     */
    protected void waitForPageLoad() {
        try {
            WebDriverWait wdw = new WebDriverWait(webDriver, webClientConfiguration.getPageloadTimeout());
            wdw.pollingEvery(webClientConfiguration.getPollInterval(), TimeUnit.SECONDS);
            /*
             * can happen during wait for element 'html'
             * 
             * WARNING: WebDriverException thrown by findElement(By.tagName:
             * html)
             * 
             * ignoring it here
             */
            wdw.ignoring(WebDriverException.class);
            wdw.until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));
        } catch (TimeoutException e) {
            logger.error("Timeout while waiting for page load to complete", e);
            throw new CitrusRuntimeException("Timeout while waiting for page load to complete");
        }
    }

    /**
     * Returns all window handles.
     * @return The window handles.
     */
    protected Set<String> getWindowHandles() {
        Set<String> handles = null;
        try {
            handles = webDriver.getWindowHandles();
        } catch (WebDriverException e) {
            logger.error("Error while getting a list of window handles", e);
            extractScreenshot(e);
            throw new CitrusRuntimeException("Could not resolve windows handles from webdriver");
        }
        return handles;
    }
    
}
