# 06 — Qualidade, Git e entrega

## 1. Estratégia de testes

| Nível | Onde | O quê | Obrigatório? |
| --- | --- | --- | --- |
| Unitário JVM | `app/src/test` | Domínio inteiro (validadores, gerador de código, formatadores, regras de status), mappers, seed | **Sim** (RNF09) |
| ViewModel com fakes | `app/src/test` | `NovoAnuncioViewModel` (não grava com erro; grava com sucesso), `DetalhesViewModel` (erro de regra vira mensagem) usando repositórios fake e `kotlinx-coroutines-test` (`StandardTestDispatcher` + `Dispatchers.setMain`) | Deveria |
| Instrumentado Room | `app/src/androidTest` | Transações dos repositórios em banco em memória (ver F3) | Deveria |
| Manual (emulador) | — | Critérios de aceite de cada RF + roteiro do §6 | **Sim** |

Regras:
- Testes com nome descritivo em português, com crase: `` fun `rejeita janela menor que uma hora`() ``.
- "Agora" sempre fixo nos testes (`Clock.fixed(...)` ou parâmetro).
- Teste que falha **não** é apagado nem marcado `@Ignore` para "passar": corrija o código ou discuta a regra.

## 2. `.gitignore` (mínimo)

```gitignore
# Gradle / build
.gradle/
build/
/app/build/
.kotlin/
*.apk
*.aab
*.ap_
*.dex
# Local
local.properties
*.keystore
*.jks
captures/
.externalNativeBuild/
.cxx/
# IDE e ferramentas locais
.idea/
*.iml
.claude/settings.local.json
.DS_Store
Thumbs.db
# Artefatos de entrega
/entrega/
```

`app/schemas/` **é versionado** (schema do Room). `gradle/wrapper/gradle-wrapper.jar` **é versionado**.

## 3. Fluxo de branches

- `main`: sempre compilando; só recebe merge de fase concluída (DoD atendido).
- Uma branch por fase: `feat/f02-dominio`, `feat/f06-inicio-detalhes`, `docs/f10-readme` etc.
- Correções depois do merge: `fix/<assunto-curto>`.
- Merge com `git merge --no-ff feat/fNN-...` para preservar o histórico da fase; apagar a branch local depois.
- **`git push` e criação de tags remotas só com autorização explícita do usuário.** Nunca `push --force` em `main`.
- Antes do merge: `git status` limpo, verificação da fase passando, tabela de status da Spec atualizada.

## 4. Commits (Conventional Commits, em pt-BR)

Formato:

```
<tipo>(<escopo opcional>): <descrição no imperativo, minúscula, sem ponto, até ~72 caracteres>

<corpo opcional: o porquê da mudança, não o como; quebre linhas em ~72 colunas>

Refs: RF12, RN03
```

| Tipo | Quando |
| --- | --- |
| `feat` | Nova funcionalidade visível ou regra nova |
| `fix` | Correção de bug |
| `test` | Só testes |
| `refactor` | Mudança interna sem alterar comportamento |
| `style` | Formatação, sem mudança de lógica |
| `docs` | README, Spec, KDoc |
| `build` | Gradle, dependências, versões |
| `chore` | Configuração, `.gitignore`, tarefas de manutenção |

Escopos sugeridos: `domain`, `data`, `ui`, `nav`, `splash`, `onboarding`, `inicio`, `detalhes`, `reserva`, `anuncio`, `atividades`, `retirada`, `expiracao`, `perfil`, `sobre`, `favoritos`, `spec`.

Boas práticas:
- **Atômico:** um commit = uma mudança lógica que compila. Não misture refatoração com funcionalidade.
- **Nada de "WIP", "ajustes", "update"** como mensagem.
- Revise o diff (`git diff --staged`) antes de cada commit; adicione arquivos pelo nome (evite `git add .` às cegas).
- Commits feitos com ajuda de IA terminam com o trailer de coautoria pedido pelo ambiente (ex.: `Co-Authored-By: ...`).

Exemplos:
```
feat(domain): valida CNPJ pelos dígitos verificadores

Refs: RN02
```
```
feat(reserva): reserva o lote em transação única no Room

Antes, uma falha entre atualizar o alimento e criar a reserva podia
deixar o alimento reservado sem reserva. A transação desfaz os dois.

Refs: RF13, RN06
```

## 5. README final (estrutura obrigatória — critério 7)

