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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public interface IBrowser {

    /**
     * Stop the browser
     */
    void stop();
    
    /**
     * Get the current URL
     * @return URL
     */
    String getCurrentUrl();
    
    /**
     * Return the page source
     * 
     * @return String page source
     */
    String getPageSource();
    
    /**
     * Get the page title.
     * @return The page title.
     */
    String getPageTitle();
    
    /**
     * Click on a single element identified by By.
     * 
     * Depending on the browser type, the element to click is first scrolled
     * into view. If the Selenium driver throws a WebDriverException, catch it
     * and print the error before throwing a CitrusRuntimeException
     *
     * @param by
     * @param isClosing True if the window has been closed.
     * @throws CitrusRuntimeException when element cannot be found
     * @see By
     */
    void click(By by, boolean isClosing);
    
    /**
     * Delete a cookie.
     * 
     * @param name cookie name
     */
    void delCookie(String name);
    
    /**
     * Get a specific element from the loaded webpage.
     *
     * @param by Element identifier method.
     * @return WebElement.
     */
    WebElement findElement(By by);
    
    /**
    *
    * @param element
    * @return String the text values of options in Select as csv.
    */
   String getOptionsInSelect(WebElement element);
   
   /**
    * Get all elements matched by By.
    *
    * @param by
    *            By.
    * @return List of WebElements.
    */
   List<WebElement> findElements(By by);
   
   /**
    * Get a specific element from the loaded web page. This method also finds a
    * disabled element. The element must be(come) visible.
    *
    * @param by Element identifier method.
    * @return WebElement.
    */
   WebElement findVisibleElement(By by);
   
   /**
    * Get a cookie value
    * 
    * @param cookie
    * @return String Value
    */
   public String getCookieValue(String cookie);
   
   /**
    * Go to a frame handle
    * 
    * @param frame
    * @throws CitrusRuntimeException
    */
   void gotoFrame(String frame);
   
   /**
    * @param by Element to hover
    * @throws CitrusRuntimeException if element cannot be found
    */
   void hover(By by);
   
   /**
    * Take a screenshot.
    *
    * A screenshot filename is constructed as follows:
    * 
    * <testcaseName>_<testcaseStep>_<browserType>_<environment>.png
    * 
    * Details:
    * 
    * <testcaseName> Name of the testcase _ <testcaseStep> Step in the testcase
    * _ <browserType> In which browser the testcase has run _ <environment> On
    * what environment the testcase has run
    *
    * The browserType and environment are set by this class.
    *
    * @param testcaseName Name of the testcase (set by user)
    * @param testcaseStep Step in the testcase (set by user)
    */
   void takeScreenshot(String testcaseName, String testcaseStep);
   
   /**
    * Select an item from a pull-down list.
    *
    * @param by
    * @param item The value of the item.
    * @throws CitrusRuntimeException when element cannot be found
    */
   void selectItem(By by, String item);
   
   /**
    * Set a cookie.
    * 
    * @param name cookie name
    * @param value cookie value
    */
   void setCookie(String name, String value);
   
   
   /**
    * Set a value on a single input element by its id after clearing it.
    *
    * @param by specified element
    * @param value Value to set.
    * @throws CitrusRuntimeException when element cannot be found
    */
   void setInput(By by, String value);
   
   /**
    * Switch to specified browser window.
    * 
    * @param urlpart Part of the URL of window to switch to.
    * @param expectedNumberOfWindows expected number of windows to be present before switching to window.
    * @throws CitrusRuntimeException When window not available or when expected number of windows is different.
    */
   void switchWindow(String urlpart, int expectedNumberOfWindows);
   
   /**
   * Try to get a specific element from the loaded webpage but do not throw an
   * error (needed for checks that require an element to not exist)
   * 
   * @param by 
   * @return WebElement
   */
  WebElement getElement(By by);
  
  /**
   * @param by Element to be validated
   * @param expected Expected value
   */
  void validate(By by, String expected);
  
  /**
   * Verify if the URL of the current page matches the expected URL
   * 
   * @param urlPath Expected URL
   * @throws CitrusRuntimeException if the URLs do not match
   */
  void verifyPageURL(String urlPath);
  
  /**
   * This method navigates to the URL indicated by pageUrl. If a
   * MalformedURLException occurs, it is assumed that the pageUrl is a
   * relative URL and it is appended to the baseURL
   * 
   * @param pageUrl fully qualified URL or relative URL
   * @throws CitrusRuntimeException if the URL is malformed
   */
  void navigateTo(String pageUrl);
}
