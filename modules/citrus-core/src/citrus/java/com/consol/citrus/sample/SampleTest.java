package com.consol.citrus.sample;

import com.consol.citrus.annotations.CitrusXmlTest;
import org.testng.annotations.Test;

import com.consol.citrus.testng.AbstractTestNGCitrusTest;

/**
 * This is a sample test
 *
 * @author Christoph
 * @since 2011-09-05
 */
public class SampleTest extends AbstractTestNGCitrusTest {
    @Test
    @CitrusXmlTest(name = "SampleTest")
    public void sampleTest() {
    }
}
