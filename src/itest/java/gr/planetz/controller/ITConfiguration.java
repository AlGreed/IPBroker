package gr.planetz.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import gr.planetz.Application;

@Profile("itest")
@Configuration
@Import({Application.class})
public class ITConfiguration {

}
