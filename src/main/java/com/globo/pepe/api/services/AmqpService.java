/*
 * Copyright (c) 2019 - Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globo.pepe.api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmqpService {

    private final ConnectionFactory connectionFactory;
    private final AmqpTemplate template;
    private final AmqpAdmin admin;

    private final Map<String, SimpleMessageListenerContainer> messageListenerContainerMap = new HashMap<>();
    private final Map<String, List<MessageListener>> messageListeners = new HashMap<>();

    @Autowired
    public AmqpService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.template = new RabbitTemplate(connectionFactory);
        this.admin = new RabbitAdmin(connectionFactory);
    }

    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    public void convertAndSend(String queue, String message) {
        template.convertAndSend(queue, message);
    }

    public Object convertSendAndReceive(String queue, Object message) {
        return template.convertSendAndReceive(queue, message);
    }

    public String newQueue(String queueName) {
        final Queue queue = new Queue(queueName);
        final String declaredQueue = admin.declareQueue(queue);
        startListeners(queueName);
        return declaredQueue;
    }

    private MessageListener messageListener(String queueName) {
        return message -> {
            for (MessageListener messageListener: messageListeners.computeIfAbsent(queueName, k -> new ArrayList<>())) {
                messageListener.onMessage(message);
            }
        };
    }

    public void startListeners(String queueName) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueueNames(queueName);
        messageListeners.put(queueName, new ArrayList<>());
        container.setMessageListener(messageListener(queueName));
        messageListenerContainerMap.put(queueName, container);
    }

    public void stopListener(String queueName) {
        final SimpleMessageListenerContainer container;
        if ((container = messageListenerContainerMap.get(queueName)) != null) {
            container.shutdown();
            messageListeners.remove(queueName);
            messageListenerContainerMap.remove(queueName);
        }
    }

    public void registerListener(String queueName, MessageListener newMessageListener) {
        final List<MessageListener> listOfMessageListener = messageListeners.get(queueName);
        listOfMessageListener.add(newMessageListener);
        messageListeners.put(queueName, listOfMessageListener);
    }

}
