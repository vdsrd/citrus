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
import com.consol.citrus.exceptions.CitrusRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Action that performs browser actions.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class BrowserAction extends AbstractWebAction {

    private String action;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Calling browser action: {}", action);
            }
            Method actionMethod = webClient.getBrowserActions().getClass().getMethod(action);
            actionMethod.invoke(webClient.getBrowserActions());
        } catch (InvocationTargetException e) {
            logger.error("An exception was thrown by the action '" + action + "' in "
                    + webClient.getBrowserActions().getClass().getCanonicalName() + ": " + e.getTargetException().getMessage(), e);
            throw new CitrusRuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            logger.error("An exception was thrown by the action '" + action + "' in "
                    + webClient.getBrowserActions().getClass().getCanonicalName() + ": " + e.getMessage());
            throw new CitrusRuntimeException(e);
        } catch (IllegalArgumentException e) {
            logger.error("An exception was thrown by the action '" + action + "' in "
                    + webClient.getBrowserActions().getClass().getCanonicalName() + ": " + e.getMessage());
            throw new CitrusRuntimeException(e);
        } catch (NoSuchMethodException e) {
            logger.error("An exception was thrown by the action '" + action + "' in "
                    + webClient.getBrowserActions().getClass().getCanonicalName() + ": " + e.getMessage());
            throw new CitrusRuntimeException(e);
        } catch (SecurityException e) {
            logger.error("An exception was thrown by the action '" + action + "' in "
                    + webClient.getBrowserActions().getClass().getCanonicalName() + ": " + e.getMessage());
            throw new CitrusRuntimeException(e);
        }
    }

    /**
     * @return the action name.
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action name.
     */
    public void setAction(String action) {
        this.action = action;
    }
}
