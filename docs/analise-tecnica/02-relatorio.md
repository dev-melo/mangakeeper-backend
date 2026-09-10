# Relatório técnico e prioridades

[Contexto](README.md) · [15 temas](01-analise-por-tema.md) · [Evidências e testes](03-validacao-e-testes.md)

## 1. Resumo executivo

O MangaKeeper tem uma arquitetura proporcional a um projeto pessoal e uma implementação legível de CRUD, autenticação e autorização. O build gera um JAR executável. A base permite evoluir para frontend e deploy sem reescrita estrutural.

A maturidade atual é de **protótipo funcional em estabilização para o primeiro deploy**. Há defeitos de entrada/autenticação e integridade reproduzidos em ambiente descartável: cadastro/login com senha vazia, PATCH aceitando volume negativo, duração JWT incorreta em UTC, exclusão de conta impedida por curtidas recebidas e respostas de erro sem contrato consistente.

As verificações de propriedade exercitadas bloquearam corretamente outro usuário. A constraint de curtidas também manteve apenas um registro em uma disputa concorrente. Preservar essas proteções e corrigir os caminhos incompletos é mais útil que adicionar novas camadas.

**Decisão:** não abrir cadastro público com a configuração e validação atuais. Corrigir os itens de segurança e contrato, preparar banco reproduzível e publicar primeiro em homologação. O frontend pode ser desenvolvido sobre contratos estabilizados enquanto melhorias futuras aguardam.

## 2. O que está bem feito

- 🟢 Controllers delegam regras aos services; repositories usam Spring Data diretamente.
- 🟢 DTOs com records evitam serialização acidental de entidades e suas relações.
- 🟢 Respostas públicas de usuário não incluem hash nem email.
- 🟢 BCrypt é utilizado para armazenar hash; a biblioteca JWT verifica assinatura, issuer e expiração.
- 🟢 Dono da coleção é derivado da autenticação, e criação de filhos verifica propriedade do pai.
- 🟢 Escritas de usuário/coleção/mangá/volume têm checagens de dono ou administrador.
- 🟢 Cadastro atribui `ROLE_USER` no servidor; cliente não define roles.
- 🟢 Curtidas e mangás têm constraints únicas com escopo de negócio.
- 🟢 Operações com várias alterações usam transações em vários caminhos relevantes.
- 🟢 Cloudinary está concentrado em uma integração pequena.
- 🟢 Existe OpenAPI e Maven Wrapper; classes permanecem pequenas e compreensíveis.

Esses pontos não garantem todos os cenários de produção, mas são fundamentos que vale preservar.

## 3. Principais problemas

### P01 — Chave JWT padrão permite deploy inseguro

**Problema:** `${JWT_SECRET:my-secret-key}` mantém a aplicação funcionando sem secret configurado.

**Severidade:** 🔴 Crítico quando a variável não é definida.

**Onde ocorre:** [application.properties](../../src/main/resources/application.properties), propriedade `api.security.token.secret`; [TokenService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/TokenService.java), emissão/verificação HMAC256.

**Evidência:** fato de código. A reprodução isolada utilizou uma chave fictícia explícita; não houve tentativa contra conta real.

**Por que importa:** conhecer a chave permite produzir token assinado com subject de usuário existente. O filtro carrega as permissões dessa conta no banco, inclusive de administrador se o subject for de um admin existente. Inventar apenas a claim roles não concede permissão por si só.

**Cenário em que quebra:** primeiro deploy com variável esquecida aceita a chave conhecida do repositório.

**Como corrigir:** remover fallback, exigir secret aleatório forte, validar configuração na inicialização e armazenar em configuração secreta da plataforma. Se um ambiente real já utilizou a chave padrão, substituí-la invalida os tokens assinados por ela.

**Prioridade:** agora, antes de qualquer exposição pública. Impacto muito alto / esforço baixo.

### P02 — Validação ineficaz permite dados e senhas inválidos

