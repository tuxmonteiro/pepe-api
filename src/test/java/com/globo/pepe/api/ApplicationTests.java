package com.globo.pepe.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "pepe.logging.tags=default",
    "spring.pid.file=/tmp/pepe.pid",
	"pepe.stackstorm.api=http://127.0.0.1:9000/api",
	"pepe.stackstorm.auth=http://127.0.0.1:9000/auth",
	"pepe.stackstorm.stream=http://127.0.0.1:9000/stream"
})
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
