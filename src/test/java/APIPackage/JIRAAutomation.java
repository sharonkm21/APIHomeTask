package APIPackage;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JIRAAutomation
{

    private String issueId;
    private final String jiraBaseUrl = "https://jirapractice2308.atlassian.net";
    private final String apiToken = "ATATT3xFfGF05PhGxczMeLkxPVOltOPCWgl8QiviLM5b-7n5XSv01QFfolk3aG0wCY97M5ZfPfqzL48UQRf5s0pqzemko8N66xdki41pl2KluUxUguDB-2Uqz3MJGCYdOdBBwj7_T47YddeJf3akthdM0IUvUkxFYXp1WjQn5gZ-ksEnndyutRI=4B348B15";
    private final String userEmail = "sharonkm17496@gmail.com";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = jiraBaseUrl;
        RestAssured.baseURI = jiraBaseUrl;
        RestAssured.authentication = RestAssured.preemptive().basic(userEmail, apiToken);

       /* String auth = userEmail + ":" + apiToken;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        RestAssured.authentication = RestAssured.preemptive().basic(userEmail, apiToken);*/
    }

    @Test(priority = 1,description = "Create the defect in Jira")
    public void createDefect() {
        String requestBody  = "{\n" +
                "    \"fields\": {\n" +
                "        \"project\": {\n" +
                "            \"key\": \"SCRUM\"\n" +
                "        },\n" +
                "        \"summary\": \"Sample Defect\",\n" +
                "        \"description\": \"Creating a defect via REST API\",\n" +
                "        \"issuetype\": {\n" +
                "            \"name\": \"Bug\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/rest/api/2/issue");

        // Print the response
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        // Validate the response
        if (response.getStatusCode() == 201) {
            System.out.println("Issue created successfully!");
        } else {
            System.out.println("Failed to create issue. Status Code: " + response.getStatusCode());
        }

        issueId = response.jsonPath().getString("id");
    }

    @Test(priority = 2, dependsOnMethods = "createDefect",description = "Update the Defect using defect ID")
    public void updateDefect() {
        String updateIssueJson = "{\n" +
                "    \"fields\": {\n" +
                "        \"summary\": \"Updated Defect Summary\",\n" +
                "        \"description\": \"Updated description via REST API\"\n" +
                "    }\n" +
                "}";

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(updateIssueJson)
                .put("/rest/api/2/issue/" + issueId)
                .then()
                .statusCode(204)
                .extract()
                .response();

        System.out.println("Updated issue with ID: " + issueId);
    }

    @Test(priority = 3, dependsOnMethods = "createDefect",description = "Search the Defect created")
    public void searchDefect() {
        Response response = RestAssured
                .given()
                .queryParam("jql", "id=" + issueId)
                .header("Content-Type", "application/json")
                .get("/rest/api/2/search")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String searchResultId = response.jsonPath().getString("issues[0].id");
        Assert.assertEquals(searchResultId, issueId, "Search result ID should match the created issue ID");
        System.out.println("Found issue with ID: " + searchResultId);
    }

    // Step 4: Add an attachment to the issue
    @Test(priority = 4, dependsOnMethods = "createDefect",description = "Add an attachment to the issue")
    public void addAttachment() {
        File file = new File("src/main/resources/defect_sample.txt");

        Response response = RestAssured
                .given()
                .header("X-Atlassian-Token", "no-check")
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", file)
                .post("/rest/api/2/issue/" + issueId + "/attachments")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertTrue(response.jsonPath().getList("filename").contains(file.getName()), "File should be attached to the issue");
        System.out.println("Attached file to issue ID: " + issueId);
    }

    // Step 5: Delete the defect created in step 1
    @Test(priority = 5, dependsOnMethods = "createDefect")
    public void deleteDefect() {
        RestAssured
                .given()
                .delete("/rest/api/2/issue/" + issueId)
                .then()
                .statusCode(204);

        System.out.println("Deleted issue with ID: " + issueId);
    }
}
