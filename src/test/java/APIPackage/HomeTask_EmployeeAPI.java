package APIPackage;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HomeTask_EmployeeAPI
{
    private String baseUrl = "https://dummy.restapiexample.com/api/v1";
    private int initialEmployeeCount;
    private int createdEmployeeId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = baseUrl;
    }

    @Test(priority = 1,description = "Get Count of all Employees")
    public void getAllEmployeesAndVerifyCount() {
        Response response = RestAssured.get("/employees");
        System.out.println(response.getBody().asString());
        System.out.println(response.jsonPath().getList("data"));
        initialEmployeeCount = response.jsonPath().getList("data").size();

        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(priority = 2,description = "Create Employee",dependsOnMethods = "getAllEmployeesAndVerifyCount")
    public void createEmployeeAndVerify() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "John Doe");
        requestParams.put("salary", "5000");
        requestParams.put("age", "30");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestParams.toString())
                .post("/create");
        System.out.println(response.getBody().asString());
        Assert.assertEquals(response.getStatusCode(), 200);

        // Verify the employee is created successfully
        createdEmployeeId = response.jsonPath().getInt("data.id");
        Assert.assertNotEquals(createdEmployeeId, 0);

        // Verify the employee count increased by 1
        Response allEmployeesResponse = RestAssured.get("/employees");
        int updatedEmployeeCount = allEmployeesResponse.jsonPath().getList("data").size();
        Assert.assertEquals(updatedEmployeeCount, initialEmployeeCount + 1);
    }

    @Test(priority = 3,description = "Get details of Employee",dependsOnMethods = "createEmployeeAndVerify")
    public void getCreatedEmployeeAndVerifyDetails() {
        Response response = RestAssured.get("/employee/" + createdEmployeeId);
        System.out.println(response.getBody().asString());
        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200);

        // Verify employee details
        Assert.assertEquals(response.jsonPath().getString("data.name"), "John Doe");
        Assert.assertEquals(response.jsonPath().getString("data.salary"), "5000");
        Assert.assertEquals(response.jsonPath().getString("data.age"), "30");
    }

    @Test(priority = 4,description = "Update Employee Details",dependsOnMethods = "getCreatedEmployeeAndVerifyDetails")
    public void updateEmployeeDetailsAndVerify() {
        // JSON Payload to update the employee details
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "John Doe Updated");
        requestParams.put("salary", "6000");
        requestParams.put("age", "31");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestParams.toString())
                .put("/update/" + createdEmployeeId);
        System.out.println(response.getBody().asString());
        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(priority = 5,dependsOnMethods = "updateEmployeeDetailsAndVerify")
    public void getUpdatedEmployeeAndVerifyDetails() {
        Response response = RestAssured.get("/employee/" + createdEmployeeId);
        System.out.println(response.getBody().asString());
        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200);

        // Verify updated employee details
        Assert.assertEquals(response.jsonPath().getString("data.name"), "John Doe Updated");
        Assert.assertEquals(response.jsonPath().getString("data.salary"), "6000");
        Assert.assertEquals(response.jsonPath().getString("data.age"), "31");
    }

    @Test(priority = 6,dependsOnMethods = "updateEmployeeDetailsAndVerify")
    public void deleteEmployeeAndVerify() {
        Response response = RestAssured.delete("/delete/" + createdEmployeeId);
        System.out.println(response.getBody().asString());
        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200);

        // Verify the employee count decreased by 1
        Response allEmployeesResponse = RestAssured.get("/employees");
        int finalEmployeeCount = allEmployeesResponse.jsonPath().getList("data").size();
        Assert.assertEquals(finalEmployeeCount, initialEmployeeCount);
    }
}