**Problema:** DTOs de cadastro/login não têm constraints; PATCHs de usuário, mangá e volume anotam o ID com `@Valid`, não o corpo.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [RegisterDTO](../../src/main/java/dev/flmelo/mangakeeper/backend/dto/security/RegisterDTO.java), [UsuarioController](../../src/main/java/dev/flmelo/mangakeeper/backend/controller/UsuarioController.java), [MangaController](../../src/main/java/dev/flmelo/mangakeeper/backend/controller/MangaController.java), [VolumeController](../../src/main/java/dev/flmelo/mangakeeper/backend/controller/VolumeController.java).

**Evidência:** confirmado cadastro `201` e login `200` com senha vazia; PATCH de senha de um caractere `200`; PATCH `numero=-2` persistiu e voltou no GET. Cadastro sem login gerou NPE no log. Ver casos no documento de validação.

**Por que importa:** hash não vazio não prova senha original válida. Constraints na entidade nem sempre existem ou são executadas antes de operações que podem falhar.

**Cenário em que quebra:** formulário envia vazio, cliente chama API diretamente ou frontend não aplica validação equivalente.

**Como corrigir:** completar constraints dos DTOs, colocar `@Valid` no corpo, alinhar regras de criação/edição e armazenamento. Definir limites de strings e positividade onde aplicável. Exemplo:

```java
@PatchMapping("/{id}")
public ResponseEntity<VolumeResponseDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateVolumeDTO request) {
    return ResponseEntity.ok(volumeService.update(id, request));
}
```

Essa mudança ativa constraints existentes; ainda é preciso completar as que faltam e mapear erros. Validar a senha original antes do hash, sem registrá-la; BCrypt não descriptografa.

**Prioridade:** agora, antes do cadastro público/frontend. Impacto muito alto / esforço baixo a médio.

### P03 — Falhas de autenticação não produzem `401` consistente

**Problema:** `validarToken` retorna string vazia; filtro consulta usuário vazio e lança exception. Não há fronteira uniforme para erros de autenticação.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [SecurityFilter](../../src/main/java/dev/flmelo/mangakeeper/backend/config/security/SecurityFilter.java), `doFilterInternal` e `recuperarToken`; [TokenService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/TokenService.java), `validarToken`; [WebSecurityConfig](../../src/main/java/dev/flmelo/mangakeeper/backend/config/security/WebSecurityConfig.java).

**Evidência:** JWT inválido em GET público, credencial incorreta e ausência de token em POST protegido retornaram `403` sem corpo no ambiente isolado. Log do token inválido mostrou `UserNotFoundException` no filtro.

**Por que importa:** frontend não distingue sessão expirada, falta de permissão e erro interno. `ControllerAdvice` não captura automaticamente exceptions lançadas antes do controller.

**Cenário em que quebra:** navegador conserva um token expirado e o envia até para páginas públicas.

**Como corrigir:** validar prefixo Bearer, tratar falha JWT explicitamente, limpar/encerrar autenticação com `401` e JSON estável, configurar entry point e denied handler. Definir política de token inválido em rota pública e testá-la. Retornar `403` para identidade válida sem permissão.

**Prioridade:** agora. Impacto alto / esforço baixo a médio.

### P04 — Expiração depende do timezone do servidor

**Problema:** `LocalDateTime.now().plusHours(2)` é interpretado como offset -03:00 mesmo quando a JVM roda em outro fuso.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [TokenService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/TokenService.java), `gerarDataExpiracao`.

**Evidência:** confirmado `exp - iat = 18000` segundos em JVM UTC, ou cinco horas.

**Por que importa:** amplia ou altera a janela de uso do token e contradiz o tempo esperado.

**Cenário em que quebra:** aplicação funciona em máquina -03:00, mas é publicada em servidor UTC.

**Como corrigir:** `Instant.now().plus(Duration.ofHours(2))`; idealmente usar `Clock` injetável para testes determinísticos e duração configurável se necessário.

