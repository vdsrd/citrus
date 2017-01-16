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

import org.openqa.selenium.By;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.consol.citrus.config.util.BeanDefinitionParserUtils;
import com.consol.citrus.selenium.action.AbstractWebAction;
import com.consol.citrus.selenium.action.ClickAction;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class ClickActionParser extends AbstractWebActionParser {

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
     */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String byXpathFromParam = element.getAttribute("byXpathFromParam").trim();
        if (StringUtils.hasText(byXpathFromParam)) {
            BeanDefinitionParserUtils.setPropertyValue(builder, byXpathFromParam, "byXpathFromParam");
        }

        By by = getByFromElement(element);
        builder.addPropertyValue("by", by);

        String closing = element.getAttribute("closing").trim();
        if (StringUtils.hasText(closing)) {
            BeanDefinitionParserUtils.setPropertyValue(builder, closing, "closing");
        }
    }

    /**
     * @see com.consol.citrus.selenium.config.xml.AbstractWebActionParser#getActionClass()
     */
    @Override
    protected Class<? extends AbstractWebAction> getActionClass() {
        return ClickAction.class;
    }
}
