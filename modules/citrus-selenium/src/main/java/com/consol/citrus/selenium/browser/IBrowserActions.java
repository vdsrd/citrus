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

import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public interface IBrowserActions {
    
    /**
     * Accept an alert box
     * 
     * @return AlertMessage
     * @throws CitrusRuntimeException
     */
    String acceptAlertBox();
    
    /**
     * Cancel an Alert box
     * 
     * @return AlertMessage
     * @throws CitrusRuntimeException
     */
    String cancelAlertBox();
    
    /**
     * Validates if there is an AlertBox (dialog) present.
     * @throws CitrusRuntimeException
     */
    void verifyAlertBoxIsPresent();
    
    /**
     * Validates that there is no AlertBox (dialog) present. This method throws
     * a CitrusRuntimeException when the dialog is still there after TIME_OUT
     * seconds
     * @throws CitrusRuntimeException
     */
    void verifyAlertBoxIsAbsent();
    
    /**
     * Clear the browser cache (delete all cookies)
     */
    void clearBrowserCache();
    
    /**
     * Use back button in browser.
     */
    void navigateBack();
    
    /**
     * Use forward button in browser.
     */
    void navigateForward();
    
    /**
     * Use refresh button in browser.
     */
    void refresh();
    
    /**
     * Take screenshot based on date/time.
     */
    void takeScreenshot();
    
    /**
     * Get the current URL
     * @return URL
     */
    String getCurrentUrl();
    
    /**
     * Open new browser window.
     * @throws CitrusRuntimeException When new window cannot be opened.
     */
    void newWindow();
    
    /**
     * Close the current browser window.
     * @throws CitrusRuntimeException When no previous windows is available anymore.
     */
    void closeWindow();
    
}
