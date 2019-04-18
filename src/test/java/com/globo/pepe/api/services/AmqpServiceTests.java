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

import com.globo.pepe.api.configuration.AmqpConfiguration;
import com.globo.pepe.api.mocks.AmqpMockConfiguration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {AmqpService.class, AmqpMockConfiguration.class}, excludeAutoConfiguration = AmqpConfiguration.class)
public class AmqpServiceTests {

    @Autowired
    public AmqpService amqpService;

    @Ignore
    @Test
    public void sendMessageTest() throws InterruptedException {
        String queueName="test";
        String originalMessage="message";
        amqpService.newQueue(queueName);
        amqpService.startListeners(queueName);
        CountDownLatch latch = new CountDownLatch(1);
        amqpService.registerListener(queueName, message -> {
            assertEquals("not original message", originalMessage, new String(message.getBody()));
            latch.countDown();
        });

        amqpService.convertAndSend(queueName, originalMessage);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        amqpService.stopListener(queueName);
    }
}