**Prioridade:** agora. Impacto alto / esforço baixo.

### P05 — Banco vazio não permite cadastro e schema não é versionado

**Problema:** registro depende de `ROLE_USER` já existente; não há seed/migration. `ddl-auto=update` gere schema sem histórico controlado.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [AuthenticationService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/AuthenticationService.java), `register`; [application.properties](../../src/main/resources/application.properties).

**Evidência:** banco novo teve tabelas criadas, mas cadastro falhou e log indicou `Role não Encontrada`. Cadastro passou após seed explícito de roles no banco descartável.

**Por que importa:** deploy não é reproduzível; mudanças de banco ficam implícitas e difíceis de revisar/restaurar.

**Cenário em que quebra:** novo ambiente, clone de outro desenvolvedor ou evolução de schema com dados reais.

**Como corrigir:** migrations iniciais com schema/roles e `ddl-auto=validate` depois de introduzi-las. Um inicializador idempotente é alternativa temporária para roles, mas não substitui migrations. Não inserir senha de admin fixa.

**Prioridade:** agora, antes do deploy. Impacto muito alto / esforço médio.

### P06 — Excluir usuário com curtidas recebidas falha

**Problema:** remove curtidas feitas pelo usuário, mas não as curtidas que terceiros fizeram em suas coleções.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [UsuarioService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/UsuarioService.java), `delete`; relações de [Usuario](../../src/main/java/dev/flmelo/mangakeeper/backend/entity/Usuario.java), [Colecao](../../src/main/java/dev/flmelo/mangakeeper/backend/entity/Colecao.java) e [Curtida](../../src/main/java/dev/flmelo/mangakeeper/backend/entity/Curtida.java).

**Evidência:** confirmado erro de FK de curtidas para coleções; DELETE respondeu `403` vazio e conta/coleção/curtida permaneceram no banco (rollback).

**Por que importa:** exclusão de conta deixa de funcionar conforme o usuário recebe interações normais. A cascata JPA não chama `ColecaoService.delete`.

**Cenário em que quebra:** Ana cria coleção, Bruno curte, Ana exclui a conta.

**Como corrigir:** excluir curtidas feitas e recebidas pelas coleções antes do grafo, na mesma transação. Alternativa: cascatas SQL planejadas em migrations. Para pouco volume, operações explícitas são simples; bulk delete pode melhorar escala, mas exige atenção ao contexto de persistência.

**Prioridade:** antes de oferecer exclusão de conta. Impacto alto / esforço baixo a médio.

### P07 — Erros de integridade e concorrência são mascarados

**Problema:** violations de banco não têm contrato consistente; registro recria exception sem causa e declara toda integridade como usuário existente.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [RestExceptionHandler](../../src/main/java/dev/flmelo/mangakeeper/backend/infra/RestExceptionHandler.java), [AuthenticationService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/AuthenticationService.java), [CurtidaService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/CurtidaService.java), `curtirColecao`.

**Evidência:** email duplicado retornou `403` vazio; oito curtidas simultâneas produziram um `204`, quatro `409` e três `403`, com apenas uma linha no banco. Logs confirmaram duplicidade para as disputas de INSERT. A contagem e ordem de respostas depende do agendamento, não é contrato fixo.

**Por que importa:** proteção de dados funciona, mas cliente recebe “sem permissão” para conflitos ou falhas internas. Mesmo `403` possui corpo diferente conforme o caminho.

**Cenário em que quebra:** duas abas curtem ao mesmo tempo; cadastro repete email; exceção interna é encaminhada para tratamento padrão. O comportamento observado é compatível com erro não tratado seguido de dispatch `/error` protegido; instrumentar a cadeia para provar cada dispatch, caso necessário.

**Como corrigir:** mapear constraints conhecidas para conflitos específicos, uniformizar erros MVC e segurança, preservar causa, registrar erro inesperado com request ID. Não simplesmente liberar tudo para “corrigir 403”. Tratar configuração do dispatch de erro junto ao contrato e manter autorização normal.

