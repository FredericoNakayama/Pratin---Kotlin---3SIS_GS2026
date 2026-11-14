# 05 — Plano de implementação

Cada fase é **pequena, verificável e termina com o app compilando**. Faça uma fase por sessão/conversa com a IA quando possível (contexto limpo = menos erro).

**Estrutura de cada fase:** objetivo · ler antes · entregáveis · definição de pronto (DoD) · verificação · commits sugeridos · prompt pronto.

**Verificação padrão** (Windows, na raiz do projeto; use o JDK do Android Studio):

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug testDebugUnitTest
```

Na F9 e na F10, rode também `.\gradlew.bat lintDebug`.

**Fluxo Git de cada fase** (detalhes em [`06-qualidade-git-entrega.md`](06-qualidade-git-entrega.md)): criar a branch `feat/fNN-<tema>` a partir de `main` → commits pequenos → verificação passa → atualizar a tabela de status em [`README.md`](README.md) → merge `--no-ff` em `main`. **Push só com autorização do usuário.**

---

## Prompt-base (cole antes do prompt de qualquer fase)

```text
Você é um(a) desenvolvedor(a) Android sênior trabalhando no projeto Pratin (Kotlin + Jetpack Compose).
A Spec em spec/ é a fonte da verdade. Antes de codar:
1. Leia spec/README.md (regras de ouro e anti-padrões).
2. Leia somente as seções indicadas na fase abaixo.
3. Liste em 3–6 tópicos o que vai fazer e quais arquivos vai criar/alterar.
Durante:
- Implemente apenas o escopo desta fase. Não adiante outras fases.
- Se algo não estiver na Spec e mudar o que o usuário vê ou o que é gravado, pare e me pergunte (com a sua recomendação).
- Siga os padrões de 03-arquitetura.md §6 e os textos de 04-ui.md §5.
Ao terminar:
- Rode a verificação da fase e mostre o resultado real (não presuma sucesso).
- Faça commits pequenos no padrão Conventional Commits de 06-qualidade-git-entrega.md, na branch da fase. Não faça push.
- Atualize a tabela "Estado das fases" em spec/README.md.
- Responda com: o que foi feito, como verificar no emulador (passo a passo curto), pendências.
```

---

## F0 — Setup do projeto e do Git

- **Objetivo:** projeto Android vazio compilando, versionado, com a Spec dentro.
- **Ler:** `03-arquitetura.md` §2 e §3; `06-qualidade-git-entrega.md` §2–§4.
- **Entregáveis:**
  - Projeto criado pelo template "Empty Activity" (Compose) **diretamente em `D:\Pratin - Kotlin - 3SIS_GS2026`** (no assistente do Android Studio, *Save location* = essa pasta; se ele criar uma subpasta, mova o conteúdo para a raiz e apague a subpasta vazia). Sem sobrescrever `spec/`, `CLAUDE.md` e `.gitignore` (mescle o `.gitignore` do template no existente). Nome `Pratin`, pacote `br.com.fiap.pratin`, `minSdk 26`, linguagem de build Kotlin DSL.
  - `gradle/libs.versions.toml` com todas as dependências de `03-arquitetura.md` §2.3 (mesmo que ainda não usadas: Navigation, Lifecycle, Room + KSP + plugin, DataStore, Splash, Coil, serialization, testes).
  - Plugin Room configurado com `schemaDirectory`.
  - `.gitignore` Android completo (ver §2 do doc 06).
  - `CLAUDE.md` na raiz (já existe; manter) e `README.md` provisório (título + equipe).
- **DoD:** `assembleDebug` e `testDebugUnitTest` passam; app abre no emulador com "Hello Android"; `git status` limpo; nenhum arquivo de build versionado.
- **Commits sugeridos:** `chore: cria projeto Android com Jetpack Compose` · `build: adiciona dependências de navegação, Room, DataStore e Coil` · `chore: configura .gitignore do Android`.
- **Prompt:**
  ```text
  Fase F0. Crie o projeto Android do Pratin na raiz do repositório conforme 03-arquitetura.md §2 e §3
  (pacote br.com.fiap.pratin, minSdk 26, Compose, Kotlin DSL, version catalog). Se você não conseguir
  usar o assistente do Android Studio, gere a estrutura equivalente ao template "Empty Activity" à mão,
  usando as versões de AGP/Kotlin/Gradle presentes no cache local (03-arquitetura.md §2.1).
  Adicione todas as dependências permitidas ao libs.versions.toml e ao app/build.gradle.kts, configure
  KSP e o plugin do Room com schemaDirectory. Não crie telas ainda. Verifique com assembleDebug.
  ```

## F1 — Design system

- **Objetivo:** tema, cores, dimensões, logo, ícone do app e strings base.
- **Ler:** `04-ui.md` §1, §5, §6; `01-produto.md` ADR-09.
- **Entregáveis:** `ui/theme/` (Color, Theme sem dynamic color, Type, Shape, Dimens, PratinColors), logo em `res/drawable`, adaptive icon, `Theme.Pratin` e `Theme.Pratin.Starting` em `themes.xml`, `strings.xml` com **todas** as chaves de 04-ui §5 e §6 + plurals, extensões `Categoria.icone()`, `Categoria.rotuloRes()`, `StatusAlimento.cor()` etc. podem ficar para a F2/F4 quando os enums existirem.
- **DoD:** app abre com o tema verde e o ícone do Pratin; splash do sistema verde com o logo.
- **Commits:** `feat(ui): adiciona tema e identidade visual do Pratin` · `feat(ui): adiciona logo e ícone do app` · `feat(i18n): adiciona textos do app em strings.xml`.
- **Prompt:** `Fase F1. Implemente o design system descrito em 04-ui.md §1 e os textos de §5–§6. Copie o logo de https://github.com/FredericoNakayama/Pratin---Flutter---3SIS_GS2026/blob/main/assets/images/logo.png (ou da cópia local que eu indicar). Configure core-splashscreen conforme ADR-09.`

