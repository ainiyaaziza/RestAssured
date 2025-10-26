package tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UpdateProductTest {
    private static String authToken;
    private static int productId;

    @BeforeClass
    public void setupAndCreateProduct() {
        baseURI = "http://127.0.0.1:5000/api";

        // Login and get token
        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"username\":\"qa_user\",\"password\":\"password123\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        authToken = loginResponse.jsonPath().getString("token");

        // Create product to update
        String payload = """
            {
              "name": "Headset Test",
              "price": 99.99,
              "stock": 5
            }
        """;

        Response res = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract().response();

        productId = res.jsonPath().getInt("id");
    }

    @Test
    public void testUpdateProductWithAuth() {
        String updatePayload = """
            {
              "name": "Headset Test Updated",
              "price": 89.99,
              "stock": 15
            }
        """;

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(updatePayload)
                .when()
                .put("/products/" + productId)
                .then()
                .statusCode(200)
                .body("message", containsString("updated successfully"));
    }

    @Test
    public void testUpdateWithoutTokenShouldFail() {
        String updatePayload = """
            {
              "name": "NoAuth Update",
              "price": 10,
              "stock": 1
            }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatePayload)
                .when()
                .put("/products/" + productId)
                .then()
                .statusCode(403)
                .body("message", containsString("Unauthorized"));
    }
}
