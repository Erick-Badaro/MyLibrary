package com.projetogs.mylibrary;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.projetogs.mylibrary.config.MongoTestConfig;

@SpringBootTest
@ActiveProfiles("test")
class MylibraryApplicationTests extends MongoTestConfig {

	@Test
	void contextLoads() {
	}

}