**Prioridade:** agora. Impacto alto / esforço médio.

### P08 — Listagens não têm filtro, paginação ou ordenação estável

**Problema:** `findAll()` materializa base inteira; resumos incluem listas completas.

**Severidade:** 🟠 Importante para frontend e crescimento.

**Onde ocorre:** `getAll` dos [services](../../src/main/java/dev/flmelo/mangakeeper/backend/service) e resumos de `CurtidaService`.

**Evidência:** fato de código; não houve benchmark de carga.

**Por que importa:** frontend precisa filtrar localmente e recursos consumidos crescem com o banco.

**Cenário em que quebra:** mostrar volumes de um único mangá exige buscar todos; coleção popular retorna todos que curtiram.

**Como corrigir:** queries filtradas por usuário/coleção/mangá, Page ou Slice, limite de tamanho, ordenação com desempate e total separado de lista de curtidas quando útil.

**Prioridade:** antes de consolidar as telas do frontend. Impacto alto / esforço médio.

### P09 — Contratos e normalização divergem

**Problema:** nomes JSON misturados, Location incorreto, login normalizado só ao salvar e trim só na resposta de criação de mangá.

**Severidade:** 🟡 Melhoria recomendada; login/contratos devem ser corrigidos agora.

**Onde ocorre:** [DTOs](../../src/main/java/dev/flmelo/mangakeeper/backend/dto), [AuthenticationController](../../src/main/java/dev/flmelo/mangakeeper/backend/controller/AuthenticationController.java), `register`; [MangaService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/MangaService.java), `create`.

**Evidência:** cadastro de `MixedAudit` salvou `mixedaudit`, mas login com a capitalização enviada falhou. POST de mangá devolveu título sem espaços, GET preservou espaços. Location observado apontou `/auth/register/{id}`. Ver tabela de campos no documento por tema.

**Por que importa:** frontend precisa de exceções de mapeamento e vê comportamentos contraditórios.

**Cenário em que quebra:** usuário tenta entrar com o nome digitado no cadastro; cliente segue o Location; formulário reutiliza payload entre GET/PATCH.

**Como corrigir:** convenção JSON única, normalização antes da consulta e persistência, mesmas regras POST/PATCH, Location em `/usuarios/{id}`, OpenAPI fiel e `/usuarios/me` autenticado como melhoria de identidade.

**Prioridade:** agora, enquanto não há consumidores publicados. Impacto alto / esforço baixo a médio.

### P10 — Upload e rotas de conta precisam de limites de abuso

**Problema:** conta pode fazer uploads repetidos; não há quota/limitação na aplicação, validação de formato/conteúdo própria, propriedade persistida do ativo ou erro externo bem tratado.

**Severidade:** 🟠 Importante para uso público.

**Onde ocorre:** [UploadController](../../src/main/java/dev/flmelo/mangakeeper/backend/controller/UploadController.java), [CloudinaryService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/CloudinaryService.java), configuração de segurança.

**Evidência:** fato de código; upload anônimo foi bloqueado. Não foram utilizados Cloudinary real, quota ou testes de arquivo com conta externa. Há limites multipart padrão do Boot, portanto não é upload de tamanho ilimitado.

**Por que importa:** custo/armazenamento e threads podem ser consumidos por uso repetido; erro de rede pode deixar arquivo órfão.

**Cenário em que quebra:** cadastro público barato seguido de centenas de uploads, ou upload bem-sucedido seguido de falha ao salvar volume. Login/cadastro também expõem custo de BCrypt a tentativas repetidas.

**Como corrigir:** limites de frequência na aplicação ou proxy, quota por identidade, formatos/conteúdo/dimensões permitidos e tamanho explícito, timeout finito, JSON de upload com identificador e persistência da propriedade. MIME fornecido pelo cliente não basta como validação. Limpeza de órfãos pode vir depois.

