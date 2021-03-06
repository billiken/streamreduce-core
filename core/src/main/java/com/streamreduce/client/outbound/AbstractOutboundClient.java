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

package com.streamreduce.client.outbound;

import com.streamreduce.core.model.OutboundConfiguration;
import com.streamreduce.core.model.messages.SobaMessage;
import com.streamreduce.rest.dto.response.SobaMessageResponseDTO;
import net.sf.json.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * <p>Author: Nick Heudecker</p>
 * <p>Created: 7/5/12 12:55 PM</p>
 */
public abstract class AbstractOutboundClient implements OutboundClient {

    protected static final String RAW_PAYLOAD_PREFIX = "raw/";
    protected static final String PROCESSED_PREFIX = "processed/";
    protected static final String INSIGHT_PREFIX = "insight/";

    protected final OutboundConfiguration outboundConfiguration;

    AbstractOutboundClient(OutboundConfiguration outboundConfiguration) {
        if (outboundConfiguration == null) {
            throw new IllegalArgumentException("Outbound configuration cannot be null.");
        }
        this.outboundConfiguration = outboundConfiguration;
    }

    protected byte[] convertSobaMessageToDTOAsBytes(SobaMessage sobaMessage) throws IOException {
        return new ObjectMapper().writeValueAsBytes(SobaMessageResponseDTO.fromSobaMessage(sobaMessage,true));
    }

    protected byte[] convertSobaMessageToDTOAsBytes(SobaMessageResponseDTO sobaMessage) throws IOException {
        return new ObjectMapper().writeValueAsBytes(sobaMessage);
    }

    String createRawPayloadKey(JSONObject payload) {
        //TODO: use an MD5 for a more "dependable" hash for uniqueness, but using hashcode for now.
        //No "id" on a raw message, so use a timestamp + a hash of the contents to uniquely identify.
        return RAW_PAYLOAD_PREFIX + this.outboundConfiguration.getOriginatingConnection().getId() + "/" +
                Long.toString(System.currentTimeMillis()) + "-" + Integer.toString(payload.hashCode());
    }

    String createProcessedMessagePath(SobaMessage message) {
        return PROCESSED_PREFIX + message.getConnectionId() + "/" + message.getId();
    }

    String createInsightMessagePath(SobaMessage message) {
        return INSIGHT_PREFIX + message.getConnectionId() + "/" + message.getId();
    }

    String createProcessedMessagePath(SobaMessageResponseDTO message) {
        return PROCESSED_PREFIX + message.getConnectionId() + "/" + message.getId();
    }

    String createInsightMessagePath(SobaMessageResponseDTO message) {
        return INSIGHT_PREFIX + message.getConnectionId() + "/" + message.getId();
    }

}
