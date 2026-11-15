# LeakTrace

Java agent de taint tracking em runtime. Detecta dados sensiveis (CPF, cartao, token, senha) chegando a logs, respostas HTTP e exceptions, e reporta no terminal, em JSON e em SARIF.

> Status: em desenvolvimento (0.1.0-SNAPSHOT). Ainda nao funcional.

## Build

Requer Java 17+ e Maven 3.9+.

```bash
mvn clean verify
```

## Modulos

Ver [CLAUDE.md](CLAUDE.md) para o mapa de modulos, donos e regras de dependencia.

## Licenca

[Apache License 2.0](LICENSE)
