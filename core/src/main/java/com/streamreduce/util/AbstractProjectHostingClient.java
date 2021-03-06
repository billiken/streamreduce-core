/*
 * Copyright 2012 Nodeable Inc
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.streamreduce.util;

import java.util.Date;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.streamreduce.core.model.Connection;

/**
 * Abstract class to be extended by all implementations of a client to an external project hosting service.
 */
public abstract class AbstractProjectHostingClient extends ExternalIntegrationClient {

    // TODO: Think about how to tune this
    protected final Cache<String, Object> requestCache = CacheBuilder.newBuilder()
                                                                     .concurrencyLevel(32)
                                                                     .weakValues()
                                                                     .build();

    private String baseUrl;
    private Date lastActivityPollDate;

    /**
     * Creates a client to the external service identified by the connection passed in.
     *
     * @param connection the connection to use for this client
     */
    public AbstractProjectHostingClient(Connection connection) {
        super(connection);

        baseUrl = connection.getUrl();
        lastActivityPollDate = connection.getLastActivityPollDate();
    }

    /**
     * Returns the base URL configured for the connection.
     *
     * @return the connection's base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the {@link Date} representing the last activity item processed.
     *
     * @return the date of the last activity processed
     */
    public Date getLastActivityPollDate() {
        return lastActivityPollDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUp() {
        requestCache.invalidateAll();
        requestCache.cleanUp();
    }

}