**Prioridade:** limites antes da exposição pública; automação de limpeza depois. Impacto alto / esforço médio.

### P11 — Crescimento e edição concorrente sem proteções específicas

**Problema:** índices por FK ausentes em caminhos relevantes, relações eager nos resumos e falta de versão para conflitos de edição.

**Severidade:** 🟡 Melhoria recomendada; algumas ações são 🔵 futuras.

**Onde ocorre:** [entidades](../../src/main/java/dev/flmelo/mangakeeper/backend/entity), repositories e updates dos services.

**Evidência:** banco descartável só criou PK/uniques; não havia índices específicos de `colecoes.usuario_id`, `volumes.manga_id`, `curtidas.colecao_id`. Ausência de `@Version` é fato; N+1 e lost update são riscos sem medição/reprodução nesta rodada.

**Por que importa:** consultas/deletes podem ficar lentos e edição simultânea pode sobrescrever dados.

**Cenário em que quebra:** milhares de filhos por tabela ou duas abas editando a mesma entidade. O comportamento exato de atualização depende do SQL gerado.

**Como corrigir:** índices em migrations com queries reais como referência, medir SQL/EXPLAIN, projeções quando justificadas. `@Version` e versão esperada do cliente conforme semântica de edição. Evitar índices redundantes e locks pessimistas por padrão.

**Prioridade:** índices junto aos novos filtros; medição e locking conforme uso. Impacto médio / esforço baixo a médio.

### P12 — Testes de comportamento inexistentes no repositório

**Problema:** só há `contextLoads`, sem assertions sobre fluxos.

**Severidade:** 🟠 Importante.

**Onde ocorre:** [MangakeeperBackendApplicationTests](../../src/test/java/dev/flmelo/mangakeeper/backend/MangakeeperBackendApplicationTests.java).

**Evidência:** inventário do repositório. Ver resultado de execução e reproduções externas à suíte no documento de validação.

**Por que importa:** o contexto pode iniciar com bugs de senha, autorização ou persistência. Refatorações ficam sem proteção.

**Cenário em que quebra:** alteração de cascata ou DTO compila e só falha quando outra pessoa usa.

**Como corrigir:** dez testes priorizados com MockMvc, PostgreSQL isolado e mocks apenas de fronteiras externas. Incorporar ao CI. Não medir qualidade apenas por cobertura percentual.

**Prioridade:** junto das correções, antes de novas features relevantes. Impacto muito alto / esforço médio.

### P13 — Operação e setup dependem de conhecimento do autor

**Problema:** README mínimo, sem procedimento de deploy/backup, health check, correlação de erros ou pipeline; wrapper sem bit executável.

**Severidade:** 🟠 Importante para operação; detalhes de organização são 🟡.

**Onde ocorre:** [README](../../README.md), [application.properties](../../src/main/resources/application.properties), [mvnw](../../mvnw), inventário de arquivos.

**Evidência:** fatos do repositório. Não há prova sobre configurações externas da plataforma, ainda não escolhida.

**Por que importa:** subir outra instância ou recuperar um incidente exige improvisação.

**Cenário em que quebra:** banco indisponível, arquivo de configuração ausente, deploy em outra máquina ou perda de dados.

**Como corrigir:** documentar variáveis/roles/comandos, exemplo sem secrets, permitir execução do wrapper, configurar saúde mínima e logs com causa/request ID, TLS e backup restaurável. Docker é opcional; o JAR já serve para deploy.

**Prioridade:** mínimo operacional antes do deploy; dashboards/tracing depois. Impacto alto / esforço baixo a médio.

### P14 — Privacidade não é implementada por `publico`

**Problema:** todas as coleções são criadas públicas e a leitura não aplica regra de privacidade.

**Severidade:** 🔵 Evolução futura; 🟠 se o produto prometer privacidade.

