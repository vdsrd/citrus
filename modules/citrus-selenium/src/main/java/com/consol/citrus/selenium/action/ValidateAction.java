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

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.validation.matcher.ValidationMatcherUtils;

/**
 * Action to validate elements.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ValidateAction extends AbstractWebAction {

    private Map<By, List<String>> validations;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (validations != null) {
            for (Map.Entry<By, List<String>> entry: validations.entrySet()) {
                By by = entry.getKey();
                
                List<String> validationValues = entry.getValue();
                for (String validationValue : validationValues) {
                    String controlValue = context.replaceDynamicContentInString(validationValue);
                    
                    if (logger.isDebugEnabled()) {
                        logger.debug("Expected value: '" + controlValue + "' Find element: " + by.toString());
                    }
                    
                    if (!ValidationMatcherUtils.isValidationMatcherExpression(controlValue)) {
                        webClient.getBrowser().validate(by, controlValue);
                    } else {
                        /*
                         * validation rules in test-cases also check if value is
                         * empty for non-existing elements. i.e an element that
                         * should not be there (thus its value is empty)
                         * 
                         * other validation rules also check if the value is
                         * empty for existing elements ... they should
                         * use @web:empty@ and not ""
                         * 
                         * getElement does not throw an exception if the element
                         * cannot be found
                         */
                        // check if the by has attribute selection
                        if (by instanceof By.ByXPath) {
                            String xpathExpression = by.toString().replaceAll("By.xpath:\\s*", "");
                            if (xpathExpression.contains("/@")) {
                                // we found attribute selection.
                                String[] parts = xpathExpression.split("/@");
                                String attribute = parts[1];
                                String newXpathExpresseion = parts[0];
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Found attribute selection in xpath '" + by.toString() + "'  --> "
                                        + newXpathExpresseion + " ; attribute: " + attribute);
                                }
                                By xby = By.xpath(newXpathExpresseion);
                                WebElement elementByXpath = webClient.getBrowser().getElement(xby);

                                if (StringUtils.hasText(attribute) && elementByXpath == null) {
                                    /*
                                     * if an element does not exist, its value
                                     * attribute must be empty
                                     */
                                    throw new CitrusRuntimeException("Element '" + by.toString()
                                            + "' does not exist but you are expecting " + attribute);
                                }
                                String actualValue = elementByXpath.getAttribute(attribute);

                                if (actualValue != null) {
                                    /*
                                     * ensure that actual does not have any
                                     * leading or trailing whitespace (stated in
                                     * Javadoc, but behaves differently on Edge)
                                     */
                                    actualValue = actualValue.trim();
                                }

                                if (logger.isDebugEnabled()) {
                                    logger.debug("(XPathExpression) Validating " + by.toString() + " actual: " + actualValue
                                        + " control: " + controlValue);
                                }

                                ValidationMatcherUtils.resolveValidationMatcher(by.toString(), actualValue,
                                        controlValue, context);

                                continue;
                            }
                        }

                        WebElement element = webClient.getBrowser().findElement(by);
                        final String tagName = element.getTagName().toLowerCase();

                        String actualValue = null;
                        if ("select".equals(tagName)) {
                            actualValue = webClient.getBrowser().getOptionsInSelect(element);
                            if (logger.isDebugEnabled()) {
                                logger.debug("validate tag '" + tagName + "' option list: " + actualValue);
                            }
                        } else {
                            actualValue = webClient.getBrowser().findElement(by).getText();
                            if (logger.isDebugEnabled()) {
                                logger.debug("validate tag '" + tagName + "' text: '" + actualValue + "'");
                            }
                        }

                        if (actualValue != null) {
                            /*
                             * ensure that actual does not have any leading or
                             * trailing whitespace (stated in Javadoc, but
                             * behaves differently on Edge)
                             */
                            actualValue = actualValue.trim();
                            
                            if (logger.isDebugEnabled()) {
                                logger.debug("Validating " + by.toString() + " actual: " + actualValue + " control: "
                                    + controlValue);
                            }
                        } else {
                            logger.warn("Value of element '" + by.toString() + "' is not defined");
                        }

                        ValidationMatcherUtils.resolveValidationMatcher(by.toString(), actualValue, controlValue,
                                context);
                    }
                }
            }
        }
    }

    /**
     * @return The validations.
     */
    public Map<By, List<String>> getValidations() {
        return this.validations;
    }

    /**
     * @param validations The validations to be used.
     */
    public void setValidations(Map<By, List<String>> validations) {
        this.validations = validations;
    }
}
