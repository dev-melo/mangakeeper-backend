# Validação executada e plano de testes

[Contexto](README.md) · [15 temas](01-analise-por-tema.md) · [Relatório](02-relatorio.md)

## 1. Ambiente e limites

Data: 10/09/2026. Commit: `19cade8`. Runtime local: Temurin Java 21.0.2; compilação configurada para Java 17. PostgreSQL descartável: **18.6**, imagem `postgres:18`, digest `sha256:4ef4dbc939d61acea57712655ddb4b4ab27419c913f94cca0cd57cb3ea3c2280`.

A aplicação foi iniciada em `127.0.0.1:18089`, com JVM em UTC, contra um banco exclusivo `mangakeeper_audit` em `127.0.0.1:55439`. O schema foi criado pelo próprio Hibernate (`ddl-auto=update`), a partir de banco vazio. O container não montou diretórios do projeto. Contas, senha de banco, chave JWT e nomes de Cloudinary eram fictícios e exclusivos da verificação.

Não foram usados banco de desenvolvimento, credenciais reais ou upload externo. A conta Cloudinary fictícia só permitiu inicializar a configuração; ela não comprova integração real. Os testes não alteraram fontes da aplicação. A reprodução foi feita por um script temporário Python com HTTP e consultas `psql`, não por novos testes JUnit versionados.

## 2. Verificações executadas

### Build

```bash
bash mvnw -o -B -ntp -DskipTests package
```

Resultado: **BUILD SUCCESS**, JAR executável gerado. O build isoladamente pulou testes. `./mvnw` havia falhado por ausência de bit executável; o modo Git observado foi `100644`. Essa falha não era de compilação.

### Teste existente

Foi executado `bash mvnw -B -ntp test`, passando propriedades `-D` para URL/usuário/senha do banco descartável, secret JWT fictício, configuração Cloudinary fictícia e `show-sql=false`.

Resultado:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

É o teste `contextLoads()` existente. Ele passou com configuração de infraestrutura fornecida. Não prova que o projeto inicializa sem configuração nem que cadastro/login/CRUD estão corretos. O banco já havia recebido dados da reprodução HTTP; o teste não validou migrations, pois não existem.

Houve warnings de instrumentação dinâmica Mockito/Byte Buddy no Java 21; não causaram falha. Isso é manutenção futura da configuração de testes, não bloqueio funcional demonstrado.

### HTTP, persistência e concorrência

As respostas abaixo são resultados observados no ambiente descrito, não respostas desejadas. Os tokens foram ocultados e nenhuma senha real foi utilizada.

| Caso | Resultado observado | Conclusão |
|---|---|---|
| Cadastro em banco sem roles | `403`, corpo vazio; log `Role não Encontrada` | Falta seed; erro interno mascarado |
| Cadastro após inserir roles | `201` | Caminho feliz depende dos dados iniciais |
| Cadastro com senha vazia | `201` | Validação de senha original ausente |
| Login da conta com senha vazia | `200`, token emitido | Falha não se limita à gravação do cadastro |
| JWT com JVM UTC | `exp - iat = 18000` segundos | Duração de cinco horas em vez de duas |
| GET público com token inválido | `403`, corpo vazio; log UserNotFoundException | Filtro não retorna contrato de autenticação adequado |
| POST protegido sem token | `403`, corpo vazio | Falta distinguir `401` de `403` |
| Login com senha incorreta | `403`, corpo vazio | Contrato de erro de autenticação incompleto |
| Cadastro sem campo login | `403`, corpo vazio; NPE no log | Falta validar antes de `toLowerCase` |
| Cadastro `MixedAudit` | `201`, username retornado `mixedaudit` | Normalização na gravação |
| Login como `MixedAudit` | `403` | Login não aplica a mesma normalização |
| Cadastro com email já usado | `403`, corpo vazio; violação unique no log | Conflito não mapeado corretamente |
| Criar coleção autenticado | `201`, dono igual à conta atual | Derivação correta do proprietário |
| B altera coleção de A | `403` com JSON `Sem permissão` | Proteção de propriedade funcionou neste caminho |
| B cria mangá na coleção de A | `403` com JSON `Sem permissão` | Proteção do pai funcionou neste caminho |
| POST de mangá com espaços no título/autor | `201`, resposta aparada | Normalização aplicada na resposta |
| GET do mesmo mangá | `200`, espaços originais presentes | Persistência e resposta de criação divergem |
| Criação de volume válido | `201` | Caminho feliz exercitado |
| PATCH de volume com `numero=-2` | `200` | Constraint do DTO não foi aplicada |
| GET do volume após PATCH negativo | `200`, `numero=-2` | Valor inválido persistiu |
| PATCH de senha de um caractere | `200` | Restrição de tamanho do DTO não aplicada |
| Criar coleção com token anterior à troca da senha | `201` | Sem revogação imediata por troca de senha |
| B curte coleção de A | `204` | Caminho feliz exercitado |
| A exclui conta cuja coleção B curtiu | `403` vazio; FK no log | Defeito de remoção reproduzido |
| Contagem SQL após exclusão falha | Conta=1, coleção=1, curtida=1 | Rollback preservou os registros consultados |
| Remover curtida de B | `204` | Preparou estado sem curtida para disputa |
| Oito POSTs simultâneos da mesma curtida | 1 × `204`, 4 × `409`, 3 × `403`; COUNT=1 | Constraint preservou integridade, contrato divergiu |
| OPTIONS com origem `http://localhost:4200` | `200`, Allow-Origin correspondente | Preflight permitido funcionou |
| OPTIONS com origem não permitida | `403`, `Invalid CORS request` | Origem rejeitada neste cenário |
| GET `/usuarios` com usuário comum | `403` | Restrição administrativa exercitada |
| POST `/upload` sem autenticação | `403` | Upload não está aberto a anônimos |

