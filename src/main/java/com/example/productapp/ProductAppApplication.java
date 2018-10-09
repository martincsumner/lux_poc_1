package com.example.productapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
public class ProductAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductAppApplication.class, args);
	}
}

@RestController
class ProductController {

	@RequestMapping(value="/products", method= RequestMethod.GET)
	public String getProducts(){
		return Arrays.asList("iPad","iPhone","iPod").toString();
	}

}
