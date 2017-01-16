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
package com.consol.citrus.selenium.config.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.selenium.action.AbstractWebAction;
import com.consol.citrus.selenium.action.ValidateAction;

/**
 * WebBrowser client parser sets properties on bean definition for client
 * component.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ValidateActionParser extends AbstractWebActionParser {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ValidateActionParser.class);

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
     */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        Map<By, List<String>> validations = new LinkedHashMap<>();
        List<Element> validationElements = DomUtils.getChildElementsByTagName(element, "element");
        for (Element validateElement : validationElements) {
            String value = validateElement.getAttribute("value");
            By by = getBy(validateElement.getAttribute("by"), validateElement.getAttribute("select"));
            List<String> listOfByValues = new ArrayList<String>();
            if (validations.containsKey(by)) {
                listOfByValues = validations.get(by);
            }
            listOfByValues.add(value);
            validations.put(by, listOfByValues);
        }
        builder.addPropertyValue("validations", validations);
    }

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#getActionClass()
     */
    @Override
    protected Class<? extends AbstractWebAction> getActionClass() {
        return ValidateAction.class;
    }

    private By getBy(String byWhat, String selector) {
        By by = null;
        try {
            Method byMethod = By.class.getMethod(byWhat, String.class);
            by = (By) byMethod.invoke(null, selector);
        } catch (NoSuchMethodException e) {
            log.error("Could not find method: " + byWhat, e);
            throw new CitrusRuntimeException("Could not create By: " + byWhat);
        } catch (SecurityException e) {
            log.error("Security exception while searching for method: " + byWhat, e);
            throw new CitrusRuntimeException("Could not create By: " + byWhat);
        } catch (IllegalAccessException e) {
            log.error("Illegal access when invoking method '" + byWhat + "' with: " + selector, e);
            throw new CitrusRuntimeException("Could not create By: " + byWhat);
        } catch (IllegalArgumentException e) {
            log.error("Illegale argument when invoking method '" + byWhat + "' with: " + selector, e);
            throw new CitrusRuntimeException("Could not create By: " + byWhat);
        } catch (InvocationTargetException e) {
            log.error("Target invocation exception when invoking method '" + byWhat + "' with: " + selector, e);
            throw new CitrusRuntimeException("Could not create By: " + byWhat);
        }
        return by;
    }
}
