# leaktrace-agent

- Responsabilidade: premain/agentmain, instrumentação ByteBuddy e jar shaded (ADR-002).
- Dono: A
- API pública: `-javaagent:leaktrace-agent.jar`; nenhuma API Java para usuários.
- Depende de: todos os módulos de implementação (módulo de montagem, isento da regra de dependência).
