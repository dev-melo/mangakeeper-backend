# Análise técnica do MangaKeeper

Data: 10/09/2026. Código de referência: `19cade8` (`main`).

## Objetivo e ordem de leitura

Avaliar a maturidade do backend para o primeiro deploy e a construção de um frontend, considerando um projeto pessoal. Os documentos registram uma revisão do código existente; exemplos de correção são propostas e não foram aplicados à aplicação.

1. [Análise dos 15 temas](01-analise-por-tema.md): arquitetura até manutenibilidade, com evidências e alternativas.
2. [Relatório e prioridades](02-relatorio.md): as 11 seções solicitadas, problemas identificados por código, notas e roadmap.
3. [Validação e testes](03-validacao-e-testes.md): verificações executadas, limites da análise, dez testes prioritários e critérios para deploy.

Os IDs `P01` a `P16` conectam os temas aos problemas detalhados no relatório. Não representam 16 bloqueios equivalentes: a severidade e o momento de correção variam.

## Contexto confirmado

| Campo | Situação |
|---|---|
| Nome | MangaKeeper |
| Objetivo | Rede social para compartilhar bibliotecas e coleções de mangás |
| Tipo | Projeto pessoal, ainda sem frontend e em preparação para o primeiro deploy |
| Linguagem | Java, alvo de compilação 17 |
| Framework | Spring Boot 3.5.13; Spring MVC; Spring Security |
| Persistência | Spring Data JPA/Hibernate; driver PostgreSQL |
| Autenticação | Login/senha, BCrypt, JWT HMAC256 via Auth0 Java JWT |
| Imagens | Cloudinary, usando `cloudinary-http5` 2.0.0 |
| API | REST com DTOs e documentação springdoc/OpenAPI |
| Build | Maven Wrapper; JAR executável Spring Boot |
| Arquitetura | Uma aplicação em camadas: controllers, services, repositories e entidades |
| Deploy | Nenhuma configuração de plataforma encontrada no repositório |
| Escala esperada | Não informada; não há base para estimar requisições/segundo |
| Usuários esperados | Não informado; priorização considera um MVP com primeiros usuários |
| Limitações conscientes | Autor informou ausência de frontend, migrations e procedimento de rollback; demais escolhas de produto não devem ser presumidas |

O PostgreSQL usado em uma verificação descartável não comprova a versão ou configuração do banco de desenvolvimento do autor.

## Como interpretar a análise

- **Fato de código:** observado nos arquivos do commit de referência.
- **Confirmado em execução:** reproduzido na verificação descrita no documento de validação; vale para aquele ambiente.
- **Risco/hipótese:** mecanismo plausível identificado, sem reprodução suficiente para afirmar frequência ou impacto medido.
- **Sugestão:** alternativa de melhoria; não é uma descrição do comportamento atual.
- **Não avaliado:** faltam ambiente, medição ou informação explicitamente identificados.

| Classificação | Interpretação |
|---|---|
| 🔴 Crítico | Pode comprometer contas/dados; bloquear exposição na condição descrita |
| 🟠 Importante | Afeta integridade, fluxos essenciais ou operação; resolver antes do uso correspondente |
| 🟡 Melhoria recomendada | Benefício real, com prioridade proporcional ao custo |
| 🔵 Evolução futura | Só justificada por crescimento ou regra de produto adicional |
| 🟢 Decisão adequada | Preservar; não refatorar apenas por preferência |

## Escopo e rastreabilidade

Foram lidos os arquivos de `src/main/java`, `application.properties`, o teste existente, `pom.xml`, README, devlog, `.gitignore` e configuração do wrapper. A análise foi organizada em configuração; segurança/cadastro; fluxos de coleção/mangá/volume/curtida; integração externa; erros/testes; síntese operacional.

Links para o código são relativos ao repositório e apontam ao arquivo; o nome do método identifica o trecho relevante. Eles continuam portáveis após clonar o projeto. O commit acima fixa a versão analisada.

Não houve auditoria do histórico Git inteiro, teste de invasão externo, teste de carga representativo, avaliação de frontend inexistente ou certificação de segurança. As verificações pontuais não substituem uma suíte automatizada mantida pelo projeto.

## Informações que ainda mudariam decisões

- Plataforma de deploy, orçamento, domínio do frontend e política de TLS/proxy.
- Versão do PostgreSQL de destino, DDL efetivo e rotina de backup/restauração.
- Se coleções privadas farão parte do MVP.
- Se volumes repetidos representam cópias físicas válidas.
- Se mangás são cadastros particulares ou haverá catálogo compartilhado.
- Tamanho de imagens aceito, quota de uploads e expectativa de tráfego.
- Política de recuperação de conta e invalidação de tokens após troca de senha.

Essas lacunas não impedem corrigir os defeitos já identificados.
