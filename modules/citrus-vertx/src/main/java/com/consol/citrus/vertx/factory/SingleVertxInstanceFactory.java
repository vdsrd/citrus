/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.vertx.factory;

import com.consol.citrus.vertx.endpoint.VertxEndpointConfiguration;
import org.vertx.java.core.Vertx;

/**
 * Vertx instance factory creates a new instance only once and holds reuses this single instance all the time.
 *
 * @author Christoph Deppisch
 * @since 1.4.1
 */
public class SingleVertxInstanceFactory extends AbstractVertxInstanceFactory {

    /** Vert.x instance */
    private Vertx vertx;

    @Override
    public final Vertx newInstance(VertxEndpointConfiguration endpointConfiguration) {
        if (vertx == null) {
            vertx = createVertx(endpointConfiguration);
        }

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return vertx;
    }

    /**
     * Sets the Vert.x instance.
     * @param vertx
     */
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
}
