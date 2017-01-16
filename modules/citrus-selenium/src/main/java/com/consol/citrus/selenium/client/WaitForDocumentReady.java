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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Waits for a document to be ready, by using java script.
 * 
 * @author VASCO Data Security 
 * @since 2.7
 */
public class WaitForDocumentReady 
{
    private static final Logger logger = LoggerFactory.getLogger(WaitForDocumentReady.class);
    private static final String COMPLETE = "complete";
    private static final String INTERACTIVE = "interactive";
    /**
     * Check if readyState of a document is set to 'complete'.
     * 
     * @param webDriver WebDriver to be used.
     * @return null if document is not ready
     */
    public static ExpectedCondition<WebDriver> isReady(final WebDriver driver) {
        return new ExpectedCondition<WebDriver>() {
            /**
             * @see com.google.common.base.Function#apply(java.lang.Object)
             */
            @Override
            public WebDriver apply(WebDriver driver) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Running javascript to get readyState");
                    }
                    final String state = (String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
                    if (logger.isDebugEnabled()) {
                        logger.debug("ReadyState is: " + state);
                    }
                    if( COMPLETE.equals(state) || INTERACTIVE.equals(state)) {
                        return driver;
                    }
                    return null;
                }
                catch(WebDriverException e) {
                    /* javascript execution may fail, catch the webdriver exception here and print the message */
                    if (logger.isTraceEnabled()) {
                        logger.trace("JavaScript exception while checking if document load was completed", e);
                    }

                    logger.warn( e.getMessage() );
                }
                return null;
            }

            /**
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString() {
                return "document.readyState ";
            }
        };
    }

}