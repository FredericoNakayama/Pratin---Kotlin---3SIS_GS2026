# 02 — Requisitos

Cada RF tem critérios de aceite (CA) no formato **Dado / Quando / Então**. Um RF só está pronto quando **todos** os seus CAs foram verificados no emulador (e, quando houver regra pura, por teste unitário).

---

## 1. Requisitos funcionais

### RF01 — Splash
- Tela T01. Mostra logo, nome "Pratin" e slogan enquanto o app prepara os dados (seed, RN16) e lê as flags de onboarding.
- **CA1** Dado o 1º acesso, quando a Splash termina, então vai para o Onboarding.
- **CA2** Dado `onboarding_visto = true` **e** `onboarding_nao_mostrar = true`, quando a Splash termina, então vai direto para o Início (RN01).
- **CA3** Quando o usuário toca em "voltar" no Início ou no Onboarding, então o app fecha (a Splash sai da pilha).
- **CA4** A Splash dura no mínimo ~1,2 s e no máximo o tempo de preparar os dados.

### RF02 — Onboarding
- Tela T02. 3 páginas (textos em [`04-ui.md`](04-ui.md) §6), com Pular, Voltar, Avançar/Começar e o checkbox "Não mostrar novamente" na última página.
- **CA1** Na 1ª página, "Voltar" não aparece; na última, "Avançar" vira "Começar".
- **CA2** "Pular" leva à última página.
- **CA3** Ao tocar em "Começar", grava `onboarding_visto = true` e `onboarding_nao_mostrar = <checkbox>` e vai ao Início, removendo o Onboarding da pilha.
- **CA4** Arrastar para os lados troca de página (pager).

### RF03 — Navegação principal (Shell)
- Barra inferior com 3 destinos: **Início**, **Atividades**, **Perfil**.
- **CA1** Trocar de aba não empilha telas: "voltar" a partir de uma aba que não seja o Início volta ao Início; a partir do Início, fecha o app.
- **CA2** Reselecionar a aba atual não recria a tela nem duplica a pilha.
- **CA3** Telas internas (Detalhes, Novo anúncio, Confirmação, Sobre) escondem a barra inferior e têm seta de voltar na `TopAppBar`.

### RF04 — Configurar e editar perfil
- Tela T06. Campos: tipo (Estabelecimento/ONG), nome, CNPJ, endereço.
- **CA1** Dado nome com menos de 3 caracteres, quando toca em "Salvar perfil", então mostra o erro embaixo do campo e não salva (RN02, RN17).
- **CA2** Dado CNPJ inválido (RN02), então mostra "CNPJ inválido." e não salva.
- **CA3** Dado endereço com menos de 5 caracteres, então mostra o erro e não salva.
- **CA4** Dado tudo válido, quando salva, então grava no DataStore (CNPJ só com dígitos), mostra o Snackbar "Perfil salvo." e a tela passa ao modo resumo.
- **CA5** No modo resumo, "Editar perfil" reabre o formulário preenchido.
- **CA6** Ao trocar de perfil, Início e Atividades refletem o novo perfil sem reiniciar o app.
- **CA7** Quando aberto a partir do diálogo "Perfil necessário", depois de salvar volta para a tela de origem.
- **CA8** (DEVERIA) O formulário oferece "Preencher com perfil de demonstração" com os perfis da RN16 (ex.: Padaria Pão Quente, ONG Mesa Cheia), para agilizar a demonstração.

### RF05 — Sobre o Pratin
- Tela T10, aberta pelo ícone ⓘ da `TopAppBar` do Início e pelo Perfil.
- **CA1** Mostra nome, slogan, problema, público beneficiado, como funciona (3 passos) e os ODS 2 e 12 com uma frase de justificativa cada.
- **CA2** Informa que é um protótipo acadêmico com dados locais de exemplo.

