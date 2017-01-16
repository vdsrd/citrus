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
import org.openqa.selenium.By;

/**
 * Action that clicks on an element.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ClickAction extends AbstractWebAction {

    private By by;
    private String byXpathFromParam;
    private String closing;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        boolean isClosing = closing == null ? false : Boolean.parseBoolean(closing);

        if (byXpathFromParam == null) {
            webClient.getBrowser().click(by, isClosing);
        } else {
            String resolvedVariable = context.replaceDynamicContentInString(byXpathFromParam);
            By b = By.ByXPath.xpath(resolvedVariable);
            webClient.getBrowser().click(b, isClosing);
        }
    }

    /**
     * @return By
     */
    public By getBy() {
        return by;
    }

    /**
     * @param by By to set.
     */
    public void setBy(By by) {
        this.by = by;
    }

    /**
     * @return By XPath From Parameter.
     */
    public String getByXpathFromParam() {
        return byXpathFromParam;
    }

    /**
     * @param str Set By XPath From Parameter.
     */
    public void setByXpathFromParam(String str) {
        this.byXpathFromParam = str;
    }

    /**
     * @return Closing parameter, indicates when the html is closing.
     */
    public String getClosing() {
        return closing;
    }

    /**
     * @param closing Set true is the HTML is closing.
     */
    public void setClosing(String closing) {
        this.closing = closing;
    }

}
