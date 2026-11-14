<p align="center">
  <img src="app/src/main/res/drawable-nodpi/logo_pratin.png" alt="Logo do Pratin" width="120">
</p>

<h1 align="center">Pratin</h1>

<p align="center"><em>Alimento bom não se joga fora.</em></p>

<p align="center">
  App Android em <strong>Kotlin + Jetpack Compose</strong> que conecta padarias, mercados e restaurantes
  a ONGs para doar alimentos próximos do vencimento.<br>
  Global Solution FIAP 2026 (2º semestre) — Android Kotlin Developer — desafio <strong>CitizenTech</strong>.
</p>

---

## Integrantes

| RM | Nome completo |
| --- | --- |
| RM555350 | Frederico Nakayama Sant'anna |
| RM556199 | João Pedro Veloso Pilon |

**Repositório:** https://github.com/FredericoNakayama/Pratin---Kotlin---3SIS_GS2026

---

## Problema

Padarias, mercados e restaurantes jogam fora alimentos **ainda próprios para consumo** porque estão perto do vencimento. No mesmo bairro, ONGs que atendem famílias em insegurança alimentar têm dificuldade para conseguir doações. O comércio e a ONG estão próximos, mas não se conectam: falta um canal simples, rápido e organizado para doar.

## Público beneficiado

- **Pessoas em insegurança alimentar** atendidas por ONGs (beneficiário final).
- **Voluntários de ONGs**, que hoje correm atrás de doações por telefone e mensagem.
- **Pequenos empreendedores** (padarias, mercados, restaurantes), que querem doar o excedente em vez de descartá-lo.

## Proposta

1. O **estabelecimento** anuncia um lote de alimento próprio para consumo, com validade de até 7 dias e uma janela de retirada.
2. A **ONG** encontra o lote, reserva e recebe um **código de retirada** de 6 caracteres.
3. Na retirada, o estabelecimento digita o código e a doação é concluída.

A doação é **sempre gratuita**. Não há login: o usuário escolhe um perfil (Estabelecimento ou ONG) e o CNPJ identifica o dono dos anúncios e das reservas.

**Alinhamento com o CitizenTech:** o app parte de uma necessidade real do território (comércio e ONG do mesmo bairro), gera uma melhoria concreta (doar leva poucos toques e a retirada é organizada com código e horário) e permite medir o impacto pelos registros (lotes retirados, quantidade redistribuída e tempo entre anúncio e reserva).

## ODS relacionados

| ODS | Justificativa |
| --- | --- |
| **ODS 2 — Fome zero e agricultura sustentável** (meta 2.1: acesso a alimentos seguros e nutritivos) | O Pratin leva alimento seguro, que seria descartado, até famílias em insegurança alimentar por meio das ONGs. |
| **ODS 12 — Consumo e produção responsáveis** (meta 12.3: reduzir o desperdício de alimentos no varejo) | O app dá ao comércio um destino útil para o excedente, reduzindo o desperdício no varejo. |

---

## Funcionalidades

| Perfil | O que faz |
| --- | --- |
| **Todos** | Introdução (onboarding), lista de alimentos disponíveis, busca sem acento, filtro por categoria, favoritos, detalhes, tela "Sobre o Pratin" |
| **Estabelecimento** | Publica anúncios (com validação), acompanha e cancela os próprios anúncios, confirma a retirada pelo código |
| **ONG** | Reserva alimentos, recebe o código de retirada, acompanha e cancela as próprias reservas |
| **Sem perfil** | Consulta a lista; ao tentar anunciar ou reservar, o app pede para configurar o perfil |

