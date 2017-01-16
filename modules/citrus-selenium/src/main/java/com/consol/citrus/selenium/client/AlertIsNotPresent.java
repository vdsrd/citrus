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

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if an alert box is not present.
 * 
 * @author VASCO Data Security 
 * @since 2.7
 */
public class AlertIsNotPresent 
{
    /**
     * This method relies on the NoAlertPresentException to complete the condition
     * When the exception is thrown, we return TRUE as this is exactly what we want.
     * (The Alert dialog is not present)
     * 
     * @param webDriver
     * @return true if no alert dialog is present
     */
    private static final Logger logger = LoggerFactory.getLogger(WaitForDocumentReady.class);

    /**
     * @param driver Webdriver to be used.
     * @return TRUE when alert box is not present.
     */
    public ExpectedCondition<Boolean> alertIsNotPresent(final WebDriver driver) {
        return new ExpectedCondition<Boolean>() {

            /**
             * @see com.google.common.base.Function#apply(java.lang.Object)
             */
            @Override
            public Boolean apply(WebDriver driver) {

                try {
                    driver.switchTo().alert();
                }
                catch( NoAlertPresentException e) 
                {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No alertbox present (as expected)");
                    }
                    return Boolean.TRUE;
                }

                return Boolean.FALSE;
            }

            /**
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString() {
                return "alert not to be present";
            }
        };
    }

}