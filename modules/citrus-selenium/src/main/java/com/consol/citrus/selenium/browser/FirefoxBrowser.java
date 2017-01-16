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
package com.consol.citrus.selenium.browser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.consol.citrus.selenium.client.WebClientConfiguration;

/**
 * @author VASCO Data Security
 * @since 2.7
 */
public class FirefoxBrowser extends AbstractBrowser {

    /**
     * @param config WebClientConfiguration object.
     */
    public FirefoxBrowser(WebClientConfiguration config) {
        super(config);
    }
    
    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createLocalWebDriver()
     */
    @Override
    protected WebDriver createLocalWebDriver() {
        DesiredCapabilities capabilities = createDesiredCapabilities(super.webClientConfiguration);
        updateDesiredCapabilityConfiguration(capabilities);
        return new FirefoxDriver(capabilities);
    }

    /**
     * @see com.consol.citrus.selenium.browser.AbstractBrowser#createDesiredCapabilities(com.consol.citrus.selenium.client.WebClientConfiguration)
     */
    @Override
    protected DesiredCapabilities createDesiredCapabilities(WebClientConfiguration config) {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, createFireFoxProfile());
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return capabilities;
    }

    private FirefoxProfile createFireFoxProfile() {
        FirefoxProfile fp = new FirefoxProfile();
    
        fp.setAcceptUntrustedCertificates(true);
        fp.setAssumeUntrustedCertificateIssuer(false);
    
        /* set custom download folder */
        fp.setPreference("browser.download.dir", createTemporaryStorage().toFile().getAbsolutePath());
    
        /* default download folder, set to 2 to use custom download folder */
        fp.setPreference("browser.download.folderList", 2);
    
        /* comma separated list if MIME types to save without asking */
        fp.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/plain");
    
        /* do not show download manager */
        fp.setPreference("browser.download.manager.showWhenStarting", false);
    
        fp.setEnableNativeEvents(true);
    
        return fp;
    }
    
    private Path createTemporaryStorage() {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("selenium");
            tempDir.toFile().deleteOnExit();
            if (logger.isDebugEnabled()) {
                logger.debug("Download storage location is: " + tempDir.toString());
            }
        } catch (IOException e) {
            logger.warn("Could not create temporary storage", e);
        }
        return tempDir;
    }

}
