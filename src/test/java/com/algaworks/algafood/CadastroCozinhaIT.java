package com.algaworks.algafood;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroCozinhaIT {

	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private CozinhaRepository cozinhaRepository;

	private Cozinha cozinhaTest;
	private int cozinhaIdInexistente;
	private int totalCozinhas;

	@BeforeEach
	public void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";

		databaseCleaner.clearTables();
		prepararDados();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {
				given()
					.accept(ContentType.JSON)
				.when()
					.get()
				.then()
					.statusCode(HttpStatus.OK.value());
	}

	@Test
	public void deveConterTodasCozinhas_QuandoConsultarCozinhas() {
			given()
				.accept(ContentType.JSON)
			.when()
				.get()
			.then()
				.body("nome", hasSize(totalCozinhas));
	}

	@Test
	public void deveRetornarStatus201_QuandoCadastrarCozinha() {
		Cozinha cozinha = new Cozinha();
		cozinha.setNome("Chinesa");

		given()
				.body(cozinha)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.post()
			.then()
				.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		given()
			.pathParams("cozinhaId", cozinhaTest.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(cozinhaTest.getNome()));
	}

	@Test
	public void deveRetornarStatus400_QuandoConsultarCozinhaInexistente() {
		given()
			.pathParams("cozinhaId", cozinhaIdInexistente)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}

	private void prepararDados() {

		String[] cozinhasNomes = {"Tailandesa", "Americana", "Brasileira", "Italiana"};

		List<Cozinha> cozinhas = Arrays.stream(cozinhasNomes)
				.map(nome -> {
					Cozinha cozinha = new Cozinha();
					cozinha.setNome(nome);
					cozinhaRepository.save(cozinha);
					return cozinha;
				}).toList();

		if(cozinhas.size() > 0) {
			this.cozinhaTest = cozinhas.get(0);
			this.cozinhaIdInexistente = (cozinhas.size() + 1);
			this.totalCozinhas = cozinhas.size();
		}
	}
}