## F2 — Domínio + testes (TDD)

- **Objetivo:** regras de negócio puras e testadas, antes de qualquer tela.
- **Ler:** `02-requisitos.md` §2 (RN02, RN03, RN07, RN13–RN15, RN17) e §3; `03-arquitetura.md` §4.
- **Entregáveis:** `domain/model`, `domain/error`, `domain/rules` (CnpjValidator, CodigoGenerator, AnuncioValidator, PerfilValidator, RegrasStatus), `domain/format/Formatadores`. Testes em `app/src/test/...` escritos **antes** da implementação de cada regra.
- **Casos de teste mínimos:**
  - CNPJ: `11.222.333/0001-81` válido; `11222333000181` válido; `11111111111111` inválido; 13 dígitos inválido; DV errado inválido; `null`/vazio inválido.
  - Código: 6 caracteres, só do alfabeto, sem `O0I1`; `confere(" px7k2q ", "PX7K2Q") == true`.
  - Anúncio (com `agora` fixo): cada regra da RN03 isolada (válido no limite e inválido logo após o limite: validade hoje+7 ok / hoje+8 erro; janela 59 min erro / 60 min ok; fim 23:59:59 da validade ok / +1 s erro; início agora-5min ok / agora-6min erro; quantidade "8,5" ok / "0" erro / "abc" erro; URL "ftp://" erro).
  - Formatadores: "Vence hoje/amanhã/em 3 dias/Vencido", quantidade `15.0 → "15"` e `8.5 → "8,5"`, CNPJ formatado, janela no mesmo dia e em dias diferentes.
  - RegrasStatus: cada transição permitida e proibida da §3 de 02-requisitos.
- **DoD:** `testDebugUnitTest` verde; nenhum import `android.*` em `domain/`.
- **Commits:** um `test(domain): …` + `feat(domain): …` por regra (ex.: `feat(domain): valida CNPJ pelos dígitos verificadores` com `Refs: RN02`).
- **Prompt:** `Fase F2. Usando TDD, implemente o pacote domain conforme 03-arquitetura.md §4 e as regras RN02, RN03, RN07, RN13–RN15, RN17 e as máquinas de estado de 02-requisitos.md. Para cada regra: escreva o teste, veja falhar, implemente, veja passar. Injete o "agora" como parâmetro (sem LocalDateTime.now() no domínio).`