- **Validações:** CNPJ pelos dígitos verificadores; anúncio com nome de 2 a 60 caracteres, quantidade maior que zero, validade de hoje até 7 dias, janela de retirada de pelo menos 1 hora terminando até o dia da validade, declaração de "próprio para consumo".
- **Ações sobre os registros:** reservar, cancelar reserva, cancelar anúncio e confirmar retirada (mudanças de status), além de **favoritar**.
- **Expiração automática:** anúncios e reservas com a janela encerrada passam a "Expirado" ao abrir ou voltar para as listas.
- **Persistência local:** tudo fica no aparelho (**Room** para anúncios, reservas e favoritos; **DataStore** para perfil e introdução) e continua disponível depois de fechar e abrir o app.
- **Dados de exemplo:** na primeira abertura, o app grava 15 anúncios com datas relativas ao dia atual, incluindo uma reserva ativa (código **`PX7K2Q`**) e um item já retirado.

**Ciclo de vida de um alimento**

```
DISPONÍVEL ──ONG reserva──► RESERVADO ──código confirmado──► RETIRADO
    │  ▲                       │
    │  └──ONG cancela reserva──┘
    │                          └──janela encerrada──► EXPIRADO
    ├──estabelecimento cancela──► CANCELADO
    └──janela encerrada──► EXPIRADO
```

---

## Fluxo principal

```mermaid
flowchart TD
    A[Splash] -->|1º acesso| B[Onboarding - 3 páginas]
    A -->|introdução dispensada| C
    B -->|Começar| C{{Barra inferior}}
    C --> D[Início]
    C --> E[Minhas atividades]
    C --> F[Perfil]
    D -->|toque no card| G[Detalhes]
    D -->|Anunciar - estabelecimento| H[Novo anúncio]
    D -->|ícone i| S[Sobre o Pratin]
    G -->|Reservar - ONG| I[Confirmação da reserva]
    I --> E
    D -.->|sem perfil| J[Diálogo: perfil necessário]
    G -.->|sem perfil| J
    J --> K[Perfil - formulário]
    E -->|Confirmar retirada| L[Diálogo: código de retirada]
```

**Passo a passo da demonstração**

1. Na primeira abertura, a **Splash** prepara os dados de exemplo e o **Onboarding** apresenta o problema e a proposta.
2. No **Perfil**, o usuário escolhe ser Estabelecimento ou ONG (há o atalho "Preencher com perfil de demonstração").
3. Como **Padaria Pão Quente**, toca em **Anunciar**, preenche o formulário e publica: o anúncio aparece no Início.
4. Trocando o perfil para **ONG Mesa Cheia**, abre o anúncio, toca em **Reservar** e recebe o **código de retirada**.
5. Em **Minhas atividades**, a ONG vê a reserva ativa com o código.
6. De volta à Padaria, **Minhas atividades** mostra o anúncio reservado; **Confirmar retirada** pede o código e conclui a doação.
7. Fechando o app à força e abrindo de novo, o anúncio criado, o status "Retirado" e os favoritos continuam lá.

---

## Capturas de tela

Capturas reais do app no emulador Pixel 8 (Android 17).

| Splash | Onboarding | Onboarding: última página |
| :---: | :---: | :---: |
| <img src="docs/prints/01-splash.png" width="220"> | <img src="docs/prints/02-onboarding.png" width="220"> | <img src="docs/prints/03-onboarding-final.png" width="220"> |

| Início (dados de exemplo) | Sobre o Pratin | Sobre: ODS e equipe |
| :---: | :---: | :---: |
| <img src="docs/prints/04-inicio.png" width="220"> | <img src="docs/prints/05-sobre.png" width="220"> | <img src="docs/prints/05b-sobre-ods.png" width="220"> |

| Perfil: CNPJ inválido | Perfil salvo | Detalhes (ONG) |
| :---: | :---: | :---: |
| <img src="docs/prints/06-perfil-erro.png" width="220"> | <img src="docs/prints/07-perfil-salvo.png" width="220"> | <img src="docs/prints/12-detalhes-ong.png" width="220"> |

