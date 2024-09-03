package APIPackage;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HomeTask4_Weather
{
    private String apiKey = "7527f9803323c46e189e820a989dd392";
    private double latitude;
    private double longitude;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://api.openweathermap.org/data/2.5";
    }

    @Test(priority = 1,description = "Get the current weather of Hyderabad.")
    public void getCurrentWeatherOfHyderabad() {
        // Make GET request to fetch weather data for Hyderabad
        Response response = RestAssured
                .given()
                .queryParam("q", "hyderabad")
                .queryParam("appid", apiKey)
                .when()
                .get("/weather")
                .then()
                .statusCode(200)  // Assert that status code is 200
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        latitude = jsonPath.getDouble("coord.lat");
        longitude = jsonPath.getDouble("coord.lon");

        Assert.assertEquals(jsonPath.getString("name"), "Hyderabad", "City name should be Hyderabad");
        Assert.assertEquals(jsonPath.getString("sys.country"), "IN", "Country code should be IN");
        System.out.println(jsonPath.getString("name")+":"+jsonPath.getString("sys.country"));
    }

    @Test(priority = 2, dependsOnMethods = "getCurrentWeatherOfHyderabad",description = "Get the weather of the location using latitude and longitude.")
    public void verifyWeatherDetailsUsingCoordinates() {
        Response response = RestAssured
                .given()
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", apiKey)
                .when()
                .get("/weather")
                .then()
                .statusCode(200)  // Assert that status code is 200
                .extract()
                .response();

        // Parse the response to JsonPath
        JsonPath jsonPath = response.jsonPath();

        // Assertions
        Assert.assertEquals(jsonPath.getString("name"), "Hyderabad", "City name should be Hyderabad");
        Assert.assertEquals(jsonPath.getString("sys.country"), "IN", "Country code should be IN");
        Assert.assertTrue(jsonPath.getFloat("main.temp_min") > 0, "Minimum temperature should be greater than 0");
        Assert.assertTrue(jsonPath.getFloat("main.temp") > 0, "Temperature should be greater than 0");
        System.out.println(jsonPath.getString("name")+":"+jsonPath.getString("sys.country"));
        System.out.println(jsonPath.getFloat("main.temp_min"));
        System.out.println(jsonPath.getFloat("main.temp"));
    }

}
