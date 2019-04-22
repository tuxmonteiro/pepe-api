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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globo.pepe.api.services.JsonLoggerService.JsonLogger;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "pepe.logging.tags=default"
})
@ContextConfiguration(classes = {JsonLoggerService.class, ObjectMapper.class}, loader = AnnotationConfigContextLoader.class)
public class JsonLoggerServiceTests {

    @Autowired
    private JsonLoggerService jsonLoggerService;
    private JsonLogger logger;

    @Before
    public void setup() {
        this.logger = jsonLoggerService.newLogger(getClass());
    }

    @Test
    public void newJsonLoggerTest() {
        assertNotNull(jsonLoggerService.newLogger(getClass()));
    }

    @Test
    public void sendDebugDisabledTest() {
        Configurator.setLevel(getClass().getPackage().getName(), Level.INFO);
        String logStr = logger.put("test", "value").sendDebug();
        assertNull(logStr);
    }

    @Test
    public void sendDebugEnabledTest() {
        Configurator.setLevel(getClass().getPackage().getName(), Level.DEBUG);
        String logStr = logger.put("test", "value").sendDebug();
        Configurator.setLevel(getClass().getPackage().getName(), Level.INFO);
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendDebugDisabledWithThrowableTest() {
        Configurator.setLevel(getClass().getPackage().getName(), Level.INFO);
        Throwable throwable = new RuntimeException("debug");
        String logStr = logger.put("test", "value").sendDebug(throwable);
        assertNull(logStr);
    }

    @Test
    public void sendDebugEnabledWithThrowableTest() {
        Throwable throwable = new RuntimeException("debug");
        Configurator.setLevel(getClass().getPackage().getName(), Level.DEBUG);
        String logStr = logger.put("test", "value").sendDebug(throwable);
        Configurator.setLevel(getClass().getPackage().getName(), Level.INFO);
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("debug", JsonPath.parse(logStr).read("$.throwable_message"));
        assertEquals("sendDebugEnabledWithThrowableTest", JsonPath.parse(logStr).read("$.throwable_stack.stackTrace[0].methodName"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendInfoTest() {
        String logStr = logger.put("test", "value").sendInfo();
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendInfoWithThrowableTest() {
        Throwable throwable = new RuntimeException("info");
        String logStr = logger.put("test", "value").sendInfo(throwable);
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("info", JsonPath.parse(logStr).read("$.throwable_message"));
        assertEquals("sendInfoWithThrowableTest", JsonPath.parse(logStr).read("$.throwable_stack.stackTrace[0].methodName"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendWarnTest() {
        String logStr = logger.put("test", "value").sendWarn();
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendWarnWithThrowableTest() {
        Throwable throwable = new RuntimeException("warn");
        String logStr = logger.put("test", "value").sendWarn(throwable);
        System.out.println(logStr);
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("warn", JsonPath.parse(logStr).read("$.throwable_message"));
        assertEquals("sendWarnWithThrowableTest", JsonPath.parse(logStr).read("$.throwable_stack.stackTrace[0].methodName"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

    @Test
    public void sendErrorTest() {
        Throwable throwable = new RuntimeException("error");
        String logStr = logger.put("test", "value").sendError(throwable);
        assertEquals(getClass().getSimpleName(), JsonPath.parse(logStr).read("$.class"));
        assertEquals("error", JsonPath.parse(logStr).read("$.throwable_message"));
        assertEquals("sendErrorTest", JsonPath.parse(logStr).read("$.throwable_stack.stackTrace[0].methodName"));
        assertEquals("default", JsonPath.parse(logStr).read("$.tags"));
        assertEquals("value", JsonPath.parse(logStr).read("$.test"));
    }

}
