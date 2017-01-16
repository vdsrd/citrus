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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.model.testcase.selenium.ExtractDefinition;

/**
 * Action to revolve a list of elements.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ExtractListAction extends AbstractWebAction {

    private Map<By, ExtractDefinition.Element> elements;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (elements == null) {
            logger.error(this.getName() + " called with invalid arguments");
            throw new CitrusRuntimeException("Action " + this.getName() + " called with invalid arguments");
        }

        if (elements != null) {
            for (By by : elements.keySet()) {
                List<String> values = new ArrayList<String>();
                String variable = context.replaceDynamicContentInString(elements.get(by).getVariable());
                String attribute = context.replaceDynamicContentInString(elements.get(by).getAttribute());
                logger.info("Extracting the element by <{}>", by);

                List<WebElement> elements = null;

                // check if the by has attribute selection
                if (by instanceof By.ByXPath) {
                    // TODO: refactor the following
                    String xpathExpression = by.toString().replaceAll("By.xpath:\\s*", "");
                    if (xpathExpression.contains("/@")) {
                        // we found attribute selection.
                        String[] parts = xpathExpression.split("/@");
                        attribute = parts[1];
                        String newXpathExpresseion = parts[0];
                        elements = webClient.getBrowser().findElements(By.xpath(newXpathExpresseion));
                    }
                }
                if (elements == null) {
                    elements = webClient.getBrowser().findElements(by);
                }

                for (WebElement element : elements) {
                    String value = null;
                    if (StringUtils.hasText(attribute)) {
                        value = element.getAttribute(attribute);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Attribute of the element: <{}>", value);
                        }
                    } else {
                        value = element.getAttribute("value");
                        if (logger.isDebugEnabled()) {
                            logger.debug("Attribute \"value\" of the element: <{}>", value);
                        }
                        if (StringUtils.isEmpty(value)) {
                            value = element.getText();
                            if (logger.isDebugEnabled()) {
                                logger.debug("Value of the element: <{}>", value);
                            }
                        }
                    }

                    if (value == null) {
                        logger.error("Attribute value is not defined");
                        throw new CitrusRuntimeException("Attribute value is not defined");
                    }

                    values.add(value);
                }

                context.setVariable(variable, values);
            }
        }

    }

    /**
     * @return Map containing the page elements.
     */
    public Map<By, ExtractDefinition.Element> getElements() {
        return elements;
    }

    /**
     * @param elements the page elements to be set.
     */
    public void setElements(Map<By, ExtractDefinition.Element> elements) {
        this.elements = elements;
    }

}