O teste de concorrência usou oito threads sincronizadas por barreira, disparando HTTP para o mesmo par usuário/coleção. Algumas requisições viram a curtida depois de criada e retornaram `409`; outras disputaram INSERT e registraram violação unique. Uma execução prova possibilidade, não frequência. O resultado não é benchmark e pode ter outra distribuição na repetição.

### Evidências de log selecionadas

Banco sem seed:

```text
java.lang.RuntimeException: Role não Encontrada
```

Cadastro sem login:

```text
java.lang.NullPointerException: ... RegisterDTO.login() ... is null
```

Exclusão de conta com curtida recebida:

```text
ERROR: update or delete on table "colecoes" violates foreign key constraint ... on table "curtidas"
Detail: Key (id)=(1) is still referenced from table "curtidas".
```

Inserção concorrente:

```text
ERROR: duplicate key value violates unique constraint ...
Detail: Key (usuario_id, colecao_id)=(3, 1) already exists.
```

Esses detalhes foram observados nos logs locais do servidor, não no corpo devolvido ao cliente. O `403` externo não significou falta de propriedade no caso de exclusão. A combinação de erro não tratado e dispatch de erro protegido é uma explicação coerente com a configuração, mas não houve tracing da cadeia para atribuir cada resposta a um filtro específico.

### Índices efetivamente encontrados no banco descartável

| Tabela | Índices observados |
|---|---|
| colecoes | PK `(id)` |
| curtidas | PK `(id)`, unique `(usuario_id, colecao_id)` |
| mangas | PK `(id)`, unique `(colecao_id, titulo, idioma)` |
| roles | PK `(id)`, unique `(role_name)` |
| usuarios | PK `(id)`, unique `(username)`, unique `(email)` |
| volumes | PK `(id)` |

A consulta foi `pg_indexes` no schema public. A tabela de associação de roles não apareceu com índice nessa consulta. Não extrapolar essa lista ao banco pessoal do autor: ele pode conter índices criados manualmente. Para decidir tuning, ainda faltam EXPLAIN e volume representativo.

## 3. Como reproduzir os cenários essenciais

Use exclusivamente banco vazio descartável e credenciais fictícias. A aplicação atual cria/altera schema automaticamente; não apontar essa sequência a produção.

### Inicialização usada

```bash
docker run --detach --rm --name mangakeeper-audit-19cade8 \
  --publish 127.0.0.1:55439:5432 \
  --env POSTGRES_USER=audit \
  --env POSTGRES_PASSWORD=audit-local-disposable \
  --env POSTGRES_DB=mangakeeper_audit postgres:18
```

Em outro terminal, após o banco ficar pronto:

```bash
java -Duser.timezone=UTC \
  -jar target/mangakeeper-backend-0.0.1-SNAPSHOT.jar \
  --server.address=127.0.0.1 --server.port=18089 \
  --spring.datasource.url=jdbc:postgresql://127.0.0.1:55439/mangakeeper_audit \
  --spring.datasource.username=audit \
  --spring.datasource.password=audit-local-disposable \
  --api.security.token.secret=audit-local-only-key-not-for-deployment \
  --cloudinary.cloud-name=audit-unused \
  --cloudinary.api-key=audit-unused \
  --cloudinary.api-secret=audit-unused \
  --spring.jpa.show-sql=false
```

Todos os valores acima são exemplos descartáveis, inadequados para deploy. Para repetir exatamente a imagem analisada, fixar o digest informado no início em vez da tag móvel.

