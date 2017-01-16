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
package com.consol.citrus.selenium.config.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.consol.citrus.selenium.config.xml.BrowserActionParser;
import com.consol.citrus.selenium.config.xml.ClickActionParser;
import com.consol.citrus.selenium.config.xml.DelCookieActionParser;
import com.consol.citrus.selenium.config.xml.ExtractActionParser;
import com.consol.citrus.selenium.config.xml.ExtractListActionParser;
import com.consol.citrus.selenium.config.xml.GetAttributeActionParser;
import com.consol.citrus.selenium.config.xml.GetCookieActionParser;
import com.consol.citrus.selenium.config.xml.GetCurrentUrlActionParser;
import com.consol.citrus.selenium.config.xml.GetPageSourceActionParser;
import com.consol.citrus.selenium.config.xml.GotoActionParser;
import com.consol.citrus.selenium.config.xml.GotoFrameActionParser;
import com.consol.citrus.selenium.config.xml.HoverActionParser;
import com.consol.citrus.selenium.config.xml.ScreenshotActionParser;
import com.consol.citrus.selenium.config.xml.SelectOptionActionParser;
import com.consol.citrus.selenium.config.xml.SetCookieActionParser;
import com.consol.citrus.selenium.config.xml.SetInputActionParser;
import com.consol.citrus.selenium.config.xml.StartActionParser;
import com.consol.citrus.selenium.config.xml.StopActionParser;
import com.consol.citrus.selenium.config.xml.SwitchWindowActionParser;
import com.consol.citrus.selenium.config.xml.ValidateActionParser;
import com.consol.citrus.selenium.config.xml.VerifyTitleActionParser;
import com.consol.citrus.selenium.config.xml.VerifyUrlActionParser;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class TestcaseNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("validate", new ValidateActionParser());
        registerBeanDefinitionParser("goto", new GotoActionParser());
        registerBeanDefinitionParser("start", new StartActionParser());
        registerBeanDefinitionParser("stop", new StopActionParser());
        registerBeanDefinitionParser("hover", new HoverActionParser());
        registerBeanDefinitionParser("screenshot", new ScreenshotActionParser());
        registerBeanDefinitionParser("click", new ClickActionParser());
        registerBeanDefinitionParser("getAttribute", new GetAttributeActionParser());
        registerBeanDefinitionParser("setInput", new SetInputActionParser());
        registerBeanDefinitionParser("browser", new BrowserActionParser());
        registerBeanDefinitionParser("extract", new ExtractActionParser());
        registerBeanDefinitionParser("extractList", new ExtractListActionParser());
        registerBeanDefinitionParser("gotoFrame", new GotoFrameActionParser());
        registerBeanDefinitionParser("getCookie", new GetCookieActionParser());
        registerBeanDefinitionParser("setCookie", new SetCookieActionParser());
        registerBeanDefinitionParser("delCookie", new DelCookieActionParser());
        registerBeanDefinitionParser("selectOption", new SelectOptionActionParser());
        registerBeanDefinitionParser("verifyUrl", new VerifyUrlActionParser());
        registerBeanDefinitionParser("switchWindow", new SwitchWindowActionParser());
        registerBeanDefinitionParser("getPagesource", new GetPageSourceActionParser());
        registerBeanDefinitionParser("getCurrentUrl", new GetCurrentUrlActionParser());
        registerBeanDefinitionParser("verifyTitle", new VerifyTitleActionParser());
    }
}
