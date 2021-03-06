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

package com.consol.citrus.jms.endpoint;

import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.*;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.correlation.ObjectStore;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

/**
 * @author Christoph Deppisch
 */
public class JmsEndpointSyncProducerTest extends AbstractTestNGUnitTest {

    private ConnectionFactory connectionFactory = org.easymock.EasyMock.createMock(ConnectionFactory.class);
    private Connection connection = EasyMock.createMock(Connection.class);
    private Session session = EasyMock.createMock(Session.class);
    private Destination destination = EasyMock.createMock(Destination.class);
    private Queue destinationQueue = EasyMock.createMock(Queue.class);
    private MessageConsumer messageConsumer = EasyMock.createMock(MessageConsumer.class);
    private MessageProducer messageProducer = EasyMock.createMock(MessageProducer.class);
    private Queue replyDestinationQueue = EasyMock.createMock(Queue.class);
    private TemporaryQueue tempReplyQueue = EasyMock.createMock(TemporaryQueue.class);

    private int retryCount = 0;
    
    @Test
    public void testSendMessageWithReplyDestination() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestination(destination);
        endpoint.getEndpointConfiguration().setReplyDestination(replyDestinationQueue);
        
        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        Map<String, Object> responseHeaders = new HashMap<String, Object>();
        TextMessage jmsResponse = new TextMessageImpl("<TestResponse>Hello World!</TestResponse>", responseHeaders);
        
        reset(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        expect(connectionFactory.createConnection()).andReturn(connection).once();
        connection.start();
        expectLastCall().once();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session).once();

        expect(session.createConsumer(replyDestinationQueue, "JMSCorrelationID = '123456789'")).andReturn(messageConsumer).once();
        expect(messageConsumer.receive(anyLong())).andReturn(jmsResponse).once();
        
        expect(session.createProducer(destination)).andReturn(messageProducer).once();
        messageProducer.send((TextMessage)anyObject());
        expectLastCall().once();
        
        expect(session.createTextMessage("<TestRequest><Message>Hello World!</Message></TestRequest>")).andReturn(
                new TextMessageImpl("<TestRequest><Message>Hello World!</Message></TestRequest>", new HashMap<String, Object>()));
        
        replay(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        endpoint.createProducer().send(message, context);
        
        verify(connectionFactory, destination, connection, session, messageConsumer, messageProducer);
    }
    
    @Test
    public void testSendMessageWithReplyDestinationName() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestinationName("myDestination");
        endpoint.getEndpointConfiguration().setReplyDestinationName("replyDestination");
        
        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        Map<String, Object> responseHeaders = new HashMap<String, Object>();
        TextMessage jmsResponse = new TextMessageImpl("<TestResponse>Hello World!</TestResponse>", responseHeaders);
        
        reset(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        expect(connectionFactory.createConnection()).andReturn(connection).once();
        connection.start();
        expectLastCall().once();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session).once();

        expect(session.createQueue("replyDestination")).andReturn(replyDestinationQueue).once();
        
        expect(session.createConsumer(replyDestinationQueue, "JMSCorrelationID = '123456789'")).andReturn(messageConsumer).once();
        expect(messageConsumer.receive(anyLong())).andReturn(jmsResponse).once();
        
        expect(session.createProducer(destinationQueue)).andReturn(messageProducer).once();
        messageProducer.send((TextMessage)anyObject());
        expectLastCall().once();
        
        expect(session.createTextMessage("<TestRequest><Message>Hello World!</Message></TestRequest>")).andReturn(
                new TextMessageImpl("<TestRequest><Message>Hello World!</Message></TestRequest>", new HashMap<String, Object>()));
        
        expect(session.createQueue("myDestination")).andReturn(destinationQueue).once();
        
        replay(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        endpoint.createProducer().send(message, context);
        
        verify(connectionFactory, destination, connection, session, messageConsumer, messageProducer);
    }
    
    @Test
    public void testSendMessageWithTemporaryReplyDestination() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestination(destination);
        
        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        Map<String, Object> responseHeaders = new HashMap<String, Object>();
        TextMessage jmsResponse = new TextMessageImpl("<TestResponse>Hello World!</TestResponse>", responseHeaders);
        
        reset(connectionFactory, destination, connection, session, messageConsumer, messageProducer, tempReplyQueue);