**Onde ocorre:** [ColecaoService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/ColecaoService.java), `create/getAll/getById`, consultas de filhos e regras GET.

**Evidência:** fato de código; não há PATCH de visibilidade. Não foi demonstrado vazamento de uma coleção tornada privada pela API atual.

**Por que importa:** adicionar apenas um toggle no frontend criaria falsa expectativa de proteção.

**Cenário em que quebra:** futuro endpoint grava `publico=false`, mantendo leituras irrestritas.

**Como corrigir:** declarar MVP público; antes de privacidade, implementar leitura autorizada em todos os acessos diretos/listas/descendentes.

**Prioridade:** antes de implementar coleção privada, não bloqueia MVP público. Impacto condicionado / esforço médio.

### P15 — Troca de senha não revoga tokens existentes

**Problema:** JWT continua válido enquanto conta existe e assinatura/expiração conferem.

**Severidade:** 🟡 Melhoria recomendada.

**Onde ocorre:** [UsuarioService](../../src/main/java/dev/flmelo/mangakeeper/backend/service/UsuarioService.java), `updateUsuario`; `SecurityFilter`.

**Evidência:** após alterar a senha, token anterior criou outra coleção com `201`.

**Por que importa:** trocar senha não encerra o acesso de quem já obteve o token. Não é necessário implementar refresh token para resolver essa decisão.

**Cenário em que quebra:** pessoa altera senha tentando encerrar uma sessão comprometida.

**Como corrigir:** definir política; tokenVersion/credentialsChangedAt validado no filtro pode invalidar tokens anteriores. Usar ID imutável como subject no futuro também evita associar tokens antigos a usernames reutilizados após exclusão/recriação. Esse segundo cenário não foi reproduzido.

**Prioridade:** antes de prometer “sair de todos os dispositivos” ou recuperação segura de conta; pode ser próxima etapa do MVP. Impacto médio / esforço médio.

### P16 — Falta inventário automatizado e revisão de dependências

**Problema:** não há SCA/rotina de atualização versionada; versões explícitas e transitivas exigem acompanhamento.

**Severidade:** 🟡 Melhoria recomendada, com checagem antes do deploy.

**Onde ocorre:** [pom.xml](../../pom.xml) e ausência de CI.

**Evidência:** inventário do JAR e consulta limitada aos avisos oficiais, detalhada na análise por tema. Não houve scanner completo. Não classificar todo CVE por faixa de versão como explorável na aplicação.

**Por que importa:** uma biblioteca pode receber correção após o desenvolvimento e antes do deploy; requisitos de exploração precisam ser confrontados com o uso real.

**Cenário em que quebra:** dependência permanece sem acompanhamento enquanto o endpoint passa a usar um recurso vulnerável.

**Como corrigir:** inventário resolvido, scanner no CI e atualização compatível testada; preservar BOM em vez de sobrescrever transitivas indiscriminadamente.

**Prioridade:** checagem antes de publicação e acompanhamento periódico. Impacto médio a alto / esforço baixo a médio.

## 4. Problemas que provavelmente só aparecerão em produção

- Banco novo cria tabelas, mas cadastro falha por falta de role (P05).
- JVM UTC emite token de cinco horas (P04).
- Interações de terceiros impedem exclusão de conta (P06).
- Duas abas/retries disputam curtida e recebem erros diferentes (P07).
- Foto real excede limite padrão, e chamadas lentas ocupam threads (P10).
- Volume de dados torna listagens e cascatas caras (P08/P11).
- Frontend recebe `403` para erro de credencial, entrada nula e FK, além de falta de permissão (P03/P07).
- Proxy pode produzir host/esquema incorreto em Location se forwarded headers não forem configurados/testados (P13, risco não exercitado).
- Token preservado após troca de senha contradiz expectativa de encerrar acesso (P15).

Os cinco primeiros defeitos de código aplicáveis foram separados de riscos externos no documento de validação; nem todo item desta lista foi executado.

