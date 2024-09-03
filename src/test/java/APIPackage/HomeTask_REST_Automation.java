package APIPackage;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import pagesPackage.BaseClass;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class HomeTask_REST_Automation
{
    @BeforeClass
    public void setup() {
        // Base URIs for the respective tasks
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }
    @Test(priority = 1,description = "Create Pet with the given JSON")
    public void createPet() {
        String requestBody = "{\n" +
                "  \"id\": 1207,\n" +
                "  \"category\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"dog\"\n" +
                "  },\n" +
                "  \"name\": \"snoopie\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"pending\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .extract()
                .response();

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        System.out.println("Response Code:----------->"+response.getStatusCode());
        System.out.println("------>"+response.getBody().asString());

        // Validate content type
        Assert.assertEquals(response.getContentType(), "application/json", "Content type should be application/json");

        // Validate response body for pet creation
        Assert.assertEquals(response.jsonPath().getInt("id"), 1207, "Pet ID should be 12345");
        Assert.assertEquals(response.jsonPath().getString("category.name"), "dog", "Category should be 'dog'");
        Assert.assertEquals(response.jsonPath().getString("name"), "snoopie", "Pet name should be 'snoopie'");
        Assert.assertEquals(response.jsonPath().getString("status"), "pending", "Status should be 'pending'");
    }

    @Test(priority = 2, dependsOnMethods = "createPet", description = "Validate details of the created pet.")
    public void task1_validatePetDetails() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/1207")
                .then()
                .extract()
                .response();

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        System.out.println("Response Code:----------->"+response.getStatusCode());
        System.out.println("------>"+response.getBody().asString());

        // Validate content type
        Assert.assertEquals(response.getContentType(), "application/json", "Content type should be application/json");

        // Validate pet details
        Assert.assertEquals(response.jsonPath().getString("category.name"), "dog", "Pet category should be 'dog'");
        Assert.assertEquals(response.jsonPath().getString("name"), "snoopie", "Pet name should be 'snoopie'");
        Assert.assertEquals(response.jsonPath().getString("status"), "pending", "Pet status should be 'pending'");
    }

    @Test(priority = 3,description = "Verify the item details")
    public void task2_validateUserDetails() {
        // Switch the base URI for the second task
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        System.out.println("Response Code:----------->"+response.getStatusCode());
        System.out.println("------>"+response.getBody().asString());

        // Validate there are more than 3 users
        List<Map<String, ?>> users = response.jsonPath().getList("$");
        Assert.assertTrue(users.size() > 3, "There should be more than 3 users");

        // Validate that one of the users has the name "Ervin Howell"
        boolean userFound = users.stream()
                .anyMatch(user -> user.get("name").equals("Ervin Howell"));
        Assert.assertTrue(userFound, "User with name 'Ervin Howell' should be present");
    }

}
