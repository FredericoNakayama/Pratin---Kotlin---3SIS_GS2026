# Pratin (Kotlin) — Spec de desenvolvimento

> **Para quem é este documento:** para a IA (ou pessoa) que vai construir o app Android do Pratin em Kotlin + Jetpack Compose.
> É a **fonte da verdade** do projeto. Se o código e a Spec divergirem, a Spec vence — ou a Spec é atualizada primeiro, com o motivo.

| Item | Valor |
| --- | --- |
| Produto | **Pratin** — "Alimento bom não se joga fora." |
| Disciplina | Android Kotlin Developer — Global Solution 2026, 2º semestre (FIAP) |
| Tema | CitizenTech |
| Origem | Port adaptado do app Flutter entregue em outra disciplina: <https://github.com/FredericoNakayama/Pratin---Flutter---3SIS_GS2026> |
| Repositório | <https://github.com/FredericoNakayama/Pratin---Kotlin---3SIS_GS2026> |
| Equipe | RM555350 — Frederico Nakayama Sant'anna · RM556199 — João Pedro Veloso Pilon |
| Prazo | **19/11/2026, 23h59** (horário de Brasília) |
| Meta | 10,0 / 10,0 na rubrica (ver [`07-rastreabilidade.md`](07-rastreabilidade.md)) |
| Versão da Spec | 1.0 — 14/11/2026 |

---

## 1. Como usar esta Spec (leia antes de tudo)

A Spec é dividida por assunto. **Não leia tudo de uma vez a cada tarefa**: leia este índice, depois só os arquivos e seções que a fase atual pede (cada fase do plano lista as referências).

| Arquivo | Conteúdo | Quando ler |
| --- | --- | --- |
| [`01-produto.md`](01-produto.md) | Problema, público, ODS, perfis, escopo, glossário, decisões (ADRs) | Uma vez, no início. Sempre que surgir dúvida de escopo. |
| [`02-requisitos.md`](02-requisitos.md) | Requisitos funcionais (RF) com critérios de aceite, regras de negócio (RN), máquinas de estado, requisitos não funcionais (RNF) | Em toda fase que implementa comportamento |
| [`03-arquitetura.md`](03-arquitetura.md) | Stack, versões, pacotes, camadas, Room, DataStore, repositórios, navegação, padrões de código, seed | Fases de setup, dados e qualquer tela nova |
| [`04-ui.md`](04-ui.md) | Identidade visual, tema, telas T01–T10, componentes reutilizáveis, textos, acessibilidade | Fases de tela |
| [`05-plano.md`](05-plano.md) | Fases F0–F10, tarefas, definição de pronto, commits e **prompts prontos** | Para saber o que fazer agora |
| [`06-qualidade-git-entrega.md`](06-qualidade-git-entrega.md) | Testes, verificação, fluxo Git, Conventional Commits, README final, prints, ZIP | Ao fechar cada fase e na entrega |
| [`07-rastreabilidade.md`](07-rastreabilidade.md) | Rubrica do professor → requisito → arquivo → evidência | No início (para entender a nota) e no checklist final |
| [`referencias/rubrica-kotlin.md`](referencias/rubrica-kotlin.md) | Transcrição da demanda oficial do professor | Quando precisar do texto original |

### Palavras-chave normativas

Os termos seguem a RFC 2119:

- **DEVE / NÃO DEVE**: obrigatório. Quebrar significa perder nota ou quebrar o app.
- **DEVERIA / NÃO DEVERIA**: recomendado. Só desvie com motivo registrado no commit ou na Spec.
- **PODE**: opcional.

### IDs

Tudo que é verificável tem ID: `RF` (requisito funcional), `RN` (regra de negócio), `RNF` (não funcional), `T` (tela), `ADR` (decisão), `F` (fase), `CA` (critério de aceite, dentro de um RF). Cite os IDs em commits (`Refs: RF12, RN03`) e em comentários KDoc quando a regra não for óbvia.

---

## 2. Contrato de trabalho da IA (regras de ouro)

1. **Uma fase por vez.** Execute somente a fase pedida em [`05-plano.md`](05-plano.md). Não adiante telas, refatorações ou "melhorias" de outras fases.
2. **Não invente requisito.** Se algo não está na Spec e muda o que o usuário vê ou o que é gravado, **pare e pergunte**. Detalhes internos sem impacto (nome de variável privada, ordem de imports) são decisão sua.
3. **Ambiguidade = pergunta curta.** Faça uma pergunta objetiva, com a sua recomendação, em vez de chutar.
4. **Prove antes de declarar pronto.** Uma fase só está pronta quando os comandos de verificação da fase passaram **e você viu a saída**. Nunca diga "deve compilar".
5. **Regras de negócio fora da UI.** Composables não decidem regra; chamam o ViewModel, que chama o repositório/domínio.
6. **Commits pequenos e que compilam.** Siga [`06-qualidade-git-entrega.md`](06-qualidade-git-entrega.md). **Não faça `push`** sem autorização explícita do usuário no chat.
7. **Nada de segredos nem arquivos locais** no Git (`local.properties`, `build/`, `.gradle/`, `.idea/` local, APKs, keystores).
8. **Textos em pt-BR**, em `strings.xml`. Nada de texto de interface "chumbado" em Composable.
9. **Prefira o simples que atende à rubrica.** A correção avalia funcionalidade comprovada, não quantidade de recursos.
10. **Mudou uma decisão? Atualize a Spec** (e o ADR correspondente) no mesmo commit ou no commit anterior.

### Anti-padrões proibidos

- Lógica de negócio, acesso a Room ou DataStore dentro de Composable.
- `GlobalScope`, `runBlocking` na thread principal, `allowMainThreadQueries()`.
- `!!` sem justificativa; capturar `Exception` genérica e engolir o erro em silêncio.
- Passar objetos inteiros como argumento de navegação (passe **IDs**).
- Criar ViewModel manualmente (`MeuViewModel()`) dentro de Composable em vez de usar a factory.
- Cores, tamanhos e textos soltos nas telas (use tema, `Dimens` e `strings.xml`).
- Dependências fora da lista em [`03-arquitetura.md`](03-arquitetura.md) §2 sem aprovação.
- Navigation 3 (`androidx.navigation3`), Hilt, Retrofit, Firebase: fora do escopo (ver ADRs).

---

## 3. Estado das fases

Atualize esta tabela ao concluir cada fase (no mesmo commit que fecha a fase).

| Fase | Descrição | Status |
| --- | --- | --- |
| F0 | Setup do projeto e do Git | ✅ concluída |
| F1 | Design system (tema, cores, logo, strings) | ✅ concluída |
| F2 | Domínio puro + testes unitários | ✅ concluída |
| F3 | Dados: Room, DataStore, repositórios, seed | ✅ concluída |
| F4 | Navegação, Splash, Onboarding, Shell | ✅ concluída |
| F5 | Perfil e Sobre | ✅ concluída |
| F6 | Início, Detalhes, reserva e Confirmação | ✅ concluída |
| F7 | Novo anúncio | ✅ concluída |
| F8 | Minhas atividades, cancelamentos, retirada, expiração | ✅ concluída |
| F9 | Polimento, acessibilidade e revisão | ✅ concluída |
| F10 | README, prints, ZIP e tag de entrega | ⬜ pendente |

Legenda: ⬜ pendente · 🟨 em andamento · ✅ concluída