1. Tentar POST `/auth/register` válido antes de inserir roles; observar falha e log.
2. Inserir no banco descartável: `INSERT INTO roles(role_name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');`.
3. Cadastrar/login com senha vazia; observar `201`/`200`. Criar outras duas contas válidas A/B.
4. Login de A; decodificar localmente apenas o payload para comparar `exp` e `iat`. Decodificar não valida assinatura e serve aqui somente para inspecionar duração.
5. A cria coleção, mangá com espaços no título e volume válido. Usar os IDs retornados, não assumir IDs fixos.
6. B tenta alterar a coleção de A e criar mangá nela; observar recusa.
7. Comparar POST/GET do mangá; enviar PATCH `{"numero":-2}` ao volume de A; confirmar no GET.
8. B curte coleção de A; A tenta DELETE de sua conta; consultar conta, coleção e curtida após a resposta para comprovar rollback.
9. Remover a curtida de B; emitir duas ou mais requisições simultâneas para o mesmo par e conferir COUNT no banco. Requisições sequenciais não provam a disputa.
10. Encerrar a aplicação e executar `docker stop mangakeeper-audit-19cade8`; `--rm` remove o container. Não remover outros containers/imagens do ambiente.

## 4. O que ainda não foi comprovado

| Questão | O que falta |
|---|---|
| N+1 e custo de queries | Dataset representativo, contagem de SQL por requisição e EXPLAIN ANALYZE em ambiente isolado |
| Lost updates/deadlocks | Testes controlados com duas transações, barreiras e inspeção de SQL/locks |
| Todos os caminhos de autorização | Matriz completa de PATCH/DELETE/criação para usuário, coleção, mangá, volume e admin |
| JWT expirado, conta removida, esquema Bearer inválido | Casos de integração específicos; nesta rodada foi testado token malformado e duração |
| Cloudinary real | Conta de teste autorizada, uploads reais limitados, timeouts/falhas simuladas e limpeza dos ativos |
| Validação de conteúdo/tamanho do upload | Casos multipart com arquivos e limites intencionais; não houve upload real nesta rodada |
| Segurança de todas as dependências | SCA com árvore resolvida e triagem de aplicabilidade; consultas pontuais não substituem isso |
| Configuração de produção | Plataforma, TLS, proxy, origem do frontend, pool e limites efetivos |
| Recuperação | Backup e restauração ensaiados em banco separado, com verificação dos dados |
| Java 17 em execução | Build/teste em runtime 17 se essa for a versão de deploy |
| Escala 10x/100x/1000x | Carga inicial, hardware, perfil de uso e teste de carga |
| Frontend | Ainda não existe; não há teste E2E de navegador nem decisão final de armazenamento do token |

## 5. Dez testes de maior valor a adicionar ao projeto

Cada item é uma família pequena de casos. Não foi implementada como parte da documentação. Critérios abaixo descrevem o comportamento desejado após correções, não o comportamento atual.

### T01 — Inicialização reproduzível e primeiro cadastro

**Tipo:** integração PostgreSQL, schema vazio. **Preparação:** subir banco novo e executar migrations. **Ação:** iniciar app e cadastrar usuário. **Esperado:** `201`, ROLE_USER atribuída, nenhum SQL manual necessário; senha não aparece na resposta. **Valor:** impede deploy que “inicia” mas não permite cadastrar. **Relaciona:** P05/P12.

### T02 — Cadastro e armazenamento de senha

**Tipo:** HTTP + PostgreSQL. **Casos:** login/senha ausentes, vazios, comprimento fora da política, email inválido e cadastro válido. **Esperado:** inválidos `400`, sem usuário persistido; válido armazena hash diferente do texto, `matches` confirma a senha e não há hash na resposta. Testar normalização e email/username duplicados com `409`. **Valor:** protege a porta pública de entrada. **Relaciona:** P02/P07/P09.

### T03 — Login e JWT

**Tipo:** unitário de tempo com Clock + integração de filtro. **Casos:** login válido, senha errada, token expirado/adulterado, usuário removido, header malformado; timezone UTC e -03:00. **Esperado:** duração configurada constante, inválidos `401` com formato uniforme; assinatura/issuer obrigatórios. **Valor:** evita logout/erro confuso e janela de token incorreta. **Relaciona:** P03/P04.

### T04 — Matriz de autorização

**Tipo:** MockMvc com cadeia de segurança real + banco. **Preparação:** A, B e admin, com grafo de cada dono. **Ação:** B tenta editar/deletar cada recurso de A e criar filhos em pais de A. **Esperado:** `403`, banco intacto; dono e admin conforme política permitidos; anônimo `401`; GET de lista administrativa negado a usuário comum. **Valor:** detecta IDOR por regressão. **Relaciona:** proteção existente/P12.

### T05 — PATCH e campos omitidos

**Tipo:** HTTP + banco. **Casos:** número negativo, código de barras inválido, campos obrigatórios em branco, senha curta, email inválido; PATCH válido com apenas um campo. **Esperado:** inválidos `400` sem persistir; PATCH parcial preserva campos omitidos. Documentar o significado de null. **Valor:** impede estados inválidos que compilação não detecta. **Relaciona:** P02/P09.

