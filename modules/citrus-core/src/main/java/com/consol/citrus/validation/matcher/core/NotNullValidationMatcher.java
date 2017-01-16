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
package com.consol.citrus.validation.matcher.core;

import java.util.List;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.validation.matcher.ValidationMatcher;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class NotNullValidationMatcher implements ValidationMatcher {

    /**
     * @see com.consol.citrus.validation.matcher.ValidationMatcher#validate(java.lang.String, java.lang.String, java.util.List, com.consol.citrus.context.TestContext)
     */
    @Override
    public void validate(String fieldName, String value, List<String> controlParameters, TestContext context) {
        if (value == null) {
            throw new ValidationException(this.getClass().getSimpleName() + " failed for field '" + fieldName
                    + "'. Received value '" + value + "' must contain '" + controlParameters + "'");
        }
    }
}
