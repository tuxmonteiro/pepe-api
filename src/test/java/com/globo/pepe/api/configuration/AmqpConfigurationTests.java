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

package com.globo.pepe.api.configuration;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AmqpConfiguration.class, loader = AnnotationConfigContextLoader.class)
@TestPropertySource(properties = {
        "amqp.url=amqp://guest:guest@127.0.0.1"
})
public class AmqpConfigurationTests {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void connectionFactoryIsNotMock() {
        Assert.assertFalse(((AbstractConnectionFactory)connectionFactory).getRabbitConnectionFactory() instanceof MockConnectionFactory);
    }

    @Test
    public void connectionFactoryNotNullTest() {
        assertNotNull(connectionFactory);
    }

}