### RF06 — Listar alimentos disponíveis (Início)
- Tela T04. Lê do Room via `Flow` (RN05).
- **CA1** Mostra só alimentos `DISPONIVEL` com a janela aberta, ordenados por `dataValidade` e depois `retiradaFim`.
- **CA2** Cada card mostra foto (ou ícone da categoria), nome, quantidade + unidade, categoria, "Vence hoje/amanhã/em X dias" (RN14), janela de retirada, estabelecimento e o botão ♥.
- **CA3** Sem itens, mostra o estado vazio "Nenhum alimento disponível agora. Volte mais tarde!".
- **CA4** Quando um alimento muda (novo anúncio, reserva, cancelamento), a lista se atualiza sozinha, sem recarregar a tela.
- **CA5** Em larguras ≥ 600 dp, mostra grade de 2 colunas.

### RF07 — Buscar por nome
- **CA1** Digitar no campo filtra a lista por nome, sem diferenciar maiúsculas nem acentos.
- **CA2** Busca sem resultado mostra "Nenhum alimento encontrado."
- **CA3** O ícone ✕ limpa a busca.

### RF08 — Filtrar por categoria
- Chips: **Favoritos**, **Todos** (padrão) e as 8 categorias (RN13).
- **CA1** Seleção de categoria é única; "Todos" remove o filtro de categoria.
- **CA2** "Favoritos" é um filtro extra que liga/desliga e combina com categoria e busca.
- **CA3** Favoritos ligado e nenhum favorito disponível: "Nenhum dos seus favoritos está disponível agora."

### RF09 — Favoritar
- **CA1** Tocar no ♥ (card ou Detalhes) alterna o favorito e grava no Room (`alimentos.favorito`).
- **CA2** O ♥ muda de estado na hora, e o estado persiste após fechar e abrir o app (RN12).
- **CA3** Funciona com ou sem perfil.

### RF10 — Atualizar (puxar para atualizar)
- **CA1** Puxar a lista do Início ou de Atividades executa a expiração (RN11) e mostra o indicador até terminar.
- **CA2** Ao voltar para o app (ON_RESUME) a expiração também roda.

### RF11 — Ver detalhes
- Tela T05, recebe `alimentoId`.
- **CA1** Mostra imagem grande, status, quantidade, categoria, validade, janela, estabelecimento, endereço, selo "Doação gratuita" e "Declarado próprio para consumo pelo estabelecimento".
- **CA2** ♥ na `TopAppBar`.
- **CA3** ID inexistente: mensagem "Este alimento não está mais disponível." e botão para voltar.
- **CA4** Ação por perfil: ONG → botão **Reservar**; sem perfil → **Reservar** abre "Perfil necessário"; Estabelecimento → aviso "Apenas ONGs podem reservar alimentos.".
- **CA5** Se o alimento não estiver `DISPONIVEL` ou com a janela aberta, o botão Reservar não aparece e o status é mostrado.

### RF12 — Cadastrar anúncio
- Tela T07, só Estabelecimento. Validações da RN03 no ViewModel/domínio, nunca no Composable.
- **CA1** Campos: nome, categoria, quantidade, unidade, validade (DatePicker), início e fim da retirada (data + hora), endereço (pré-preenchido com o do perfil), URL da foto (opcional) com pré-visualização, checkbox "Declaro que o alimento está próprio para consumo".
- **CA2** Ao tocar em "Publicar anúncio" com campos inválidos, mostra **todas** as mensagens de erro embaixo dos campos e **não grava** nada.
- **CA3** Com tudo válido, grava no Room com `status = DISPONIVEL`, `estabelecimentoCnpj/Nome` do perfil, `criadoEm = agora`, mostra "Anúncio publicado!" e volta ao Início, onde o item aparece.
- **CA4** O botão mostra progresso e fica desabilitado durante a gravação (sem duplo envio).
- **CA5** Girar a tela não perde o que foi digitado.

### RF13 — Reservar alimento
- Só ONG. Fluxo da RN06.
- **CA1** Tocar em Reservar abre confirmação ("Reservar este lote para a sua ONG?").
- **CA2** Confirmado: alimento passa a `RESERVADO`, reserva `ATIVA` é criada com código (RN07) e o app vai para a Confirmação (T08), removendo Detalhes da pilha.
- **CA3** Se, no momento da gravação, o alimento não estiver mais disponível ou a janela tiver fechado, mostra a mensagem da RN06 e nada é gravado.

