package APIPackage;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pagesPackage.BaseClass;

import java.util.List;

public class HomeTask_Four_REST_Automation
{
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://events.epam.com/api/v2";
    }

    @Test(priority = 1,description = "Event names which are in English(En) language ")
    public void verifyEnglishEventNames() {
        // Expected event names in English
        List<String> expectedEventNames = List.of("Event 1", "Event 3");

        // Send GET request to the /events endpoint
        Response response = RestAssured
                .given()
                .when()
                .get("/events")
                .then()
                .statusCode(200)  // Verify that the status code is 200
                .extract()
                .response();

        // Parse response to get the event names where the language is 'En'
        JsonPath jsonPath = response.jsonPath();
        List<String> englishEventNames = jsonPath.getList("events.findAll { it.language == 'En' }.name");

        // Verify that the extracted event names match the expected list
        Assert.assertEquals(englishEventNames, expectedEventNames, "The event names do not match the expected list");

        // Print out the event names to confirm
        System.out.println("English event names: " + englishEventNames);
    }
}