## F3 — Dados (Room, DataStore, repositórios, seed)

- **Objetivo:** persistência completa e regras transacionais funcionando, ainda sem telas.
- **Ler:** `03-arquitetura.md` §5, §7, §8; `02-requisitos.md` RN04–RN12, RN16.
- **Entregáveis:** entidades, `Converters` (formato fixo de data!), relações, DAOs, `PratinDatabase` (versão 1, schema exportado), `PreferenciasDataStore`, mappers, repositórios (interfaces + implementações), `SeedInitializer` + `SeedData`, `AppContainer` + `PratinApplication` registrada no manifest.
- **Testes:**
  - JVM: mappers; `SeedData` gera só CNPJs válidos e janelas dentro da validade.
  - Instrumentado (DEVERIA, banco em memória): `reservar` muda alimento para RESERVADO e cria reserva ATIVA; segunda reserva do mesmo alimento falha com `JA_RESERVADO`; `cancelar` devolve para DISPONIVEL; `confirmarRetirada` com código errado não altera nada; `expirarVencidos` expira alimento e reserva; seed não roda duas vezes.
- **DoD:** build verde; `app/schemas/.../1.json` gerado e versionado; App Inspection (Database Inspector) mostra as tabelas com o seed após abrir o app (chame o seed temporariamente na `MainActivity` ou aguarde a F4).
- **Commits:** `feat(data): cria entidades e DAOs do Room` · `feat(data): adiciona conversores de data e enum` · `feat(data): implementa repositórios com transações de reserva` (`Refs: RN06, RN08, RN10, RN11`) · `feat(data): persiste perfil e onboarding com DataStore` · `feat(data): adiciona dados de exemplo na primeira execução` (`Refs: RN16`) · `test(data): …`.
- **Prompt:** `Fase F3. Implemente a camada data conforme 03-arquitetura.md §5, §7 e §8. Toda operação que altera mais de uma tabela usa database.withTransaction e revalida o estado dentro da transação. Os converters de LocalDateTime devem usar o padrão fixo yyyy-MM-dd'T'HH:mm:ss. Escreva os testes instrumentados de repositório com Room.inMemoryDatabaseBuilder se o emulador estiver disponível.`

## F4 — Navegação, Splash, Onboarding e Shell

- **Objetivo:** esqueleto navegável completo com telas provisórias.
- **Ler:** `03-arquitetura.md` §6.2 e §7; `04-ui.md` §2, T01–T03; RF01–RF03.
- **Entregáveis:** `Rotas.kt`, `PratinNavHost`, `PratinApp` (Scaffold + NavigationBar), `SplashScreen` + `SplashViewModel`, `OnboardingScreen` + `OnboardingViewModel`, placeholders para Início, Atividades, Perfil, Detalhes, Novo anúncio, Confirmação, Sobre (cada um com `TopAppBar` e título).
- **DoD:** todos os CAs de RF01–RF03 verificados no emulador (inclusive "voltar" e reseleção de aba); fechar e abrir com "Não mostrar novamente" marcado vai direto ao Início.
- **Commits:** `feat(nav): adiciona rotas type-safe e NavHost` · `feat(splash): adiciona splash com decisão de destino` · `feat(onboarding): adiciona introdução em 3 páginas` · `feat(nav): adiciona barra de navegação inferior`.

## F5 — Perfil e Sobre

- **Ler:** RF04, RF05, RF21-CA2; `04-ui.md` T06, T10.
- **Entregáveis:** `PerfilViewModel` (form state, validação via `PerfilValidator`, salvar), Perfil (aba) e `EditarPerfilRoute`, preenchimento com perfil de demonstração, "Ver introdução novamente", `SobreScreen`, `PerfilNecessarioDialog` (componente pronto para F6).
- **DoD:** todos os CAs de RF04 e RF05; perfil persiste após fechar o app.
- **Commits:** `feat(perfil): adiciona formulário de perfil com validação de CNPJ` · `feat(perfil): adiciona perfis de demonstração` · `feat(sobre): adiciona tela Sobre o Pratin com ODS`.