| Busca sem resultado | Seletor de data | Grade em paisagem |
| :---: | :---: | :---: |
| <img src="docs/prints/21-busca-vazia.png" width="220"> | <img src="docs/prints/11-seletor-data.png" width="220"> | <img src="docs/prints/22-grade-paisagem.png" width="320"> |

---

## Evidências (critérios 4, 5 e 7)

### 1. Criação de um registro

O formulário **não grava** dados inválidos (todos os erros aparecem embaixo dos campos). Com os dados corretos, o anúncio é gravado no Room e aparece no Início com a mensagem "Anúncio publicado!".

| Validação de campos obrigatórios | Formulário preenchido | Registro criado |
| :---: | :---: | :---: |
| <img src="docs/prints/08-anuncio-erros.png" width="220"> | <img src="docs/prints/09-anuncio-preenchido.png" width="220"> | <img src="docs/prints/10-anuncio-publicado.png" width="220"> |

### 2. Ação adicional sobre o registro (alterar status e favoritar)

A ONG reserva o "Sonho de creme" (status **Disponível → Reservado**) e recebe o código. O estabelecimento confirma a retirada com esse código (status **Reservado → Retirado**).

| Reserva confirmada (código) | Minhas reservas (ONG) | Meus anúncios (Estabelecimento) |
| :---: | :---: | :---: |
| <img src="docs/prints/13-confirmacao-reserva.png" width="220"> | <img src="docs/prints/14-minhas-reservas.png" width="220"> | <img src="docs/prints/15-meus-anuncios.png" width="220"> |

| Código de retirada | Retirada confirmada | Favorito + filtro Favoritos |
| :---: | :---: | :---: |
| <img src="docs/prints/16-codigo-retirada.png" width="220"> | <img src="docs/prints/17-retirada-confirmada.png" width="220"> | <img src="docs/prints/18-favoritos.png" width="220"> |

### 3. Permanência após reabrir o app

Depois das ações acima, o app foi **encerrado à força** (`adb shell am force-stop br.com.fiap.pratin`, equivalente a fechá-lo pelos apps recentes) e aberto de novo. O anúncio criado continua lá com o status **Retirado**, e o favorito continua marcado.

| Antes de fechar | Depois de reabrir: anúncios | Depois de reabrir: favoritos |
| :---: | :---: | :---: |
| <img src="docs/prints/17-retirada-confirmada.png" width="220"> | <img src="docs/prints/20-apos-reabrir-atividades.png" width="220"> | <img src="docs/prints/19-apos-reabrir-favoritos.png" width="220"> |

---

## Tecnologias

Kotlin · Jetpack Compose (Material 3) · Navigation Compose (rotas type-safe) · ViewModel + StateFlow · Room (KSP) · DataStore Preferences · Kotlin Coroutines/Flow · Coil (fotos por URL) · Core SplashScreen · JUnit 4 + kotlinx-coroutines-test · AndroidX Test

| Item | Versão |
| --- | --- |
| Gradle / AGP / Kotlin | 9.4.1 / 9.1.0 / 2.3.20 |
| Compose BOM / Navigation / Lifecycle | 2026.09.00 / 2.10.2 / 2.11.0 |
| Room / DataStore | 2.8.5 / 1.3.0 |
| `minSdk` / `targetSdk` / `compileSdk` | 26 / 37 / 37 |

## Arquitetura

MVVM com fluxo unidirecional de dados e separação entre interface, dados e regras de negócio:

```
 ui/       Composables (Route + Screen) e ViewModels com StateFlow   ← interface e lógica de apresentação
   │  observa o estado / chama ações
   ▼
 data/     Repositórios, Room (DAOs, entidades), DataStore, seed     ← dados
   │  usa
   ▼
 domain/   Modelos, validadores e regras em Kotlin puro              ← regras de negócio (sem Android)
```

