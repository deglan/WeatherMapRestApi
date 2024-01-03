package RestAssured;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicRestAssuredTestWithYaml {

    private static RequestSpecification reqSpec;
    private static ResponseSpecification resSpec;
    private static List<Map<String, String>> locations;

    @BeforeAll
    public static void setUp() {
        Yaml yaml = new Yaml();
        try (InputStream in = BasicRestAssuredTestWithYaml.class
                .getResourceAsStream("/weatherApiTestConfig.yaml")) {
            Map<String, Object> config = yaml.load(in);
            reqSpec = new RequestSpecBuilder()
                    .setBaseUri((String) config.get("baseUri"))
                    .addQueryParam("appid", (String) config.get("apiKey"))
                    .build();
            resSpec = new ResponseSpecBuilder()
                    .expectStatusCode(200)
                    .expectContentType(ContentType.JSON)
                    .build();
            locations = (List<Map<String, String>>) config.get("locations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Stream<Map<String, String>> provideLocations() {
        return locations.stream();
    }

    @ParameterizedTest
    @MethodSource("provideLocations")
    public void shouldGETWeatherForLocation(Map<String, String> location) {
        given().spec(reqSpec)
                .queryParam("id", location.get("id"))
                .log().all()
                .when()
                .get("/data/2.5/forecast")
                .then()
                .spec(resSpec)
                .log().body()
                .body("city.name", equalTo(location.get("city")));
    }
}
