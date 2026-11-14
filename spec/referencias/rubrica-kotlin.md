# Demanda oficial — Android Kotlin Developer (GS 2026, 2º semestre)

> Transcrição do documento do professor (`GS_2026_2SEM_Android_Kotlin_Developer_CitizenTech.docx`), mantida aqui para a IA consultar sem precisar do `.docx`. O documento original **não fica dentro da pasta do projeto** (decisão da equipe); está guardado fora dela, em `D:\`. Em caso de divergência, vale o documento original.

**Professor:** Ewerton Luiz de Lima Carreira · **Valor:** 10,0 pontos
**Desafio:** CitizenTech — tecnologia que melhora a vida das pessoas.

## Objetivo da entrega

Desenvolver um aplicativo Android funcional, conectado ao desafio CitizenTech, que permita ao usuário consultar e registrar informações relevantes para a solução escolhida. O projeto deve ir além de telas estáticas: é necessário demonstrar **navegação, interação, gerenciamento de estado e persistência local**.

## Critérios de avaliação

### 1. Contextualização da solução e tela inicial — 1,0
- Apresentar o nome do aplicativo, seu objetivo e o público beneficiado.
- Explicar qual problema real a solução pretende reduzir ou resolver.
- Identificar pelo menos um ODS relacionado e justificar a conexão no README.
- Tela inicial com identidade visual básica e acesso às funcionalidades.

### 2. Navegação entre telas — 1,5
- Navegação funcional com **Navigation Compose**.
- No mínimo **três telas com finalidades distintas** (ex.: início, listagem/acompanhamento, cadastro/detalhamento).
- Navegar e retornar entre os fluxos sem travamentos.

### 3. Interface e experiência de uso — 1,5
- Componentes Jetpack Compose / Material 3 (Column, Row, Card, LazyColumn, Scaffold, TextField, Button…).
- Conteúdo organizado, legível e coerente com o público.
- **Mensagens ou indicadores claros para ações realizadas e para estados sem dados.**

### 4. Dados, cadastro e interação funcional — 2,0
- Entidade/modelo com **pelo menos três atributos relevantes**, além de eventual identificador.
- Cadastrar e visualizar registros pelo aplicativo.
- **Pelo menos uma ação adicional**: editar, excluir, alterar status ou marcar como concluído/favorito.
- **Validar ao menos um campo obrigatório**, impedindo registro inválido.
- A interface reflete as alterações feitas pelo usuário.

### 5. Persistência local com Room — 1,5
- **Room** para armazenar os registros criados ou modificados pelo usuário.
- Registros continuam disponíveis **após fechar e abrir novamente** o app.
- Não é necessário autenticação, backend ou sincronização na nuvem.

### 6. Organização do código e gerenciamento de estado — 1,5
- Nomes claros e separação básica entre **interface, dados e lógica de apresentação**.
- **ViewModel** para manter e disponibilizar o estado das funcionalidades principais.
- Interface atualiza corretamente quando os dados mudam.
- Projeto **compila e executa no Android Studio**.

### 7. Documentação e demonstração — 1,0
- `README.md` com: problema, público beneficiado, proposta, ODS, funcionalidades, tecnologias e instruções para executar.
- Capturas de tela reais do app em execução e explicação breve do fluxo principal.
- Evidência visual da **criação de um registro**, da **ação adicional** e da **permanência após reabrir o app**.

## Regras de escopo

- **Obrigatório:** Kotlin, Jetpack Compose, Navigation Compose, ViewModel e Room.
- **Permitido:** dados simulados (mockados), recursos locais e APIs públicas pertinentes.
- **Opcional, sem pontuação adicional:** API, Retrofit, notificações, geolocalização, autenticação, mapas, IA.
- Não exige backend próprio, Play Store, integração governamental ou dados em tempo real.
- Protótipo acadêmico: demonstrar a proposta de valor **sem alegar** que serviços externos foram integrados.
- Deve funcionar no emulador ou em dispositivo Android compatível.

## Entrega

1. Projeto Android completo em **.zip**, importável no Android Studio, **sem pastas de build** ou arquivos locais desnecessários.
2. `README.md` dentro do projeto com as informações e evidências, incluindo **nome completo e RM de cada integrante** (1 a 3).
3. Vídeo pitch (entrega institucional; a nota da disciplina segue só a rubrica).

**Prazo:** 19/11/2026, 23h59 (Brasília).

## Orientações importantes

- O foco é **funcionalidade comprovada** e aplicação dos conceitos, não quantidade de funcionalidades.
- Layouts estáticos sem operações funcionais não atendem cadastro, interação e persistência.
- O que não puder ser executado ou demonstrado não recebe pontuação integral.
- A equipe deve compreender, adaptar e explicar o código entregue.