## 5. Problemas de escalabilidade

**Atuais:** findAll ilimitado, falta de filtro por pai, resumos com todos os usuários e cópia de upload para memória. Resolver antes que o cliente dependa desse contrato.

**Futuros:** cascatas grandes, queries adicionais por eager/lazy, pool compartilhado por instâncias, upload síncrono e conflitos de edição. Medir SQL/latência/quantidade de dados, não presumir gargalo de CPU genérico.

**Otimizações prematuras:** cache distribuído, microservices, mensageria, particionamento, troca de ORM e Kubernetes. O projeto ainda consegue obter grande benefício com consultas delimitadas e operação básica. Cenários de 10x/100x/1000x estão na seção 9 da análise por tema, sem promessas de capacidade não medidas.

## 6. Dívida técnica

| Dívida | Aceitável ou perigosa? | Motivo |
|---|---|---|
| Usuario conhece Spring Security/JPA | Aceitável | Baixo custo de manutenção no tamanho atual |
| Pastas por camada | Aceitável | Código pequeno e navegação previsível |
| DTOs mapeados à mão | Aceitável | Duplicação pequena; helper basta |
| Sem soft delete | Aceitável | Não há requisito de recuperação por item |
| Sem refresh token | Aceitável com contrato claro | Novo login pode atender MVP; não resolve sozinho revogação |
| Regras de validação divergentes | Perigosa | Permite estados inválidos já reproduzidos |
| Banco sem migrations/seed | Perigosa para deploy | Novo ambiente não atende cadastro |
| Erros mascarados | Perigosa | Prejudica UX e diagnóstico |
| Sem teste de comportamento | Perigosa | Regressões silenciosas |
| Falta de procedimento de recuperação | Perigosa com dados reais | Backup sem restauração validada não comprova recuperação |

## 7. Overengineering

Não há excesso arquitetural significativo. Pequenos excessos: wrappers sem regra (`clean` apenas faz trim), código antigo comentado, handlers quase idênticos e documentação de controller parcialmente repetitiva/desalinhada. Resolver localmente. Não há justificativa para remover a tabela de roles ou dividir `CurtidaService` apenas por estética.

## 8. Underengineering

Faltam validação real, tratamento de autenticação/erro, consistência de exclusão, migrations, testes de comportamento, limites de abuso, contrato de consulta e operação mínima. Esses são requisitos concretos de primeiro uso público; novas camadas e padrões não os substituem.

## 9. Top 10 melhorias

| Ranking | Melhoria | Impacto / esforço | Referências |
|---|---|---|---|
| 1 | Remover fallback JWT e aplicar validação de senha/DTO/PATCH | Muito alto / baixo a médio | P01/P02 |
| 2 | Corrigir duração JWT e tratamento `401` | Alto / baixo a médio | P03/P04 |
| 3 | Migrations, roles e banco reproduzível | Muito alto / médio | P05 |
| 4 | Corrigir exclusão com curtidas recebidas | Alto / baixo a médio | P06 |
| 5 | Padronizar erros de banco, MVC e concorrência | Alto / médio | P07 |
| 6 | JSON, normalização, Location e identidade atual | Alto / baixo a médio | P09 |
| 7 | Consultas por pai, paginação e índices úteis | Alto / médio | P08/P11 |
| 8 | Limites e contrato do upload/login/cadastro | Alto / médio | P10 |
| 9 | Dez testes críticos, CI e revisão de dependências | Muito alto / médio | P12/P16 |
| 10 | Setup/deploy documentado, saúde, logs e restauração | Alto / baixo a médio | P13 |

Esforço é relativo, não estimativa de dias. Testes devem acompanhar cada correção, não esperar todos os outros itens terminarem.

## 10. Roadmap

### Agora

