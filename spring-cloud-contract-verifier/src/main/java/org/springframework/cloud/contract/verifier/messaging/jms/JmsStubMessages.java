/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.contract.verifier.messaging.jms;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

class JmsStubMessages implements MessageVerifier<Message> {

	private final JmsTemplate jmsTemplate;

	JmsStubMessages(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void send(Message message, String destination) {
		jmsTemplate.convertAndSend(destination, message, new ReplyToProcessor());
	}

	@Override
	public Message receive(String destination, long timeout, TimeUnit timeUnit) {
		jmsTemplate.setReceiveTimeout(timeout);
		return jmsTemplate.receive(destination);
	}

	@Override
	public Message receive(String destination) {
		return jmsTemplate.receive(destination);
	}

	@Override
	public void send(Object payload, Map headers, String destination) {
		jmsTemplate.convertAndSend(destination, payload, new ReplyToProcessor());
	}

}

class ReplyToProcessor implements MessagePostProcessor {

	@Override
	public Message postProcessMessage(Message message) throws JMSException {
		message.setStringProperty("requiresReply", "no");
		return message;
	}

}