### RF14 — Confirmação de reserva
- Tela T08, recebe `reservaId`.
- **CA1** Mostra "Reserva confirmada!", o código em destaque e legível (fonte monoespaçada, espaçada), alimento, estabelecimento, endereço e janela.
- **CA2** Botão para copiar o código (Snackbar "Código copiado.").
- **CA3** "Ver minhas atividades" leva à aba Atividades; "Voltar ao início" leva ao Início. Nos dois casos "voltar" não retorna à Confirmação.

### RF15 — Reservas da ONG
- Aba Atividades com perfil ONG.
- **CA1** Lista as reservas do `ongCnpj` do perfil: nome do alimento, código, status, janela, estabelecimento e endereço.
- **CA2** Ordem: `ATIVA` primeiro, depois `criadaEm` decrescente.
- **CA3** Vazio: "Você ainda não reservou nenhum alimento."

### RF16 — Cancelar reserva
- **CA1** Reserva `ATIVA` com janela aberta mostra "Cancelar reserva" → confirmação → RN08 → Snackbar "Reserva cancelada.".
- **CA2** O alimento volta ao Início como disponível, sem recarregar manualmente.

### RF17 — Anúncios do estabelecimento
- Aba Atividades com perfil Estabelecimento.
- **CA1** Lista os anúncios do `estabelecimentoCnpj` do perfil: nome, foto/ícone, status, quantidade, janela e, se reservado, o nome da ONG.
- **CA2** Ordem: pendentes (`DISPONIVEL`/`RESERVADO`) primeiro, depois `criadoEm` decrescente.
- **CA3** Vazio: "Você ainda não publicou nenhum anúncio." com atalho "Anunciar".

### RF18 — Cancelar anúncio
- **CA1** Anúncio `DISPONIVEL` mostra "Cancelar anúncio" → confirmação → RN09 → Snackbar "Anúncio cancelado.".
- **CA2** O item some do Início e aparece como Cancelado em Atividades.

### RF19 — Confirmar retirada
- **CA1** Anúncio `RESERVADO` com janela aberta mostra "Confirmar retirada" → diálogo com campo de 6 caracteres (maiúsculas automáticas).
- **CA2** Código certo (RN10): alimento `RETIRADO`, reserva `CONCLUIDA`, Snackbar "Retirada confirmada. Obrigado por doar!".
- **CA3** Código errado: "Código inválido." dentro do diálogo; nada muda; o diálogo continua aberto.
- **CA4** Menos de 6 caracteres: "O código tem 6 caracteres." e o botão confirmar fica desabilitado.

### RF20 — Expiração automática
- RN11. Roda na Splash, no ON_RESUME de Início/Atividades e no puxar para atualizar.
- **CA1** Dado um anúncio `DISPONIVEL` ou `RESERVADO` com `retiradaFim <= agora`, quando a expiração roda, então ele fica `EXPIRADO` e a reserva `ATIVA` dele fica `EXPIRADA`.
- **CA2** Itens expirados não aparecem no Início e aparecem com o status em Atividades.

### RF21 — Bloqueio sem perfil
- **CA1** Sem perfil, "Anunciar" (FAB) e "Reservar" abrem o diálogo "Perfil necessário" com "Agora não" e "Configurar perfil".
- **CA2** Sem perfil, Atividades mostra "Configure seu perfil para ver suas atividades." e o botão "Configurar perfil".
- **CA3** ONG não vê o FAB "Anunciar".

---

## 2. Regras de negócio

