package com.example.sigurnostprojekat;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class SigurnostProjekatApplication {

	public static void main(String[] args) throws Exception{
		SpringApplication.run(SigurnostProjekatApplication.class, args);
//		KeyPair keyPair = Crypto.generateRSAKeyPair();
//		String privateKey = Crypto.keyToString(keyPair.getPrivate());
//		String publicKey = Crypto.keyToString(keyPair.getPublic());
//		System.out.println("Public Key:" + publicKey);
//		System.out.println("Private Key:" + privateKey);
	}

	@Bean
	public ModelMapper modelMapper(){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		return modelMapper;
	}
}
