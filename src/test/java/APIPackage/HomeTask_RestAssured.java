package APIPackage;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import pagesPackage.BaseClass;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class HomeTask_RestAssured extends BaseClass
{
    private static final Logger log = LoggerFactory.getLogger(HomeTask_RestAssured.class);

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test(priority = 1,description = "Verify number of resources")
    public void verifyNumberOfResources() {
        String[] resources = {"/posts", "/comments", "/albums", "/photos", "/todos", "/users"};

        for (String resource : resources) {
            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(resource);

            // Verify status code
            Assert.assertEquals(response.getStatusCode(), 200);
            System.out.println("Response Code:----------->"+response.getStatusCode());
            // Print the number of resources
            System.out.println("Number of resources at " + resource + ": " + response.jsonPath().getList("$").size());
        }
    }

    @Test(priority = 2, description = "Get specific resource and verify status code and status body.")
    public void testGetSpecificResource() {
        String[] resources = {"/posts/1", "/comments/1", "/albums/1", "/photos/1", "/todos/1", "/users/1"};

        for (String resource : resources) {
            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(resource);

            // Verify status code
            Assert.assertEquals(response.getStatusCode(), 200);
            System.out.println("Response Code:----------->"+response.getStatusCode());
            // Verify the response body is not null
            Assert.assertNotNull(response.getBody().asString(), "Response body is null for resource: " + resource);
            System.out.println("------>"+response.getBody().asString());
        }
    }

    @Test(priority = 3,description = "Modifying a specific resource")
    public void testModifySpecificResource() {
        String[] resources = {"/posts/1", "/comments/1", "/albums/1", "/photos/1", "/todos/1", "/users/1"};

        for (String resource : resources) {
            Map<String, Object> jsonBody = new HashMap<>();
            jsonBody.put("title", "Modified Title");

            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .put(resource);

            // Verify status code
            Assert.assertEquals(response.getStatusCode(), 200);
            System.out.println("Response Code:----------->"+response.getStatusCode());
            // Verify the response body
            Assert.assertTrue(response.getBody().asString().contains("Modified Title"),
                    "Response body does not contain updated title for resource: " + resource);

            System.out.println("------>"+response.getBody().asString());

        }
    }

    @Test(priority = 4,description = "Creating a new resource.")
    public void testCreateNewResource() {
        String[] resources = {"/posts", "/comments", "/albums", "/photos", "/todos", "/users"};

        for (String resource : resources) {
            Map<String, Object> jsonBody = new HashMap<>();
            jsonBody.put("title", "New Resource Title");
            jsonBody.put("body", "This is the body of the new resource.");
            jsonBody.put("userId", 1207);

            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(resource);

            // Verify status code
            Assert.assertEquals(response.getStatusCode(), 201);
            System.out.println("Response Code:----------->"+response.getStatusCode());
            // Verify the response body contains the new title
            Assert.assertTrue(response.getBody().asString().contains("New Resource Title"),
                    "Response body does not contain the new resource title for resource: " + resource);
            System.out.println("------>"+response.getBody().asString());
        }
    }

    @Test(priority = 5, description = "Deleting a specific resource")
    public void testDeleteSpecificResource() {
        String[] resources = {"/posts/1207", "/comments/1207", "/albums/1207", "/photos/1207", "/todos/1207", "/users/1207"};

        for (String resource : resources) {
            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete(resource);

            // Verify status code
            Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("Response Code:----------->"+response.getStatusCode());
            // Verify the response body (should be empty after delete)
            Assert.assertEquals(response.getBody().asString(), "{}",
                    "Response body is not empty after deletion for resource: " + resource);
            System.out.println("Response Body----------->"+response.getBody().asString());
        }
    }
}
