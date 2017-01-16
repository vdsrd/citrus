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
package com.consol.citrus.selenium.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * Action to verify the page title.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class VerifyTitleAction extends AbstractWebAction {

    private String title;
    
    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (!title.equals(webClient.getBrowser().getPageTitle())) {
            throw new CitrusRuntimeException("Expected title '" + title + "' does not match page title: " + webClient.getBrowser().getPageTitle());
        }
    }
    
    /**
     * @param title page title to be verified.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The page title.
     */
    public String getTitle() {
        return this.title;
    }
}