### T06 — Curtidas simultâneas

**Tipo:** integração PostgreSQL com transações independentes. **Preparação:** mesmo usuário/coleção, sem curtida; barreira para concorrência. **Esperado:** uma linha final, resposta de sucesso e conflito/idempotência conforme contrato, nunca erro interno mascarado. Também testar remoção repetida. Não envolver todo teste em uma única transação que esconda commits. **Valor:** valida a constraint e a tradução do erro. **Relaciona:** P07.

### T07 — Exclusão de conta com interações

**Tipo:** integração PostgreSQL. **Preparação:** A possui coleção/mangá/volume; B curte coleção de A; A também curte outra coleção. **Ação:** excluir A. **Esperado:** conta e dependentes pertinentes removidos, curtidas feitas/recebidas eliminadas, B e seus recursos preservados; falha intermediária deliberada causa rollback. **Valor:** protege integridade e evita defeito social difícil de ver sozinho. **Relaciona:** P06.

### T08 — Filtros, paginação e contratos

**Tipo:** HTTP + banco. **Preparação:** dados de vários usuários, coleções e mangás. **Esperado:** filtro por pai retorna somente os filhos corretos; size máximo respeitado; ordem estável com desempate; POST/GET consistentes; Location resolvível; JSON/OpenAPI coerentes. **Valor:** viabiliza frontend sem carregar base inteira. **Relaciona:** P08/P09/P11.

### T09 — Upload e falha externa

**Tipo:** MVC + service com fronteira Cloudinary simulada. **Casos:** anônimo, vazio, formato/conteúdo não aceito, excesso de tamanho, sucesso, timeout e falha do provedor. **Esperado:** `401/400/413` conforme caso, JSON de sucesso estável, erro externo previsível e causa registrada; ativo associado ao dono e quota aplicada. Usar conta real só em teste específico com limpeza. **Valor:** protege custo e confiabilidade sem depender da internet em toda a suíte. **Relaciona:** P10.

### T10 — Mudança de credencial e contrato de sessão

**Tipo:** integração. **Preparação:** token válido, troca de senha autorizada. **Esperado:** senha nova funciona e antiga não; token anterior segue a política decidida. Se revogação for implementada, deve retornar `401`; `/usuarios/me` deve exigir autenticação e retornar só DTO permitido. **Valor:** evita promessa falsa de encerramento de acesso e facilita frontend. **Relaciona:** P15/P09.

Fixtures simples podem construir dois usuários e um grafo; não criar factories genéricas antes de haver repetição real. PostgreSQL em container (direto, CI ou Testcontainers) é adequado. Mocks são úteis para Cloudinary/Clock, mas não para provar constraints e cascatas.

## 6. Critérios de aceite para primeiro deploy

- [ ] Ausência de secret JWT impede inicialização; secret de produção não é o exemplo dos documentos.
- [ ] Cadastro e PATCH rejeitam os estados inválidos reproduzidos.
- [ ] Token tem duração correta em UTC; autenticação/autorizações usam `401`/`403` coerentes.
- [ ] Banco novo recebe schema/roles por mecanismo versionado.
- [ ] Exclusão de conta funciona com curtidas recebidas sem apagar recursos de terceiros.
- [ ] Curtidas/cadastro concorrentes têm integridade e erro previsível.
- [ ] Contratos JSON, Location e filtros estão definidos antes de integrar as telas.
- [ ] Upload e conta pública têm limites e falhas externas previsíveis.
- [ ] CI executa testes com banco isolado; dependências são verificadas.
- [ ] Domínio HTTPS, origem CORS, porta, banco e secrets estão configurados na plataforma.
- [ ] Saúde mínima, logs úteis e procedimento de restauração foram testados.
- [ ] Runtime Java de destino foi verificado.

Os itens estão desmarcados porque esta entrega analisa e documenta; não implementa as correções. Passar o teste de contexto não satisfaz esses critérios.

## 7. Encerramento da verificação

O container `mangakeeper-audit-19cade8` foi parado e removido automaticamente. O processo Java foi encerrado por SIGTERM; os logs registraram `Graceful shutdown complete` e encerramento do HikariPool. Isso confirma encerramento gracioso nesta execução local, sem requisições longas em andamento; não comprova a janela de término da futura plataforma.

A imagem baixada permaneceu no cache Docker. Não foram removidas imagens ou containers alheios. Os resultados relevantes estão transcritos neste documento, sem depender dos logs temporários para leitura do relatório.

Os links relativos dos quatro documentos e a presença das 15 seções temáticas, 11 seções de relatório e campos obrigatórios dos problemas foram verificados. Alterações desta entrega se limitam a `docs/analise-tecnica/`.
