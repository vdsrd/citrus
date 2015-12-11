/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.ws;

import com.consol.citrus.annotations.CitrusXmlTest;
import com.consol.citrus.testng.AbstractTestNGCitrusTest;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 * @since 2009
 */
public class SendSoapMessageWithAttachmentITest extends AbstractTestNGCitrusTest {
    @Test
    @CitrusXmlTest
    public void SendSoapMessageWithAttachmentITest() {}
    
    @Test
    @CitrusXmlTest
    public void SendSoapMessageWithMtomAttachmentITest() {}
	
    @Test
    @CitrusXmlTest
	public void SendSoapMessageWithAttachmentInTemplateITest() {}
}