1. Corrigir P01–P07 com testes dos cenários já reproduzidos.
2. Definir convenção JSON, erros e filtros para o frontend (P08/P09).
3. Definir limites de upload e rotas públicas; checar dependências (P10/P16).
4. Documentar configuração, executar em banco novo, configurar HTTPS/saúde/logs e validar restauração (P13).
5. Publicar homologação e exercitar cadastro → login → coleção → mangá → volume → curtida por outro usuário → exclusão.

Critério: não precisar inserir roles manualmente, não aceitar senha vazia/volume negativo, negar escrita de terceiros, distinguir `401`/`403`/`409`, preservar integridade e conseguir recuperar dados. O frontend pode começar sobre esse contrato; não há necessidade de concluir toda evolução futura.

### Depois

Recuperação de senha e política de revogação (P15), limpeza de imagens órfãs, métricas simples, medição de N+1/EXPLAIN e conflito otimista onde necessário. Melhorar documentação junto de cada mudança, sem reorganização geral obrigatória.

### Muito depois

Privacidade se entrar no produto (P14), catálogo compartilhado se exigido, reorganização por features quando houver dificuldade concreta, upload direto assinado e escala horizontal após medição. Não há recomendação atual de infraestrutura distribuída.

## 11. Avaliação final

Notas qualitativas para o estado de prontidão do backend, não medidas de capacidade do autor. A falta de testes de carga/produção limita as notas de escalabilidade e operação.

| Área | Nota | Justificativa |
|---|---:|---|
| Arquitetura | 7/10 | Simples, proporcional e com responsabilidades reconhecíveis |
| Organização | 7/10 | Intuitiva, com pequenas inconsistências de nomes/localização |
| Qualidade do código | 6/10 | Legível; regras de entrada/normalização/erros divergem |
| Segurança | 4/10 | BCrypt e propriedade adequados; senha vazia e fallback JWT exigem correção |
| Banco de dados | 5/10 | Modelo/constraints úteis; migrations ausentes e exclusão quebrada |
| Testabilidade | 5/10 | Services testáveis, mas contexto estático e ausência de suíte de comportamento |
| Observabilidade | 2/10 | Logs padrão, sem correlação/saúde/operação configuradas |
| Escalabilidade | 4/10 | Base suficiente ao MVP, mas listas ilimitadas e sem medição |
| Manutenibilidade | 6/10 | Classes pequenas; conhecimento implícito e falta de regressão |
| Developer Experience | 3/10 | JAR compila, mas setup/roles/variáveis/deploy não estão guiados |

### Avaliação em entrevista

Como projeto pessoal, demonstra fundamentos de backend em estágio júnior: CRUD relacional, DTOs, autenticação, autorização por proprietário e integração externa. O repositório ainda não demonstra operação madura em produção. A nota do projeto não é uma sentença sobre senioridade do autor; explicar trade-offs, reproduzir falhas e corrigir com testes seria evidência importante de evolução.

Perguntas que eu faria:

1. Por que autenticar uma rota não basta para autorizar edição de um recurso? Como testar A alterando recurso de B?
2. Como duas curtidas do mesmo usuário disputam INSERT mesmo com transação? Qual garantia vem da constraint?
3. O que a cascata de usuário exclui, e o que acontece com curtidas recebidas? Cascata chama service?
4. Como um banco vazio recebe schema e roles sem copiar seu banco pessoal?
5. Qual a diferença entre validar senha recebida, gerar hash e verificar `matches` no login?
6. Como o frontend identifica token expirado e recupera o usuário atual sem listar todos os usuários?
7. Qual query alimenta uma tela com volumes de um mangá e como limita a resposta?
8. Como correlacionar falha HTTP com a causa do erro Cloudinary sem vazar credenciais?
9. Como distinguir rollback de transação, rollback de deploy e recuperação de dados?
10. Que melhoria você adiaria para publicar o MVP com segurança, e qual evidência justificaria retomá-la?

A arquitetura deve ser preservada enquanto se corrigem garantias de comportamento. O caminho para o primeiro deploy é estabilização, não reescrita.
