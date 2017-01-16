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

/**
 * Action to switch control to a specific window or popup.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class SwitchWindowAction extends AbstractWebAction {

    private String urlPart;
    private String numWindowsOpen = "1";

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        webClient.getBrowser().switchWindow(urlPart, Integer.parseInt(numWindowsOpen));
    }

    /**
     * @return The URL part.
     */
    public String getUrlPart() {
        return urlPart;
    }

    /**
     * @param str The URL part that has to be available in the window.
     */
    public void setUrlPart(String str) {
        this.urlPart = str;
    }

    /**
     * @return The number of windows that must be open.
     */
    public String getNumWindowsOpen() {
        return numWindowsOpen;
    }

    /**
     * @param num Set the number of windows that must be open. 
     */
    public void setNumWindowsOpen(String num) {
        numWindowsOpen = num;
    }

}