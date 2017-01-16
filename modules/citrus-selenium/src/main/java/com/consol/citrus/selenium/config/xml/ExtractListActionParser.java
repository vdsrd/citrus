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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.consol.citrus.model.testcase.selenium.ExtractDefinition;
import com.consol.citrus.selenium.action.AbstractWebAction;
import com.consol.citrus.selenium.action.ExtractListAction;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class ExtractListActionParser extends AbstractWebActionParser {

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
     */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        Map<By, ExtractDefinition.Element> elements = new LinkedHashMap<>();
        List<Element> webElements = DomUtils.getChildElementsByTagName(element, "element");
        for (Element webElement : webElements) {
            By by = getByFromElement(webElement);
            ExtractDefinition.Element el = new ExtractDefinition.Element();
            el.setVariable(webElement.getAttribute("variable"));
            el.setAttribute(webElement.getAttribute("attribute"));
            elements.put(by, el);
        }
        builder.addPropertyValue("elements", elements);
    }

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#getActionClass()
     */
    @Override
    protected Class<? extends AbstractWebAction> getActionClass() {
        return ExtractListAction.class;
    }
}
