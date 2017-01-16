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

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.model.testcase.selenium.ExtractDefinition;

/**
 * Action to extract an element.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ExtractAction extends AbstractWebAction {

    private Map<By, ExtractDefinition.Element> elements;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {

        if (elements == null) {
            logger.error(this.getName() + " called with invalid arguments");
            throw new CitrusRuntimeException("Action " + this.getName() + "called with invalid arguments");
        }

        if (elements != null) {
            for (Map.Entry<By, ExtractDefinition.Element> entry: elements.entrySet()) {
                By by = entry.getKey();
                ExtractDefinition.Element elem = entry.getValue();
                
                String variable = context.replaceDynamicContentInString(elem.getVariable());
                String attribute = context.replaceDynamicContentInString(elem.getAttribute());
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Extracting the element by <{}>", by);
                }

                WebElement element = null;

                // check if the by has attribute selection
                if (by instanceof By.ByXPath) {
                    String xpathExpression = by.toString().replaceAll("By.xpath:\\s*", "");
                    if (xpathExpression.contains("/@")) {
                        // we found attribute selection.
                        String[] parts = xpathExpression.split("/@");
                        attribute = parts[1];
                        String newXpathExpresseion = parts[0];
                        element = webClient.getBrowser().findElement(By.xpath(newXpathExpresseion));
                    }
                }
                if (element == null) {
                    element = webClient.getBrowser().findElement(by);
                }
                
                String value = null;
                if (element != null) {
                    if (StringUtils.hasText(attribute)) {
                        value = element.getAttribute(attribute);
                    } else {
                        String tagName = element.getTagName().toLowerCase();
                        if ("select".equals(tagName)) {
                            value = webClient.getBrowser().getOptionsInSelect(element);
                        } else {
                            value = element.getAttribute("value");
                            if (StringUtils.isEmpty(value)) {
                                value = element.getText();
                            }
                        }
                    }
                }
                context.setVariable(variable, value);
            }
        }
    }

    /**
     * @return a Map containing all page elements.
     */
    public Map<By, ExtractDefinition.Element> getElements() {
        return elements;
    }

    /**
     * @param elements Set page elements.
     */
    public void setElements(Map<By, ExtractDefinition.Element> elements) {
        this.elements = elements;
    }

}