| ID | Regra |
| --- | --- |
| **RN01** | **Destino da Splash:** Início se `onboarding_visto == true` **e** `onboarding_nao_mostrar == true`; senão, Onboarding. Chave ausente = `false`. |
| **RN02** | **CNPJ:** remover o que não for dígito; exigir 14 dígitos; rejeitar todos os dígitos iguais. 1º DV com pesos `5,4,3,2,9,8,7,6,5,4,3,2` sobre os 12 primeiros; `resto = soma % 11`; `DV = if (resto < 2) 0 else 11 - resto`. 2º DV com pesos `6,5,4,3,2,9,8,7,6,5,4,3,2` sobre os 13 primeiros. Guardar **só dígitos**; exibir `00.000.000/0000-00`. |
| **RN03** | **Anúncio válido:** nome com 2 a 60 caracteres (após `trim`) · categoria e unidade obrigatórias · `quantidade > 0` (aceita decimal com vírgula ou ponto) · `hoje <= dataValidade <= hoje + 7 dias` · `retiradaInicio` e `retiradaFim` obrigatórias · `retiradaFim > agora` · `retiradaInicio >= agora - 5 min` · `retiradaFim - retiradaInicio >= 1 h` · `retiradaFim <= dataValidade às 23:59:59` · endereço com 5+ caracteres · `fotoUrl` vazia ou começando com `http://` ou `https://` · declaração marcada. |
| **RN04** | **Dono:** anúncio pertence a `estabelecimentoCnpj`; reserva pertence a `ongCnpj`. Atividades filtra pelo CNPJ do perfil atual. Só o dono cancela ou confirma. |
| **RN05** | **Lista do Início:** `status == DISPONIVEL` **e** `agora < retiradaFim`. Ordem: `dataValidade` crescente, depois `retiradaFim` crescente. |
| **RN06** | **Reservar** (uma transação Room): (1) ler o alimento pelo ID; (2) se não existir → "Este alimento não está mais disponível."; se `status != DISPONIVEL` → "Este alimento já foi reservado."; se `agora >= retiradaFim` → "O prazo de retirada deste alimento terminou."; (3) alimento → `RESERVADO`; (4) inserir reserva `ATIVA` com código (RN07) e `criadaEm = agora`. Qualquer falha desfaz tudo (transação). A reserva vale para o lote inteiro. Só perfil ONG. |
| **RN07** | **Código de retirada:** 6 caracteres sorteados com `java.security.SecureRandom` do alfabeto `ABCDEFGHJKLMNPQRSTUVWXYZ23456789` (sem `O`, `0`, `I`, `1`). Comparação: `digitado.trim().uppercase() == codigo`. |
| **RN08** | **Cancelar reserva:** só se `reserva.status == ATIVA`, `agora < retiradaFim` e a reserva for da ONG do perfil. Efeito (transação): reserva `CANCELADA`, alimento `DISPONIVEL`. |
| **RN09** | **Cancelar anúncio:** só se `status == DISPONIVEL` e o anúncio for do perfil. Efeito: `CANCELADO`. |
| **RN10** | **Confirmar retirada:** só se `alimento.status == RESERVADO`, `agora < retiradaFim` e o anúncio for do perfil. Busca a reserva `ATIVA` do alimento; compara o código (RN07). Certo → (transação) reserva `CONCLUIDA`, alimento `RETIRADO`. Errado → erro "Código inválido.", nada muda. |
| **RN11** | **Expiração** (uma transação): reservas `ATIVA` cujo alimento tem `retiradaFim <= agora` → `EXPIRADA`; alimentos `DISPONIVEL` ou `RESERVADO` com `retiradaFim <= agora` → `EXPIRADO`. |
| **RN12** | **Favoritos** são locais (coluna `favorito` no Room) e funcionam com ou sem perfil. Favoritar não altera `atualizadoEm`. |
| **RN13** | **Categorias:** `PAES` (Pães e confeitaria), `HORTIFRUTI` (Hortifrúti), `LATICINIOS` (Laticínios), `CARNES` (Carnes e frios), `MERCEARIA` (Mercearia), `REFEICOES` (Refeições prontas), `BEBIDAS` (Bebidas), `OUTROS` (Outros). **Unidades:** `UNIDADE` (unidade/unidades), `KG` (kg), `LITRO` (litro/litros), `PACOTE` (pacote/pacotes), `CAIXA` (caixa/caixas), `PORCAO` (porção/porções). Plural quando quantidade ≠ 1. |
| **RN14** | **Dias para vencer:** `dataValidade == hoje` → "Vence hoje"; amanhã → "Vence amanhã"; senão "Vence em X dias". Vencido → "Vencido". |
| **RN15** | **Datas:** `java.time` (`LocalDate`, `LocalDateTime`), sem fuso. Na tela: `dd/MM/yyyy`, `dd/MM` e `HH:mm` com `Locale("pt", "BR")`. Janela no card: `Retirada 10/11 14:00–18:00` (se dias diferentes: `10/11 14:00 – 11/11 10:00`). Quantidade: sem casas decimais se inteira (`15`), senão vírgula (`8,5`). |
| **RN16** | **Seed:** na 1ª execução (tabela `alimentos` vazia), gravar os dados de exemplo de [`03-arquitetura.md`](03-arquitetura.md) §8 com datas **relativas ao dia atual**. Nunca roda de novo se já houver qualquer alimento. |
| **RN17** | **Perfil válido:** tipo obrigatório · nome com 3+ caracteres · CNPJ válido (RN02) · endereço com 5+ caracteres. |

