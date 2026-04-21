# Dev Log - MangaKeeper

## CRUD Usuário

### Problema
Erro 415 ao fazer PATCH

### Causa
Postman estava enviando como Text ao invés de JSON

### Solução
Selecionar Body → raw → JSON

---

### Problema
Campo avatar_url não atualizava

### Causa
Jackson não mapeava snake_case automaticamente

### Solução
Uso de @JsonProperty("avatar_url")

---

## CRUD Coleções

### Problema
"error": "Not Found"
org.springframework.web.servlet.resource.
NoResourceFoundException: No static resource colecoes

### Causa
Ao criar uma requisição no Postman usando POST com a url:
localhost:8080/colecoes/

### Solução
Tirar o / depois de colecoes/
Ficando localhost:8080/colecoes


