# demo-app

- Responsabilidade: app Spring Boot 4 com três vazamentos plantados, alvo dos testes do LeakTrace.
- Dono: C
- API pública: nenhuma (não publicado). Endpoints abaixo.
- Depende de: leaktrace-annotations (módulo de montagem, isento da regra de dependência).

## Vazamentos plantados (dados sintéticos, ver `DemoData`)

| # | Sink | Endpoint | O que vaza |
|---|------|----------|------------|
| 1 | LOG | `POST /api/customers` | CPF no log de cadastro |
| 2 | HTTP_RESPONSE | `GET /api/me` | token interno de serviço no JSON |
| 3 | EXCEPTION | `POST /api/login` (senha errada) | senha digitada na mensagem da exception e no corpo do erro |

`PlantedLeaksTest` prova que os três acontecem sem o agente.

## Rodar

```bash
mvn -pl demo-app -am spring-boot:run
```

Com o agente: a definir quando `leaktrace-agent` gerar o jar (backlog semana 3).
