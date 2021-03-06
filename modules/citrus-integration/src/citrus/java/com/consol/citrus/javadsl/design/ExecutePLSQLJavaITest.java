/*
 * Copyright 2006-2013 the original author or authors.
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

package com.consol.citrus.javadsl.design;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * @author Christoph Deppisch
 */
@Test
public class ExecutePLSQLJavaITest extends TestNGCitrusTestDesigner {
    
    @Autowired
    @Qualifier("testDataSource")
    private DataSource dataSource;
    
    @CitrusTest
    public void executePLSQLAction() {
        plsql(dataSource)
            .sqlResource("classpath:com/consol/citrus/actions/plsql.sql")
            .ignoreErrors(true);
        
        plsql(dataSource)
            .sqlScript("BEGIN\n" +
                            "EXECUTE IMMEDIATE 'create or replace function test (v_id in number) return number is\n" +
                              "begin\n" +
                               "if v_id  is null then\n" +
                                "return 0;\n" +
                                "end if;\n" +
                                "return v_id;\n" +
                              "end;';\n" +
                        "END;\n" +
                        "/")
            .ignoreErrors(true);
    }
}