## F6 — Início, Detalhes, reserva e Confirmação

- **Ler:** RF06–RF11, RF13, RF14, RF21; `04-ui.md` T04, T05, T08, §4.
- **Entregáveis:** componentes (`AlimentoCard`, `AlimentoImagem`, `StatusChip`, `FavoritoButton`, `InfoRow`, estados, `CategoriaFiltroBar`, `CodigoRetiradaBox`, `ConfirmacaoDialog`), `InicioViewModel` (busca sem acento, filtros, favoritos, pull-to-refresh, ON_RESUME), `DetalhesViewModel` (reservar), `ConfirmacaoViewModel`.
- **DoD:** CAs de RF06–RF11, RF13, RF14, RF21-CA1/CA3. Reservar como ONG gera código e o item some do Início.
- **Commits:** `feat(inicio): lista alimentos disponíveis do Room` · `feat(inicio): adiciona busca e filtro por categoria` · `feat(favoritos): permite favoritar alimentos` · `feat(detalhes): adiciona tela de detalhes` · `feat(reserva): permite à ONG reservar e ver o código`.

## F7 — Novo anúncio

- **Ler:** RF12, RN03; `04-ui.md` T07.
- **Entregáveis:** `NovoAnuncioViewModel` (estado do formulário, validação completa ao publicar, `publicando`, `publicadoId`), `NovoAnuncioScreen`, `DateField`, `DateTimeField`, `PratinTextField`, `PrimaryButton`.
- **DoD:** CAs de RF12; anúncio publicado aparece no Início e persiste após reabrir o app; rotação não perde dados.
- **Commits:** `feat(anuncio): adiciona formulário de novo anúncio` · `feat(anuncio): valida campos obrigatórios e janela de retirada` (`Refs: RN03`) · `feat(anuncio): grava anúncio no Room`.

## F8 — Minhas atividades

- **Ler:** RF15–RF20; `04-ui.md` T09.
- **Entregáveis:** `AtividadesViewModel` (alterna ONG/Estabelecimento conforme o perfil), `ReservaCard`, `AnuncioCard`, `ConfirmarRetiradaDialog`, cancelamentos, confirmação de retirada, expiração em ON_RESUME/refresh.
- **DoD:** CAs de RF15–RF20. Roteiro: com o seed, entrar como Restaurante Sabor Caseiro → confirmar retirada com `PX7K2Q` → status Retirado; entrar como ONG Mesa Cheia → ver reserva Concluída.
- **Commits:** `feat(atividades): lista reservas da ONG` · `feat(atividades): lista anúncios do estabelecimento` · `feat(atividades): permite cancelar reserva e anúncio` · `feat(retirada): confirma retirada pelo código` · `feat(expiracao): expira anúncios e reservas vencidos`.

## F9 — Polimento e revisão

- **Ler:** `02-requisitos.md` §4 (RNF); `04-ui.md` §7; `07-rastreabilidade.md`.
- **Tarefas:** revisar acessibilidade (TalkBack rápido), grade em tablet, rotação em todas as telas, textos sem "chumbar", previews, `lintDebug`, remover código morto, KDoc com IDs, rodar o **roteiro completo** de `06-qualidade-git-entrega.md` §6.
- **DoD:** checklist de `07-rastreabilidade.md` §2 todo marcado (exceto README/prints).
- **Commits:** `fix(...)`, `refactor(...)`, `style(...)`, `test(...)` conforme o caso.

## F10 — README, prints, ZIP e tag

- **Ler:** `06-qualidade-git-entrega.md` §5–§8.
- **Entregáveis:** `README.md` final, prints em `docs/prints/`, ZIP de entrega, tag `v1.0.0`.
- **DoD:** README cobre todos os itens do critério 7; ZIP abre e compila numa pasta limpa; tag criada (push da tag só com autorização).
- **Commits:** `docs: escreve README com problema, ODS, funcionalidades e execução` · `docs: adiciona capturas de tela do fluxo principal`.
