package com.helloworld.commerce

import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.exitProcess


@SpringBootApplication
class MysqlApplication

fun main(args: Array<String>) {
//    val context = runApplication<MysqlApplication>(*args)
//    val exitCode = SpringApplication.exit(context, ExitCodeGenerator { 0 })
//    exitProcess(exitCode)
    runApplication<MysqlApplication>(*args)
}