```
app/src/main/java/br/com/fiap/pratin/
├── PratinApplication.kt, MainActivity.kt
├── di/            # AppContainer: injeção de dependência manual
├── domain/        # model, error, rules (CNPJ, anúncio, código, status), format
├── data/          # local/db (Room), local/prefs (DataStore), mapper, repository, seed
└── ui/            # navigation, theme, components, util, screens/<tela>/(Screen + ViewModel)
```

- O Room é a **única fonte de dados** dos anúncios e reservas. As telas observam `Flow`s, então a interface se atualiza sozinha quando os dados mudam.
- Reservar, cancelar e confirmar retirada rodam em **transações** do Room, que revalidam o status antes de alterar (se algo falhar, nada é gravado).
- A especificação completa usada no desenvolvimento está em [`spec/`](spec/README.md).

---

## Como executar

**Pré-requisitos:** Android Studio 2025.3 ou superior (JDK 21 embutido) e um emulador ou aparelho com Android 8.0 (API 26) ou superior.

1. Abra a pasta do projeto no Android Studio e aguarde a sincronização do Gradle.
2. Selecione o emulador e clique em **Run ▶**.

Pela linha de comando (Windows):

```powershell
.\gradlew.bat assembleDebug          # gera o APK de debug
.\gradlew.bat testDebugUnitTest      # 68 testes unitários (domínio, dados e ViewModels)
.\gradlew.bat connectedDebugAndroidTest   # 14 testes do Room no emulador
```

**Perfis de demonstração** (CNPJs fictícios, válidos pelo cálculo). No Perfil, use "Preencher com perfil de demonstração":

| Perfil | Nome | CNPJ |
| --- | --- | --- |
| Estabelecimento | Padaria Pão Quente | 11.222.333/0001-81 |
| Estabelecimento | Mercado Bom Preço | 11.444.777/0001-61 |
| Estabelecimento | Restaurante Sabor Caseiro | 98.765.432/0001-98 |
| Estabelecimento | Supermercado Economia | 33.444.555/0001-81 |
| Estabelecimento | Mercearia São José | 55.666.777/0001-81 |
| ONG | ONG Mesa Cheia | 12.345.678/0001-95 |

**Atalho para testar a retirada:** entre como **Restaurante Sabor Caseiro**, abra **Minhas atividades** e confirme a retirada da marmita com o código **`PX7K2Q`**.

---

## Requisitos da avaliação

| Critério | Onde está |
| --- | --- |
| 1. Contextualização e tela inicial | Onboarding, tela "Sobre o Pratin" (problema, público, ODS, equipe), Início com identidade visual; ODS justificados neste README |
| 2. Navegação | Navigation Compose com rotas type-safe: Splash, Onboarding, Início, Detalhes, Novo anúncio, Confirmação, Atividades, Perfil, Sobre |
| 3. Interface e experiência | Scaffold, TopAppBar, NavigationBar, LazyVerticalGrid, Card, Row/Column, TextField, Button, FilterChip, DatePicker/TimePicker, diálogos; Snackbar após cada ação e estado vazio em cada lista |
| 4. Dados e interação | Entidade `Alimento` (16 atributos) e `Reserva`; cadastro com validação; reservar, cancelar, confirmar retirada e favoritar; interface reativa |
| 5. Room | Banco `pratin.db` com tabelas `alimentos` e `reservas`, transações e schema exportado em `app/schemas` |
| 6. Código e estado | Pacotes `ui`/`data`/`domain`; um ViewModel com `StateFlow` por tela; 68 testes unitários e 14 instrumentados |
| 7. Documentação | Este README, capturas reais e evidências de criação, ação adicional e persistência |

## Observação

Protótipo acadêmico. Os dados ficam no próprio aparelho e os anúncios iniciais são **exemplos fictícios**. O app **não está integrado** a serviços externos, governamentais ou de terceiros. As fotos de alguns anúncios de exemplo são carregadas por URL e, sem internet, aparecem como o ícone da categoria.

## Vídeo pitch

🎬 &lt;Adicionar link do vídeo pitch aqui&gt;
