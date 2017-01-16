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

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;

/**
 * Action to take screenshots.
 * 
 * @author VASCO Data Security
 * @since 2.7
 */
public class ScreenshotAction extends AbstractWebAction {
    private String testcaseStep;

    /**
     * @see com.consol.citrus.selenium.action.AbstractWebAction#doExecute(com.consol.citrus.context.TestContext)
     */
    @Override
    public void doExecute(TestContext context) {
        webClient.getBrowser().takeScreenshot(context.getVariable(Citrus.TEST_NAME_VARIABLE), testcaseStep);
    }

    /**
     * @return The test case step.
     */
    public String getTestcaseStep() {
        return testcaseStep;
    }

    /**
     * @param step Test case step to be set.
     */
    public void setTestcaseStep(String step) {
        this.testcaseStep = step;
    }

}