        expect(connectionFactory.createConnection()).andReturn(connection).once();
        connection.start();
        expectLastCall().once();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session).once();

        expect(session.createTemporaryQueue()).andReturn(tempReplyQueue).once();
        
        expect(session.createConsumer(tempReplyQueue)).andReturn(messageConsumer).once();
        expect(messageConsumer.receive(anyLong())).andReturn(jmsResponse).once();
        
        expect(session.createProducer(destination)).andReturn(messageProducer).once();
        messageProducer.send((TextMessage)anyObject());
        expectLastCall().once();
        
        expect(session.createTextMessage("<TestRequest><Message>Hello World!</Message></TestRequest>")).andReturn(
                new TextMessageImpl("<TestRequest><Message>Hello World!</Message></TestRequest>", new HashMap<String, Object>()));
        
        tempReplyQueue.delete();
        expectLastCall().once();
        
        replay(connectionFactory, destination, connection, session, messageConsumer, messageProducer, tempReplyQueue);

        endpoint.createProducer().send(message, context);
        
        verify(connectionFactory, destination, connection, session, messageConsumer, messageProducer, tempReplyQueue);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testSendMessageWithReplyHandler() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestination(destination);
        endpoint.getEndpointConfiguration().setReplyDestination(replyDestinationQueue);
        
        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        Map<String, Object> responseHeaders = new HashMap<String, Object>();
        TextMessage jmsResponse = new TextMessageImpl("<TestResponse>Hello World!</TestResponse>", responseHeaders);
        
        reset(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        expect(connectionFactory.createConnection()).andReturn(connection).once();
        connection.start();
        expectLastCall().once();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session).once();

        expect(session.createConsumer(replyDestinationQueue, "JMSCorrelationID = '123456789'")).andReturn(messageConsumer).once();
        expect(messageConsumer.receive(anyLong())).andReturn(jmsResponse).once();
        
        expect(session.createProducer(destination)).andReturn(messageProducer).once();
        messageProducer.send((TextMessage)anyObject());
        expectLastCall().once();
        
        expect(session.createTextMessage("<TestRequest><Message>Hello World!</Message></TestRequest>")).andReturn(
                new TextMessageImpl("<TestRequest><Message>Hello World!</Message></TestRequest>", new HashMap<String, Object>()));
        
        replay(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        endpoint.createProducer().send(message, context);

        verify(connectionFactory, destination, connection, session, messageConsumer, messageProducer);
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testSendMessageWithReplyMessageCorrelator() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestination(destination);
        endpoint.getEndpointConfiguration().setReplyDestination(replyDestinationQueue);

        MessageCorrelator correlator = new DefaultMessageCorrelator();
        endpoint.getEndpointConfiguration().setCorrelator(correlator);

        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        Map<String, Object> responseHeaders = new HashMap<String, Object>();
        TextMessage jmsResponse = new TextMessageImpl("<TestResponse>Hello World!</TestResponse>", responseHeaders);
        
        reset(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        expect(connectionFactory.createConnection()).andReturn(connection).once();
        connection.start();
        expectLastCall().once();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session).once();

        expect(session.createConsumer(replyDestinationQueue, "JMSCorrelationID = '123456789'")).andReturn(messageConsumer).once();
        expect(messageConsumer.receive(anyLong())).andReturn(jmsResponse).once();
        
        expect(session.createProducer(destination)).andReturn(messageProducer).once();
        messageProducer.send((TextMessage)anyObject());
        expectLastCall().once();
        
        expect(session.createTextMessage("<TestRequest><Message>Hello World!</Message></TestRequest>")).andReturn(
                new TextMessageImpl("<TestRequest><Message>Hello World!</Message></TestRequest>", new HashMap<String, Object>()));
        
        replay(connectionFactory, destination, connection, session, messageConsumer, messageProducer);

        endpoint.createProducer().send(message, context);
        
        verify(connectionFactory, destination, connection, session, messageConsumer, messageProducer);
    }
    
    @Test
    public void testSendEmptyMessage() throws JMSException {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setConnectionFactory(connectionFactory);

        endpoint.getEndpointConfiguration().setDestination(destination);
        
        try {
            endpoint.createProducer().send(null, context);
        } catch(IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Message is empty - unable to send empty message");
            return;
        }
        
        Assert.fail("Missing " + CitrusRuntimeException.class + " because of sending empty message");
    }

    @Test
    public void testOnReplyMessage() {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();

        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createProducer();
        jmsSyncProducer.getCorrelationManager().saveCorrelationKey(
                endpoint.getEndpointConfiguration().getCorrelator().getCorrelationKeyName(jmsSyncProducer.getName()),
                jmsSyncProducer.toString(), context);
        jmsSyncProducer.getCorrelationManager().store(jmsSyncProducer.toString(), message);

        Assert.assertEquals(jmsSyncProducer.receive(context), message);
    }

    @Test
    public void testOnReplyMessageWithCorrelatorKey() {
        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();

        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createProducer();
        jmsSyncProducer.getCorrelationManager().store(new DefaultMessageCorrelator().getCorrelationKey(message), message);

        Assert.assertEquals(jmsSyncProducer.receive(new DefaultMessageCorrelator().getCorrelationKey(message), context), message);
    }

    @Test
    public void testReplyMessageRetries() {
        retryCount = 0;

        final Message message = new DefaultMessage("<TestRequest><Message>Hello World!</Message></TestRequest>");

        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();

        ((JmsSyncProducer)endpoint.createProducer()).getCorrelationManager().setObjectStore(new ObjectStore<Message>() {
            @Override
            public void add(String correlationKey, Message object) {
            }

            @Override
            public Message remove(String correlationKey) {
                retryCount++;
                if (retryCount == 5) {
                    return message;
                } else {
                    return null;
                }
            }
        });

        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createConsumer();
        jmsSyncProducer.getCorrelationManager().saveCorrelationKey(
                endpoint.getEndpointConfiguration().getCorrelator().getCorrelationKeyName(jmsSyncProducer.getName()),
                jmsSyncProducer.toString(), context);

        Assert.assertEquals(retryCount, 0);
        Assert.assertEquals(jmsSyncProducer.receive(context, 2500), message);
        Assert.assertEquals(retryCount, 5);
    }

    @Test
    public void testReplyMessageRetriesExceeded() {
        retryCount = 0;

        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setPollingInterval(300L);

        ((JmsSyncProducer)endpoint.createProducer()).getCorrelationManager().setObjectStore(new ObjectStore<Message>() {
            @Override
            public void add(String correlationKey, Message object) {
            }

            @Override
            public Message remove(String correlationKey) {
                retryCount++;
                return null;
            }
        });


        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createConsumer();
        jmsSyncProducer.getCorrelationManager().saveCorrelationKey(
                endpoint.getEndpointConfiguration().getCorrelator().getCorrelationKeyName(jmsSyncProducer.getName()),
                jmsSyncProducer.toString(), context);

        Assert.assertEquals(retryCount, 0);
        try {
            jmsSyncProducer.receive(context, 800);
            Assert.fail("Missing action timeout exception");
        } catch (ActionTimeoutException e) {
            Assert.assertEquals(retryCount, 4);
        }
    }

    @Test
    public void testIntervalGreaterThanTimeout() {
        retryCount = 0;

        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setPollingInterval(1000L);

        ((JmsSyncProducer)endpoint.createProducer()).getCorrelationManager().setObjectStore(new ObjectStore<Message>() {
            @Override
            public void add(String correlationKey, Message object) {
            }

            @Override
            public Message remove(String correlationKey) {
                retryCount++;
                return null;
            }
        });

        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createConsumer();
        jmsSyncProducer.getCorrelationManager().saveCorrelationKey(
                endpoint.getEndpointConfiguration().getCorrelator().getCorrelationKeyName(jmsSyncProducer.getName()),
                jmsSyncProducer.toString(), context);

        Assert.assertEquals(retryCount, 0);
        try {
            jmsSyncProducer.receive(context, 250);
            Assert.fail("Missing action timeout exception");
        } catch (ActionTimeoutException e) {
            Assert.assertEquals(retryCount, 2);
        }
    }

    @Test
    public void testZeroTimeout() {
        retryCount = 0;

        JmsSyncEndpoint endpoint = new JmsSyncEndpoint();
        endpoint.getEndpointConfiguration().setPollingInterval(1000L);

        ((JmsSyncProducer)endpoint.createProducer()).getCorrelationManager().setObjectStore(new ObjectStore<Message>() {
            @Override
            public void add(String correlationKey, Message object) {
            }

            @Override
            public Message remove(String correlationKey) {
                retryCount++;
                return null;
            }
        });


        JmsSyncProducer jmsSyncProducer = (JmsSyncProducer)endpoint.createConsumer();
        jmsSyncProducer.getCorrelationManager().saveCorrelationKey(
                endpoint.getEndpointConfiguration().getCorrelator().getCorrelationKeyName(jmsSyncProducer.getName()),
                jmsSyncProducer.toString(), context);

        Assert.assertEquals(retryCount, 0);
        try {
            jmsSyncProducer.receive(context, 0);
            Assert.fail("Missing action timeout exception");
        } catch (ActionTimeoutException e) {
            Assert.assertEquals(retryCount, 1);
        }
    }
}
