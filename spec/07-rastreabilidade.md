# 07 — Rastreabilidade: rubrica → Spec → código → evidência

## 1. Matriz

| Critério (pts) | Exigência | Requisitos | Onde no código | Evidência (print / verificação) |
| --- | --- | --- | --- | --- |
| **1. Contextualização e tela inicial (1,0)** | Nome, objetivo, público | RF01, RF02, RF05 | `ui/screens/splash`, `onboarding`, `sobre` | Prints 01–03, 05 |
| | Problema real | RF05; `01-produto.md` §1–2 | `sobre`, README | Print 05 + README |
| | ≥ 1 ODS justificado no README | `01-produto.md` §2 | README | Seção ODS do README |
| | Tela inicial com identidade e acesso às funções | RF03, RF06; `04-ui.md` §1 | `ui/theme`, `inicio`, `PratinApp` | Print 04 |
| **2. Navegação (1,5)** | Navigation Compose | ADR-05; `03-arquitetura.md` §6.2 | `ui/navigation` | Código + prints |
| | ≥ 3 telas distintas | T04–T10 | `ui/screens/*` | Prints 04, 09, 11, 13 |
| | Ir e voltar sem travar | RF03-CA1..3, RF13-CA2, RF14-CA3 | `PratinNavHost` | Roteiro §6 do doc 06 |
| **3. Interface e UX (1,5)** | Componentes M3 | `04-ui.md` §8 | `ui/components`, telas | Prints |
| | Conteúdo organizado e legível | `04-ui.md` §1, §3 | tema + telas | Prints |
| | Mensagens de ação e estados vazios | RF04-CA4, RF06-CA3, RF07-CA2, RF08-CA3, RF12-CA3, RF15-CA3, RF16–RF19 | Snackbars, `EmptyState` | Prints 07, 10, 15, 18 |
| **4. Dados e interação (2,0)** | Entidade com ≥ 3 atributos | `03-arquitetura.md` §5.1 | `AlimentoEntity` (16 colunas), `ReservaEntity` | Código + print 19 |
| | Cadastrar e visualizar | RF12, RF06, RF11 | `novoanuncio`, `inicio`, `detalhes` | Prints 09, 10 |
| | Ação adicional | RF09 (favoritar), RF13/RF16/RF18/RF19 (alterar status) | repositórios + `atividades` | Prints 12, 15, 16 |
| | Validação de campo obrigatório | RF04, RF12; RN03, RN17 | `domain/rules`, ViewModels | Prints 06, 08 + testes |
| | UI reflete as alterações | RF06-CA4, RF16-CA2, RF18-CA2 | `Flow` do Room → `StateFlow` | Prints 10, 15 |
| **5. Room (1,5)** | Room guarda criados/modificados | ADR-01; `03-arquitetura.md` §5 | `data/local/db` | Print 19 |
| | Persiste após reabrir | RF09-CA2, RF12-CA3 | — | **Print 17** (antes/depois do `force-stop`) |
| **6. Código e estado (1,5)** | Separação UI / dados / lógica | `03-arquitetura.md` §1, §3 | pacotes `ui`, `data`, `domain` | README (arquitetura) |
| | ViewModel com estado | ADR-03; `03-arquitetura.md` §6.1 | `*ViewModel` + `*UiState` | Código |
| | UI atualiza quando dados mudam | ADR-03 | `collectAsStateWithLifecycle` | Prints 10, 15 |
| | Compila e executa | RNF01, RNF02 | — | `assembleDebug` + ZIP validado |
| **7. Documentação (1,0)** | README completo | `06-qualidade-git-entrega.md` §5 | `README.md` | — |
| | Prints reais + fluxo principal | §6 do doc 06 | `docs/prints` | — |
| | Evidência: criação, ação adicional, persistência | §6 do doc 06 | — | Prints 10, 15, 17 |
| **Escopo** | Kotlin, Compose, Navigation Compose, ViewModel, Room | RNF03 | `libs.versions.toml` | — |
| **Entrega** | ZIP sem build, README com nomes e RMs | §7–§8 do doc 06 | — | ZIP validado |

## 2. Checklist de nota máxima (marque na F9/F10)

- [ ] Nome, objetivo, público e problema aparecem **no app** (Onboarding + Sobre) e no README.
- [ ] ODS 2 e 12 justificados no README.
- [ ] Navigation Compose com ≥ 3 telas de finalidade distinta; "voltar" coerente em todas.
- [ ] Componentes M3 citados na rubrica presentes (Column, Row, Card, LazyColumn, Scaffold, TextField, Button).
- [ ] Snackbar após cada ação e estado vazio em cada lista.
- [ ] Entidade `Alimento` com ≥ 3 atributos; cadastro e listagem funcionando.
- [ ] Ações adicionais: favoritar e alterar status (reservar, cancelar, confirmar retirada).
- [ ] Validação impede gravar anúncio/perfil inválido (testado).
- [ ] Tudo gravado em Room/DataStore; continua após `force-stop`.
- [ ] Pacotes `ui` / `data` / `domain`; ViewModel + StateFlow em todas as telas principais.
- [ ] `assembleDebug`, `testDebugUnitTest`, `lintDebug` sem erro; ZIP validado em pasta limpa.
- [ ] README com problema, público, proposta, ODS, funcionalidades, tecnologias, execução, prints, fluxo, evidências, nomes e RMs.
- [ ] Nenhum texto sugere integração real com serviços externos.
