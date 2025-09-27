package br.com.fiap.appSori;

import br.com.fiap.appSori.config.security.filter.SecurityFilter;
import br.com.fiap.appSori.repository.*;
import br.com.fiap.appSori.service.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
class AppSoriApplicationTests {
	// 1. MOCK DO BEAN PRINCIPAL FALTANTE
	@MockBean
	private MongoTemplate mongoTemplate;

	// 2. MOCK DE TODOS OS REPOSITÓRIOS DEPENDENTES
	// Isso garante que o Spring não tente injetar o 'mongoTemplate'
	// real em nenhum dos seus repositórios durante o teste de contexto.
	@MockBean
	private CheckInDiarioRepository checkInDiarioRepository;

	@MockBean
	private OrganizacaoRepository organizacaoRepository;

	@MockBean
	private RelacionamentoRepository relacionamentoRepository;

	@MockBean
	private ResultadoRepository resultadoRepository;

	@MockBean
	private TentativaRepository tentativaRepository;

	@MockBean
	private TesteRepository testeRepository;

	@MockBean
	private UsuarioRepository usuarioRepository;


	@Test
	void contextLoads() {
		// Se o contexto carregar sem erros, o teste passa.
	}
}
