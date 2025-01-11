package com.aadi.bank.the_first_bank.env;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @Bean
    public Dotenv loadEnv() {
        Dotenv dotenv = Dotenv.load(); // Load the .env file

        // Optionally, you can print the value to verify if it's loaded correctly
        System.out.println(dotenv.get("USER_EMAIL"));
        return dotenv;
    }
}
