# Proteção do branch `main`

Configuração esperada no GitHub (Settings > Branches > Branch protection rules, ou Rulesets). Não é aplicada automaticamente por este repositório: um admin aplica e confere.

## Regras

| Regra | Valor |
|-------|-------|
| Exigir pull request antes do merge | sim |
| Aprovações necessárias | 1 |
| Descartar aprovações quando houver novos commits | sim |
| Exigir revisão de Code Owners | sim (ver `.github/CODEOWNERS`) |
| Exigir status checks antes do merge | `build (Java 17)` e `build (Java 21)` |
| Exigir branch atualizado com `main` | sim |
| Exigir conversas resolvidas | sim |
| Exigir histórico linear | sim (squash ou rebase) |
| Bloquear force push e exclusão | sim |
| Aplicar também a administradores | sim |

Revisão cruzada (CLAUDE.md): A revisa B, B revisa C, C revisa A. O CODEOWNERS garante o dono do módulo; a revisão cruzada é combinada no PR.

## Aplicar via CLI

Requer permissão de admin no repositório. Revise antes de rodar.

```bash
gh api -X PUT repos/novaes7be/LeakTrace/branches/main/protection \
  -H "Accept: application/vnd.github+json" \
  --input - <<'JSON'
{
  "required_status_checks": {
    "strict": true,
    "contexts": ["build (Java 17)", "build (Java 21)"]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "required_approving_review_count": 1,
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": true
  },
  "required_conversation_resolution": true,
  "required_linear_history": true,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "restrictions": null
}
JSON
```

## Conferir

```bash
gh api repos/novaes7be/LeakTrace/branches/main/protection --jq '{reviews: .required_pull_request_reviews.required_approving_review_count, checks: .required_status_checks.contexts}'
```

Os nomes dos checks só existem depois que o workflow `CI` rodar ao menos uma vez.