---

## 3. Máquinas de estado

```
Alimento (StatusAlimento):
  DISPONIVEL ──reservar (RN06)──────────► RESERVADO
  RESERVADO  ──cancelar reserva (RN08)──► DISPONIVEL
  RESERVADO  ──confirmar código (RN10)──► RETIRADO    [final]
  DISPONIVEL ──cancelar anúncio (RN09)──► CANCELADO   [final]
  DISPONIVEL ──janela encerrada (RN11)──► EXPIRADO    [final]
  RESERVADO  ──janela encerrada (RN11)──► EXPIRADO    [final]

Reserva (StatusReserva):
  ATIVA ──confirmar código──► CONCLUIDA  [final]
  ATIVA ──ONG cancela───────► CANCELADA  [final]
  ATIVA ──janela encerrada──► EXPIRADA   [final]
```

Qualquer outra transição **NÃO DEVE** acontecer. O repositório valida o estado atual **dentro da transação** antes de alterar.

---

## 4. Requisitos não funcionais

| ID | Requisito |
| --- | --- |
| RNF01 | O projeto **DEVE** compilar com `gradlew assembleDebug` e abrir no Android Studio instalado sem ajustes manuais. Build quebrado = nota perdida nos critérios 6 e seguintes. |
| RNF02 | `gradlew testDebugUnitTest` **DEVE** passar. `gradlew lintDebug` **NÃO DEVE** ter erros (warnings tolerados, mas DEVERIAM ser revistos na F9). |
| RNF03 | Stack obrigatória: Kotlin, Jetpack Compose (Material 3), Navigation Compose, ViewModel, Room. Dependências permitidas em [`03-arquitetura.md`](03-arquitetura.md) §2. |
| RNF04 | Nenhum acesso a Room/DataStore na thread principal. Operações em `suspend` / `Flow` com `viewModelScope`. |
| RNF05 | Estado de tela sobrevive a rotação (ViewModel + `SavedStateHandle` quando necessário). |
| RNF06 | Plataforma: Android, `minSdk 26`; demonstração no emulador Pixel 8 (API 37) já instalado. |
| RNF07 | Idioma pt-BR em textos e datas. |
| RNF08 | Acessibilidade: áreas de toque ≥ 48 dp; `contentDescription` em ícones de ação e imagens informativas; contraste do tema Material 3. |
| RNF09 | Testes unitários (JUnit) para `CnpjValidator`, `CodigoGenerator`, `AnuncioValidator`, `PerfilValidator`, formatadores e regras de transição de status. |
| RNF10 | Código original da equipe, comentado onde a regra não é óbvia; a equipe precisa conseguir explicar cada parte. |
| RNF11 | App funciona offline. Só as fotos por URL precisam de internet, com fallback para o ícone. |
