import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pojo.Praga;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class SidagroTest {
    private static String token;

    @BeforeAll
    public static void setUp(){
        baseURI = "https://gateway-sidagro-homolog.dac.ufla.br";

        token = given()
                .body("{\n" +
                        "   \"cpf\": \"10487412656\",\n" +
                        "   \"senha\": \"12345678\"\n" +
                        "}")
                .contentType(ContentType.JSON)
            .when()
                .post("auth/login")
            .then()
                .log().all()
                .extract()
                    .path("token");
    }

    @Test
    public void testeCicloDeVidaPraga(){
        //POST: Cria o registro e extrai o ID da resposta

        Praga newPraga = new Praga("Teste Popular", "Teste Científico", "ATIVO");
        int id = given()
                .header("Authorization", token)
                .body(newPraga)
                .contentType(ContentType.JSON)
            .when()
                .post("/praga")
            .then()
                .log().all()
                .assertThat()
                    .statusCode(201)
                .extract()
                .path("id");

        //GET: Busca o registro criado para conferir se os dados foram salvos corretamente (status code 200)
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
            .when()
                .get("/praga/" + id)
            .then()
                .log().all()
                .assertThat()
                    .statusCode(200)
                    .body("nomePopular", is("Teste Popular"));

        //PUT: Altera algum campo e verifica se a alteração persistiu
        Praga putPraga = new Praga("Teste Popular alterado", "Teste Científico alterado", "ATIVO");
        given()
                .header("Authorization", token)
                .body(putPraga)
                .contentType(ContentType.JSON)
            .when()
                .put("/praga/" + id)
            .then()
                .log().all()
                .assertThat()
                    .statusCode(200)
                    .body("nomeCientifico", is("Teste Científico alterado"));

        //DELETE: Remove o registro
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
            .when()
                .delete("/praga/" + id)
            .then()
                .log().all()
                .assertThat()
                    .statusCode(204);

        //GET: Valida que agora o retorno é 404 (Not Found), garantindo que o delete funcionou de verdade.
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
            .when()
                .get("/praga/" + id)
            .then()
                .log().all()
                .assertThat()
                    .statusCode(404);

    }

    // POST sem Token: Valida se a API retorna 403 Unauthorized
    @Test
    public void testWhithoutToken(){
        Praga newPraga = new Praga("Teste Popular", "Teste Científico", "ATIVO");
        given()
                .header("Authorization", "sem token")
                .body(newPraga)
                .contentType(ContentType.JSON)
            .when()
                .post("/praga")
            .then()
                .log().all()
                .assertThat()
                    .statusCode(403);
    }

    //POST: Campos Obrigatórios Vazios
    @Test
    public void testWithoutLabels(){
        Praga newPraga = new Praga("", "", "INVALIDO");

        given()
                .header("Authorization", token)
                .body(newPraga)
                .contentType(ContentType.JSON)
            .when()
                .post("/praga")
            .then()
                .log().all()
                .assertThat()
                    .statusCode(400); // Tá retornando 500, pela minha pesquisa isso é um bug e eu deveria reportar
    }
}