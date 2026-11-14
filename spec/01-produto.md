# 01 — Produto

## 1. Problema e proposta

Padarias, mercados e restaurantes jogam fora alimentos ainda próprios para consumo porque estão perto do vencimento. Ao mesmo tempo, ONGs que atendem famílias em insegurança alimentar têm dificuldade para conseguir doações. Comércio e ONG costumam estar no mesmo bairro, mas não se conectam.

O **Pratin** conecta as duas pontas:

1. O **estabelecimento** anuncia um lote de alimento próprio para consumo, com validade de até 7 dias e uma janela de retirada.
2. A **ONG** encontra o lote, reserva e recebe um **código de retirada** de 6 caracteres.
3. Na retirada, o estabelecimento digita o código e a doação é concluída.

A doação é **sempre gratuita**. Não há login: o usuário escolhe um perfil (Estabelecimento ou ONG) e o CNPJ identifica o dono dos anúncios e das reservas.

## 2. Alinhamento com o CitizenTech

| Pergunta | Resposta |
| --- | --- |
| Problema real | Alimento bom vai para o lixo enquanto famílias do mesmo bairro passam fome. |
| Público beneficiado | Pessoas em insegurança alimentar atendidas por ONGs (beneficiário final) · voluntários de ONGs que buscam doações · pequenos empreendedores (padarias, mercados, restaurantes) sem canal simples para doar. |
| Território | Bairros de metrópoles e cidades pequenas, onde comércio e ONG estão próximos. |
| Melhoria concreta | Doar leva poucos toques; a retirada é organizada com código e horário; menos desperdício e mais refeições. |
| Como medir | Lotes retirados, quantidade redistribuída e tempo entre anúncio e reserva — tudo calculável a partir dos registros do Room. |

### ODS relacionados (justificativa vai para o README)

- **ODS 2 — Fome zero e agricultura sustentável** (meta 2.1: acesso a alimentos seguros e nutritivos). O app leva alimento que seria descartado até quem precisa, via ONGs.
- **ODS 12 — Consumo e produção responsáveis** (meta 12.3: reduzir pela metade o desperdício de alimentos no varejo e no consumidor). O app dá ao varejo um destino útil para o excedente.

## 3. Perfis e permissões

| Ação | Sem perfil | Estabelecimento | ONG |
| --- | :---: | :---: | :---: |
| Ver lista, detalhes e "Sobre" | ✅ | ✅ | ✅ |
| Buscar, filtrar, favoritar, atualizar | ✅ | ✅ | ✅ |
| Criar anúncio | 🔒 | ✅ | ❌ (botão oculto) |
| Reservar | 🔒 | ❌ (aviso) | ✅ |
| Ver/cancelar as próprias reservas | 🔒 | ❌ | ✅ |
| Ver/cancelar os próprios anúncios | 🔒 | ✅ | ❌ |
| Confirmar retirada pelo código | ❌ | ✅ (só nos próprios anúncios) | ❌ |

🔒 = a ação aparece, mas abre o diálogo "Perfil necessário", que leva ao formulário de perfil.

> **Demonstração em um único aparelho:** como tudo é local (ADR-01), o ciclo completo é demonstrado trocando o perfil: como Estabelecimento, anuncie; troque para ONG e reserve; volte ao Estabelecimento e confirme a retirada com o código. Os dados persistem entre as trocas porque o dono é o CNPJ gravado em cada registro.

## 4. Escopo

### 4.1 Dentro do escopo
- Splash e Onboarding (3 páginas, "Não mostrar novamente").
- Tela "Sobre o Pratin" com problema, público e ODS.
- Perfil local (Estabelecimento ou ONG) com validação de CNPJ.
- Cadastro de anúncio com validações (validade, quantidade, janela de retirada, declaração).
- Lista de alimentos com busca, filtro por categoria, favoritos e puxar para atualizar.
- Detalhes, reserva com código, confirmação de reserva.
- Minhas atividades: reservas (ONG) e anúncios (Estabelecimento), cancelamentos, confirmação de retirada.
- Expiração automática de anúncios e reservas com a janela encerrada.
- Dados de exemplo (seed) gravados no Room na primeira abertura.
- Persistência local: **Room** (anúncios, reservas, favoritos) e **DataStore** (perfil e onboarding).

