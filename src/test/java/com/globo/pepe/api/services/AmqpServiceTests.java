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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.globo.pepe.api.mocks.AmqpMockConfiguration;
import com.globo.pepe.common.services.AmqpService;
import com.globo.pepe.common.services.JsonLoggerService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {AmqpService.class, AmqpMockConfiguration.class, JsonLoggerService.class, ObjectMapper.class}, loader = AnnotationConfigContextLoader.class)
public class AmqpServiceTests {

    @Autowired
    public AmqpService amqpService;

    @Test
    public void connectionFactoryIsMock() {
        assertTrue(((AbstractConnectionFactory)amqpService.connectionFactory()).getRabbitConnectionFactory() instanceof MockConnectionFactory);
    }

    @Test
    public void convertAndSendTest() throws InterruptedException {
        String queueName="test";
        String originalMessage="message";
        amqpService.newQueue(queueName);
        amqpService.prepareListenersMap(queueName);
        CountDownLatch latch = new CountDownLatch(1);
        amqpService.registerListener(queueName, message -> {
            assertEquals("not original message", originalMessage, new String(message.getBody()));
            latch.countDown();
        });

        amqpService.convertAndSend(queueName, originalMessage, 10000);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        amqpService.stopListener(queueName);
    }

    @Test(expected = IllegalStateException.class)
    public void stopListenerWithoutQueueTest() {
        amqpService.stopListener("not-found");
    }
}
