package com.markjidon.weatherapp;


import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AppiumTest {


    AndroidDriver<MobileElement> driver;

//    public static void main(String[] args) throws IOException {
//
//        FileReader cityNames = new FileReader("C:\\Users\\marka\\AndroidStudioProjects\\WeatherApp\\app\\testData\\cityNames.txt");
//        BufferedReader input = new BufferedReader(cityNames);
//        List<String> cityName = new ArrayList<>();
//        String s;
//        while((s = input.readLine()) != null) {
//            cityName.add(s);
//            System.out.println(cityName);
//        }
//        input.close();
//
//        String[] arr = cityName.toArray(new String[]{});
//    }

    @BeforeMethod
    public void setup() throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities();

        dc.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
        dc.setCapability("platformName", "android");
        dc.setCapability("appPackage", "com.markjidon.weatherapp");
        dc.setCapability("appActivity", ".MainActivity");
        dc.setCapability("automationName", "uiautomator2");
        dc.setCapability("noReset", true);
        driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), dc);

    }

    @Test
    public void testCityNameExists() throws InterruptedException {
        MobileElement searchBar = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/et_dataInput");
        MobileElement reportBody = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/rL_reportBody");
        MobileElement tomorrow = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/tv_tomorrow");

        String[] cityNames = new String[]{"Pune", "Kuala Lumpur", "London", "New York"};

        for(int i = 0; i < cityNames.length; i++) {
            searchBar.click();
            searchBar.sendKeys(cityNames[i]);
            Thread.sleep(500);
            driver.executeScript("mobile: performEditorAction", ImmutableMap.of("action", "search"));
            Thread.sleep(3000);
            reportBody.click();
            Thread.sleep(2500);
            tomorrow.click();
            reportBody.click();
            MobileElement resultText = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/tv_result");
            Assert.assertEquals(resultText.getText(), "Success");
            Thread.sleep(1000);
        }
    }

    @Test
    public void unsupportedCityNames() throws InterruptedException {
        MobileElement searchBar = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/et_dataInput");

        String[] unsupportedCityNames = new String[]{"UnsupportedCityName", "a", "1!@#%^&*())_+"};
        //Actual Test
        for(int i = 0; i < unsupportedCityNames.length; i++) {
            searchBar.click();
            searchBar.sendKeys(unsupportedCityNames[i]);
            Thread.sleep(500);
            driver.executeScript("mobile: performEditorAction", ImmutableMap.of("action", "search"));
            Thread.sleep(2500);
            MobileElement resultText = (MobileElement) driver.findElementById("com.markjidon.weatherapp:id/tv_result");
            Assert.assertEquals(resultText.getText(), "Location not found");
            Thread.sleep(1000);
        }
    }

    @AfterMethod
    public void endTest() {
        driver.quit();
    }
}
