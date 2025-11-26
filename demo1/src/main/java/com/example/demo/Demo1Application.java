package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class Demo1Application {

	public static void main(String[] args) {
		System.out.println("Demo1Application started");

		// اگر می‌خوای متدهای مشابه Spring اجرا شوند، می‌توانی آنها را اینجا فراخوانی کنی
		runApplication();
	}

	private static void runApplication() {
		// شبیه‌سازی کاری که SpringApplication.run() انجام می‌دهد
		System.out.println("Application is running (pure Java version)");
		// اینجا می‌توانی کدهای business logic خودت را اضافه کنی
	}
}
