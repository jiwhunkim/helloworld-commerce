package com.helloworld.commerce.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	fun mysqlContainer(): org.testcontainers.mysql.MySQLContainer {
		return org.testcontainers.mysql.MySQLContainer(DockerImageName.parse("mysql:latest")).apply {
			withUrlParam("characterEncoding", "UTF-8")
			withUrlParam("useUnicode", "true")
			withUrlParam("sslMode", "DISABLED")
			withUrlParam("useSSL", "false")
			withUrlParam("serverTimezone", "Asia/Seoul")
		}
	}

}
