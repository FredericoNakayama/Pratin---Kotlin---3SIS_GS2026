# Pratin (Kotlin) — instruções para agentes de IA

Este repositório é o app Android **Pratin** (Kotlin + Jetpack Compose) da Global Solution FIAP 2026.
A pasta `D:\Pratin - Kotlin - 3SIS_GS2026` é a raiz do Git **e** do projeto Android (Gradle e `app/` diretamente nela).

**Antes de qualquer tarefa, leia [`spec/README.md`](spec/README.md).** A pasta `spec/` é a fonte da verdade: requisitos, arquitetura, UI, plano por fases, regras de Git e checklist de entrega.

Resumo do que mais importa:
- Uma fase do [`spec/05-plano.md`](spec/05-plano.md) por vez; não invente requisito — pergunte.
- Stack obrigatória: Kotlin, Compose (Material 3), Navigation Compose, ViewModel, Room. Sem backend.
- Verificação (PowerShell): `$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat assembleDebug testDebugUnitTest`.
- Commits em Conventional Commits (pt-BR), um por mudança lógica, na branch da fase. **Não faça push sem autorização.**
