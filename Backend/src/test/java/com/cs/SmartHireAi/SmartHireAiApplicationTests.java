package com.cs.SmartHireAi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SmartHireAiApplicationTests {

	@Test
	void contextLoads() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		System.out.println(
				bCryptPasswordEncoder.matches(
						"chirag",
						"$2a$10$Z/E49CtGCAUZ.rSTXayjD.XndjpYziOcJe/Ws4B/FwtZ8Zff1v3FW"
				)
		);
	}

}
