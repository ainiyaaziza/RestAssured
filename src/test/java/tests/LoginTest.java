package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class LoginTest {
    @Test
    public void testValidLogin() {
        baseURI = "http://127.0.0.1:5000/api";

        Response res = given()
                .header("Content-Type", "application/json")
                .body("{\"username\":\"qa_user\", \"password\":\"password123\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        String token = res.jsonPath().getString("token");
        Assert.assertTrue(token.startsWith("TOKEN_QA_USER_SECRET"));
    }
}