1. Logo, nome, slogan e uma frase do que é o app.
2. **Integrantes:** tabela com **nome completo e RM** (Frederico Nakayama Sant'anna — RM555350; João Pedro Veloso Pilon — RM556199).
3. **Problema** e **público beneficiado**.
4. **Proposta** (como funciona em 3 passos) e alinhamento com o CitizenTech.
5. **ODS 2 e ODS 12** com a justificativa da conexão (texto de `01-produto.md` §2).
6. **Funcionalidades** (lista por perfil) e ciclo de vida do alimento.
7. **Tecnologias:** Kotlin, Jetpack Compose (Material 3), Navigation Compose, ViewModel + StateFlow, Room, DataStore, Coil, Coroutines/Flow, JUnit — e a arquitetura (diagrama de camadas + pastas).
8. **Como executar:** pré-requisitos (Android Studio 2025.3+, JDK 21 embutido, emulador API 26+), abrir a pasta, sincronizar o Gradle, Run ▶; comandos `gradlew assembleDebug` e `gradlew testDebugUnitTest`; perfis de demonstração (CNPJs) e o código `PX7K2Q`.
9. **Fluxo principal** explicado em 5–8 passos, com prints.
10. **Evidências** (seção própria, com prints numerados): criação de registro, ação adicional, persistência após reabrir (ver §6).
11. Observação de protótipo acadêmico: dados locais de exemplo; nenhuma integração com serviços externos ou públicos.
12. Link do vídeo pitch e link do repositório.

## 6. Roteiro de demonstração e prints

Use o emulador `Pixel_8`, tema claro, idioma do sistema em português (para os seletores de data). Para gravar/limpar o estado:

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb shell pm clear br.com.fiap.pratin            # zera o app (1º acesso)
& $adb exec-out screencap -p > docs\prints\NN-nome.png   # captura a tela atual
& $adb shell am force-stop br.com.fiap.pratin       # "fecha" o app de verdade
```

> No PowerShell 5.1, o `>` pode corromper binários. Se o PNG não abrir, use: `& $adb shell screencap -p /sdcard/s.png; & $adb pull /sdcard/s.png docs\prints\NN-nome.png`.

| # | Print | Evidencia |
| --- | --- | --- |
| 01 | Splash | Identidade visual (crit. 1) |
| 02 | Onboarding (pág. 1) e 03 (última, com "Não mostrar novamente") | Contextualização (crit. 1) |
| 04 | Início com seed, chips e FAB | Tela inicial, LazyColumn, Card (crit. 1, 3) |
| 05 | Sobre o Pratin | Problema, público, ODS (crit. 1) |
| 06 | Perfil — formulário com erro de CNPJ | Validação (crit. 4) |
| 07 | Perfil salvo (Estabelecimento) | DataStore, mensagem de ação (crit. 3) |
| 08 | Novo anúncio com erros de validação | Campo obrigatório validado (crit. 4) |
| 09 | Novo anúncio preenchido | Cadastro (crit. 4) |
| 10 | Início com "Anúncio publicado!" e o novo item | **Criação de registro** + UI reflete (crit. 4, 7) |
| 11 | Detalhes como ONG | Detalhamento (crit. 2) |
| 12 | Confirmação com código | Ação adicional: alterar status (crit. 4, 7) |
| 13 | Minhas reservas (ONG) | Acompanhamento (crit. 2) |
| 14 | Meus anúncios (Estab.) com "Confirmar retirada" | Ação adicional (crit. 4) |
| 15 | Diálogo do código / "Retirada confirmada" | **Ação adicional** (crit. 7) |
| 16 | Favorito marcado + filtro Favoritos | Ação adicional: favoritar (crit. 4) |
| 17 | Após `force-stop` e reabrir: o anúncio criado, o favorito e os status continuam | **Persistência após reabrir** (crit. 5, 7) |
| 18 | Estado vazio (ex.: busca sem resultado ou Atividades de perfil novo) | Estado sem dados (crit. 3) |
| 19 | (Opcional) App Inspection → Database Inspector com as tabelas | Room (crit. 5) |
| 20 | (Opcional) Grade em tablet | Responsividade |

Para o print 17, mostre no README a sequência: print antes → comando `force-stop` (ou fechar pelos apps recentes) → print depois.

## 7. ZIP de entrega

Gere a partir do Git, o que já exclui `build/`, `.gradle/`, `.idea/` e `local.properties`:

```powershell
New-Item -ItemType Directory -Force entrega | Out-Null
git archive --format=zip --prefix=Pratin-Kotlin/ -o entrega\Pratin-Kotlin-3SIS_GS2026.zip HEAD
```

Validação obrigatória: extraia o ZIP em uma pasta temporária, abra no Android Studio (ou rode `gradlew.bat assembleDebug` lá) e confirme que compila. Confira que o ZIP contém `README.md`, `docs/prints/`, `spec/`, `app/`, `gradle/`, `gradlew*`, `settings.gradle.kts` e **não** contém `build/`.

## 8. Checklist final de entrega (até 19/11/2026, 23h59)

- [ ] `main` com todas as fases mergeadas; `assembleDebug`, `testDebugUnitTest` e `lintDebug` sem erros.
- [ ] README completo (§5) com prints reais (§6) e nomes + RMs.
- [ ] ZIP gerado e validado (§7).
- [ ] Tag `v1.0.0` criada (e enviada ao GitHub, se o usuário autorizar).
- [ ] Vídeo pitch gravado e link no README.
- [ ] ZIP enviado na área de entrega da disciplina (feito pela equipe).