### 4.2 Fora do escopo (não implementar)
- Login, senha, autenticação, backend, sincronização na nuvem, MockAPI/Retrofit.
- Consulta de CNPJ na Receita Federal (só o cálculo dos dígitos).
- Preço, pagamento, notificações, chat, mapas, geolocalização.
- Reserva parcial de lote (a reserva vale para o lote inteiro).
- Edição de anúncio já publicado e exclusão física de registros (as ações adicionais do app são **mudanças de status** e **favoritar**).
- Câmera/galeria para a foto do anúncio (a foto é por URL; ver ADR-06).

### 4.3 Antimetas (falhou mesmo que "funcione")
- Uma tela que só mostra dados fixos, sem ler/gravar no Room.
- Dados que somem ao fechar e abrir o app.
- Texto do app ou do README que sugira integração real com serviços externos ou órgãos públicos.
- Projeto que não compila em uma máquina limpa com Android Studio.

## 5. Glossário

| Termo | Significado |
| --- | --- |
| Alimento / Anúncio / Lote | Registro publicado por um estabelecimento. Um anúncio = um lote. |
| Janela de retirada | Intervalo `retiradaInicio`–`retiradaFim` em que a ONG pode retirar. |
| Janela aberta | `agora < retiradaFim`. |
| Código de retirada | 6 caracteres gerados na reserva e conferidos pelo estabelecimento. |
| Dono | Perfil cujo CNPJ está em `estabelecimentoCnpj` (anúncio) ou `ongCnpj` (reserva). |
| Seed | Dados de exemplo gravados no Room na primeira execução. |

## 6. Decisões de arquitetura (ADRs)

Formato curto: decisão, motivo, consequência. Para mudar uma decisão, edite o ADR e registre a data.

| ID | Decisão | Motivo | Consequência |
| --- | --- | --- | --- |
| ADR-01 | **Room é a única fonte de dados** dos anúncios e reservas, com seed na 1ª abertura. Sem MockAPI. | A rubrica pontua persistência com Room de registros criados e modificados; local evita dependência de rede e ambiguidade na correção. | Todos os perfis convivem no mesmo aparelho; regras que no Flutter rodavam contra a API rodam em transações do Room. |
| ADR-02 | **DataStore Preferences** para perfil e flags de onboarding. | São preferências, não registros; DataStore é o substituto atual do SharedPreferences. | Perfil observado como `Flow`. |
| ADR-03 | **MVVM + fluxo unidirecional** (UiState imutável em `StateFlow`, eventos chamando métodos do ViewModel). | Exigência de ViewModel e de UI que reflete mudanças; padrão recomendado pelo Android. | Um ViewModel por tela; Composables "burros". |
| ADR-04 | **Injeção de dependência manual** (`AppContainer` na `Application` + `viewModelFactory`). Sem Hilt. | Menos configuração e menos risco de build com AGP 9/KSP; mais fácil de explicar na banca. | Um ponto único cria banco, DataStore e repositórios. |
| ADR-05 | **Navigation Compose com rotas type-safe** (`@Serializable`), passando **apenas IDs**. Não usar Navigation 3. | A rubrica exige Navigation Compose; type-safety evita erros de rota; IDs evitam estado duplicado. | Telas de destino buscam o registro pelo ID no ViewModel (`SavedStateHandle.toRoute()`). |
| ADR-06 | **Foto do anúncio por URL** (Coil) com fallback para o ícone da categoria. Sem câmera/galeria. | Fora da rubrica; reduz permissões e complexidade. | Sem internet, as fotos caem no ícone; o app continua funcional. |
| ADR-07 | **Domínio em Kotlin puro** (`domain/`) sem dependências Android. | Regras testáveis com JUnit na JVM, sem emulador. | Mensagens de erro do domínio são enums; a UI as converte em `strings.xml`. |
| ADR-08 | **Relógio injetável** (`java.time.Clock`). | Expiração, validade e janela dependem do "agora"; testes precisam fixá-lo. | Nenhum código chama `LocalDateTime.now()` sem passar o `Clock`. |
| ADR-09 | **Splash em Compose com o mesmo fundo do splash do sistema** (`core-splashscreen`). | Manter a Splash pedida pela equipe sem "splash duplo" visível no Android 12+. | O sistema mostra o fundo verde com o ícone e emenda na Splash Compose (logo + slogan). |
