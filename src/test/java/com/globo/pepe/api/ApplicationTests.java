package com.globo.pepe.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "pepe.logging.tags=default"
})
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
