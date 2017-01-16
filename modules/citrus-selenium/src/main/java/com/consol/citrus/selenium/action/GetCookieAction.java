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
 * Action to retrieve a cookie.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class GetCookieAction extends AbstractWebAction {
    private String cookie;
    private String variable;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("GetCookie variable='" + variable + "', cookie='" + cookie + "'");
        }

        String value = webClient.getBrowser().getCookieValue(cookie);
        if (value != null) {
            context.setVariable(variable, value);
            if (logger.isDebugEnabled()) {
                logger.debug("Set variable '" + variable + "' with value '" + value + "'");
            }
        }
    }

    /**
     * @return variable
     */
    public String getVariable() {
        return variable;
    }

    /**
     * Set variable to store cookie value in.
     * 
     * @param variable
     */
    public void setVariable(String variable) {
        this.variable = variable;
    }

    /**
     * @return the cookie name
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * @param cookie The cookie name to set
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

}
