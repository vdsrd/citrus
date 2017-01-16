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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 * Action to resolve an attribute.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class GetAttributeAction extends AbstractWebAction {

    private By by;
    private String byXpathFromParam;
    private String attribute;
    private String varName;

    private void evalAttribute(TestContext context, WebElement element) {
        // special case for disabled attribute
        if ("disabled".equalsIgnoreCase(attribute)) {
            if (element.isEnabled()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Element '" + by + "' is not disabled");
                }
                throw new CitrusRuntimeException("Element '" + by + "' is not disabled");
            }
        }

        if ("checked".equalsIgnoreCase(attribute) || "selected".equalsIgnoreCase(attribute)) {
            if (!element.isSelected()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Element '" + by + "' is not '" + attribute + "'");
                }
                throw new CitrusRuntimeException("Element '" + by + "' is not '" + attribute + "'");
            }
        }

        // get the value of an attribute
        if (varName != null) {
            String value = element.getAttribute(attribute);
            if (value == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Element '" + by + "' has no value for attribute " + attribute);
                }
                throw new CitrusRuntimeException("Element '" + by + "' has no value for attribute " + attribute);
            }
            context.setVariable(varName, value);
        }
    }

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (byXpathFromParam == null) {
            evalAttribute(context, webClient.getBrowser().findVisibleElement(by));
        } else {
            String resolvedVariable = context.replaceDynamicContentInString(byXpathFromParam);
            by = By.ByXPath.xpath(resolvedVariable);
            evalAttribute(context, webClient.getBrowser().findVisibleElement(by));
        }
    }

    /**
     * @return the By.
     */
    public By getBy() {
        return by;
    }

    /**
     * @param by By to be set.
     */
    public void setBy(By by) {
        this.by = by;
    }

    /**
     * @return By XPath From parameter.
     */
    public String getByXpathFromParam() {
        return byXpathFromParam;
    }

    /**
     * @param str Set By XPath From parameter.
     */
    public void setByXpathFromParam(String str) {
        this.byXpathFromParam = str;
    }

    /**
     * @return the attribute.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attrib attribute to be set.
     */
    public void setAttribute(String attrib) {
        this.attribute = attrib;
    }

    /**
     * @return the variable.
     */
    public String getVariable() {
        return this.varName;
    }

    /**
     * @param var Set the variable.
     */
    public void setVariable(String var) {
        this.varName = var;
    }

}
