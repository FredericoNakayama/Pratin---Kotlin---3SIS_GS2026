# 03 — Arquitetura e padrões de código

## 1. Visão geral

MVVM com fluxo unidirecional (ADR-03), três camadas e dependências só para baixo:

```
 ui/        Composables (Route + Screen) e ViewModels         ← interface + lógica de apresentação
   │  observa StateFlow / chama métodos
   ▼
 data/      Repositórios, Room (DAOs, entidades), DataStore, seed   ← dados
   │  usa
   ▼
 domain/    Modelos, enums, validadores e regras em Kotlin puro      ← regras de negócio (sem Android)
```

- `ui` conhece `data` (repositórios) e `domain`. `data` conhece `domain`. `domain` não conhece ninguém.
- Os ViewModels recebem **interfaces** de repositório (facilita testes com fakes).
- A UI nunca importa entidades Room (`*Entity`); só modelos de `domain`.

## 2. Stack e dependências

### 2.1 Ambiente da máquina (verificado em 14/11/2026)

| Item | Valor |
| --- | --- |
| Android Studio | 2025.3.3 (build 253) — JDK embutido (JBR) 21 em `C:\Program Files\Android\Android Studio\jbr` |
| SDK | `%LOCALAPPDATA%\Android\Sdk` com `android-36`, `android-36.1`, `android-37.0` |
| Emulador | AVD `Pixel_8` (imagem `android-37.0` Google Play, x86_64) |
| Cache Gradle local | AGP 9.1.1 / 9.2.1 · Kotlin 2.3.x · Gradle 9.3.1 / 9.4.1 · Room 2.7.1 |

### 2.1.1 Versões adotadas na F0 (14/11/2026)

| Item | Versão | Item | Versão |
| --- | --- | --- | --- |
| Gradle (wrapper) | 9.4.1 | Compose BOM | 2026.09.00 |
| AGP | 9.1.0 (Kotlin embutido; máximo aceito pelo Android Studio 2025.3.3) | Navigation Compose | 2.10.2 |
| Kotlin (plugins compose/serialization) | 2.3.20 | Lifecycle | 2.11.0 |
| KSP | 2.3.14 | Room | 2.8.5 |
| `compileSdk` / `targetSdk` / `minSdk` | **37.0** / 37 / 26 | DataStore | 1.3.0 |
| Java/Kotlin JVM target | 17 | Coil | 3.6.3 |

> `compileSdk 37` é obrigatório: Compose BOM 2026.09, Navigation 2.10 e Coil 3.6 recusam compilar contra a API 36 (`checkDebugAarMetadata`).
> A API 37 só existe no SDK como **`android-37.0`** (versão menor no nome). Declare `compileSdk { version = release(37) { minorApiLevel = 0 } }`: com `compileSdk = 37` a sincronização do Android Studio 2025.3 procura `android-37` e falha ("Could not find compile target android-37"). O AGP 9.1.0 foi testado até a API 36.1; o aviso correspondente está suprimido em `gradle.properties` com a justificativa.
> Pela linha de comando, o build precisa de `local.properties` com `sdk.dir=C\:\\Users\\<usuario>\\AppData\\Local\\Android\\Sdk` (o Android Studio cria sozinho; o arquivo **não** é versionado).

### 2.2 Regra de versões

1. **Gere o projeto pelo template "Empty Activity" (Compose) do Android Studio instalado.** As versões de AGP, Kotlin, Gradle e Compose BOM do template são a referência mais confiável para esta máquina.
2. Toda versão fica em `gradle/libs.versions.toml` (version catalog). Nada de versão "chumbada" no `build.gradle.kts`.
3. Ao adicionar uma biblioteca, use a **última versão estável compatível** com o Kotlin/AGP do template. Se o build falhar por incompatibilidade, ajuste a versão (não troque a biblioteca) e registre no commit.
4. **O AGP NÃO DEVE passar da versão máxima aceita pelo Android Studio da equipe.** O build pela linha de comando funciona com versões mais novas, mas o Android Studio recusa abrir o projeto ("incompatible version of the Android Gradle plugin"). Confira no aviso do próprio Android Studio ou na versão gerada pelo template. Um cache do Gradle com versões mais novas **não** prova compatibilidade. (Em 14/11/2026 o AGP 9.2.1 foi trocado por 9.1.0 por esse motivo.)
5. Com **AGP 9.x**, o suporte a Kotlin é embutido: **não** aplique `org.jetbrains.kotlin.android` se o template não aplicar. Os plugins `org.jetbrains.kotlin.plugin.compose` e `org.jetbrains.kotlin.plugin.serialization` continuam necessários.
6. `compileSdk` = o exigido pelas bibliotecas (hoje 37), `targetSdk` = 36 ou superior; **`minSdk = 26`** (java.time nativo).

### 2.3 Dependências permitidas

| Uso | Artefato(s) |
| --- | --- |
| Compose + Material 3 | `androidx.compose:compose-bom` (platform), `ui`, `ui-graphics`, `ui-tooling-preview`, `material3`, `material-icons-extended`, `ui-tooling` (debug), `ui-test-manifest` (debug) |
| Activity | `androidx.activity:activity-compose` |
| Navegação | `androidx.navigation:navigation-compose` (≥ 2.8, rotas type-safe) |
| Serialização (rotas) | `org.jetbrains.kotlinx:kotlinx-serialization-json` + plugin `org.jetbrains.kotlin.plugin.serialization` |
| ViewModel / ciclo de vida | `androidx.lifecycle:lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `lifecycle-runtime-ktx` |
| Room | `androidx.room:room-runtime`, `room-ktx`, `room-compiler` (via **KSP**), plugin `androidx.room` (exporta o schema) |
| KSP | plugin `com.google.devtools.ksp` (versão compatível com o Kotlin do projeto) |
| DataStore | `androidx.datastore:datastore-preferences` |
| Splash | `androidx.core:core-splashscreen` |
| Imagens | `io.coil-kt.coil3:coil-compose`, `io.coil-kt.coil3:coil-network-okhttp` |
| Core | `androidx.core:core-ktx` |
| Testes (JVM) | `junit:junit` 4, `org.jetbrains.kotlinx:kotlinx-coroutines-test` |
| Testes (instrumentados, opcional) | `androidx.test.ext:junit`, `androidx.test:runner`, `androidx.room:room-testing`, `androidx.compose.ui:ui-test-junit4` |

**Proibidos sem aprovação:** Hilt/Dagger/Koin, Retrofit/Ktor, Firebase, Navigation 3, Accompanist, qualquer biblioteca de "UI kit".

### 2.4 Manifest
- Permissão `android.permission.INTERNET` (só para as fotos por URL).
- `android:name=".PratinApplication"`, tema de partida `Theme.Pratin.Starting` (splash, ADR-09), `android:label="@string/app_name"`.
- Uma única Activity (`MainActivity`, `ComponentActivity`), com `enableEdgeToEdge()`.

## 3. Estrutura do repositório e dos pacotes

**Pasta do projeto: `D:\Pratin - Kotlin - 3SIS_GS2026`.** Ela é ao mesmo tempo a raiz do Git e a raiz do projeto Android: `settings.gradle.kts`, `gradlew` e `app/` ficam **diretamente nela**, nunca em uma subpasta (ex.: `D:\Pratin - Kotlin - 3SIS_GS2026\Pratin\`). Arquivos que não fazem parte do projeto (como o `.docx` do professor) **não** ficam nesta pasta.

```
Pratin---Kotlin---3SIS_GS2026/          ← raiz do Git = raiz do projeto Android
├── CLAUDE.md                           ← ponteiro curto para a Spec (para agentes de IA)
├── README.md                           ← documentação de entrega (critério 7)
├── spec/                               ← ESTA Spec
├── docs/prints/                        ← capturas de tela usadas no README
├── settings.gradle.kts, build.gradle.kts, gradle.properties, gradle/ (wrapper + libs.versions.toml)
├── gradlew, gradlew.bat
└── app/
    ├── build.gradle.kts
    ├── schemas/                        ← schema JSON exportado pelo Room (versionado)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/ (values/strings.xml, values/themes.xml, drawable/, mipmap-*/)
        │   └── java/br/com/fiap/pratin/
        ├── test/java/br/com/fiap/pratin/          ← testes JVM (domínio, ViewModels com fakes)
        └── androidTest/java/br/com/fiap/pratin/   ← testes de DAO (opcional)
```

Pacote base: **`br.com.fiap.pratin`** (applicationId igual).

```
br.com.fiap.pratin
├── PratinApplication.kt            # cria o AppContainer
├── MainActivity.kt                 # splash do sistema, edge-to-edge, PratinTheme { PratinApp() }
├── di/
│   └── AppContainer.kt             # banco, DataStore, Clock, repositórios (ADR-04)
├── domain/
│   ├── model/                      # Alimento, Reserva, Perfil, ReservaDetalhada, AnuncioDetalhado,
│   │                               # NovoAnuncio, enums (TipoPerfil, StatusAlimento, StatusReserva,
│   │                               # Categoria, Unidade)
│   ├── rules/                      # CnpjValidator, CodigoGenerator, AnuncioValidator,
│   │                               # PerfilValidator, RegrasStatus (transições e "pode…?")
│   ├── error/                      # ErroRegra (enum), RegraNegocioException, ErroCampo (enum)
│   └── format/                     # Formatadores (datas, quantidade, CNPJ, "Vence em X dias")
├── data/
│   ├── local/
│   │   ├── db/                     # PratinDatabase, Converters
│   │   │   ├── entity/             # AlimentoEntity, ReservaEntity, relações
│   │   │   └── dao/                # AlimentoDao, ReservaDao
│   │   └── prefs/                  # PreferenciasDataStore (perfil + onboarding)
│   ├── mapper/                     # Entity ⇄ domain
│   ├── repository/                 # interfaces + implementações
│   └── seed/                       # SeedData, SeedInitializer
└── ui/
    ├── PratinApp.kt                # Scaffold raiz + NavHost + barra inferior
    ├── navigation/                 # Rotas.kt (@Serializable), PratinNavHost.kt, AbaPrincipal.kt
    ├── theme/                      # Color.kt, Theme.kt, Type.kt, Shape.kt, Dimens.kt
    ├── components/                 # componentes reutilizáveis (ver 04-ui.md §4)
    ├── util/                       # UiText, mapeamento ErroRegra/ErroCampo → @StringRes
    └── screens/
        ├── splash/  onboarding/  inicio/  detalhes/  novoanuncio/
        ├── confirmacao/  atividades/  perfil/  sobre/
        └── (cada pasta: XxxScreen.kt, XxxViewModel.kt, XxxUiState.kt)
```

Nomes: classes e arquivos em PascalCase, funções e variáveis em camelCase, **termos de domínio em português** (`Alimento`, `reservar`, `codigoRetirada`) e termos técnicos do Android em inglês (`Screen`, `ViewModel`, `Dao`, `Repository`, `Entity`, `UiState`).

## 4. Domínio (Kotlin puro)

### 4.1 Modelos

```kotlin
data class Alimento(
    val id: Long,
    val nome: String,
    val categoria: Categoria,
    val quantidade: Double,
    val unidade: Unidade,
    val dataValidade: LocalDate,
    val retiradaInicio: LocalDateTime,
    val retiradaFim: LocalDateTime,
    val endereco: String,
    val fotoUrl: String?,
    val status: StatusAlimento,
    val estabelecimentoCnpj: String,
    val estabelecimentoNome: String,
    val declaracaoProprio: Boolean,
    val favorito: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
) {
    fun janelaAberta(agora: LocalDateTime) = agora.isBefore(retiradaFim)
}

data class Reserva(
    val id: Long,
    val alimentoId: Long,
    val ongCnpj: String,
    val ongNome: String,
    val codigoRetirada: String,
    val status: StatusReserva,
    val criadaEm: LocalDateTime,
    val atualizadaEm: LocalDateTime,
)

data class Perfil(val tipo: TipoPerfil, val nome: String, val cnpj: String, val endereco: String) {
    val isOng get() = tipo == TipoPerfil.ONG
    val isEstabelecimento get() = tipo == TipoPerfil.ESTABELECIMENTO
}

data class ReservaDetalhada(val reserva: Reserva, val alimento: Alimento)          // Atividades (ONG), Confirmação
data class AnuncioDetalhado(val alimento: Alimento, val reservaAtiva: Reserva?)     // Atividades (Estab.)

/** Dados digitados no formulário (texto cru), validados por AnuncioValidator. */
data class NovoAnuncio(
    val nome: String = "", val categoria: Categoria? = null, val quantidadeTexto: String = "",
    val unidade: Unidade? = null, val dataValidade: LocalDate? = null,
    val retiradaInicio: LocalDateTime? = null, val retiradaFim: LocalDateTime? = null,
    val endereco: String = "", val fotoUrl: String = "", val declaracaoProprio: Boolean = false,
)
```

Enums (`StatusAlimento`, `StatusReserva`, `TipoPerfil`, `Categoria`, `Unidade`) ficam em `domain/model`. **Rótulos e cores não ficam no domínio**: a UI tem extensões como `Categoria.rotuloRes()`, `Categoria.icone()`, `StatusAlimento.cor()`.

### 4.2 Erros

```kotlin
enum class ErroRegra {
    ALIMENTO_NAO_ENCONTRADO, JA_RESERVADO, PRAZO_ENCERRADO, CODIGO_INVALIDO, CODIGO_TAMANHO,
    APENAS_ONGS, APENAS_ESTABELECIMENTOS, NAO_PODE_CANCELAR_ANUNCIO, NAO_PODE_CANCELAR_RESERVA,
    RETIRADA_INDISPONIVEL, RESERVA_NAO_ENCONTRADA, NAO_E_DONO, ANUNCIO_INVALIDO, PERFIL_NECESSARIO,
}
class RegraNegocioException(val erro: ErroRegra) : Exception(erro.name)

enum class ErroCampo { NOME_ALIMENTO, CATEGORIA, QUANTIDADE, UNIDADE, VALIDADE_OBRIGATORIA,
    VALIDADE_FORA_DO_PRAZO, JANELA_OBRIGATORIA, JANELA_NO_PASSADO, JANELA_CURTA,
    JANELA_APOS_VALIDADE, ENDERECO, FOTO_URL, DECLARACAO, TIPO_PERFIL, NOME_PERFIL, CNPJ }

/** Chaves dos mapas de erro devolvidos pelos validadores (um erro por campo, o primeiro que falhar). */
enum class CampoAnuncio { NOME, CATEGORIA, QUANTIDADE, UNIDADE, VALIDADE, JANELA, ENDERECO, FOTO_URL, DECLARACAO }
enum class CampoPerfil { TIPO, NOME, CNPJ, ENDERECO }
```

Ordem de checagem da janela (`CampoAnuncio.JANELA`), devolvendo o primeiro erro: obrigatória → no passado → curta → após a validade.

### 4.3 Validadores (funções puras, 100% testadas)

```kotlin
object AnuncioValidator {
    /** Mapa campo → erro. Vazio = válido. Cobre toda a RN03. */
    fun validar(form: NovoAnuncio, agora: LocalDateTime): Map<CampoAnuncio, ErroCampo>
    fun lerQuantidade(texto: String): Double?   // aceita "8,5" e "8.5"
}
object PerfilValidator { fun validar(tipo: TipoPerfil?, nome: String, cnpj: String, endereco: String): Map<CampoPerfil, ErroCampo> }
object CnpjValidator { fun apenasDigitos(v: String): String; fun isValido(v: String?): Boolean }
object CodigoGenerator {
    const val ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; const val TAMANHO = 6
    fun gerar(random: java.util.Random = SecureRandom()): String
    fun confere(digitado: String, esperado: String): Boolean
}
object RegrasStatus {
    fun podeReservar(a: Alimento, agora: LocalDateTime): Boolean
    fun podeCancelarAnuncio(a: Alimento): Boolean
    fun podeCancelarReserva(r: Reserva, a: Alimento, agora: LocalDateTime): Boolean
    fun podeConfirmarRetirada(a: Alimento, agora: LocalDateTime): Boolean
}
```

## 5. Dados

### 5.1 Room — schema (versão 1)

**Tabela `alimentos`** (`AlimentoEntity`)

| Coluna | Tipo Kotlin | Observação |
| --- | --- | --- |
| `id` | `Long` | `@PrimaryKey(autoGenerate = true)` |
| `nome` | `String` | |
| `categoria` | `Categoria` | converter → `TEXT` (nome do enum) |
| `quantidade` | `Double` | |
| `unidade` | `Unidade` | converter → `TEXT` |
| `dataValidade` | `LocalDate` | converter → `TEXT` `yyyy-MM-dd` |
| `retiradaInicio` / `retiradaFim` | `LocalDateTime` | converter → `TEXT` `yyyy-MM-dd'T'HH:mm:ss` |
| `endereco` | `String` | |
| `fotoUrl` | `String?` | |
| `status` | `StatusAlimento` | converter → `TEXT` |
| `estabelecimentoCnpj` / `estabelecimentoNome` | `String` | CNPJ só dígitos |
| `declaracaoProprio` | `Boolean` | |
| `favorito` | `Boolean` | default `false` |
| `criadoEm` / `atualizadoEm` | `LocalDateTime` | |

Índices: `status`, `estabelecimentoCnpj`.

**Tabela `reservas`** (`ReservaEntity`)

| Coluna | Tipo | Observação |
| --- | --- | --- |
| `id` | `Long` | PK autoincremento |
| `alimentoId` | `Long` | `ForeignKey` → `alimentos.id`, `onDelete = CASCADE`, indexado |
| `ongCnpj` / `ongNome` | `String` | `ongCnpj` indexado |
| `codigoRetirada` | `String` | |
| `status` | `StatusReserva` | |
| `criadaEm` / `atualizadaEm` | `LocalDateTime` | |

**Relações** (`@Embedded` + `@Relation`): `ReservaComAlimento` (reserva + seu alimento) e `AlimentoComReservas` (alimento + lista de reservas; a UI pega a `ATIVA`).

> ⚠️ **Armadilha das datas:** os converters **DEVEM** usar `DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")` (e truncar para segundos). `LocalDateTime.toString()` omite os segundos quando são zero, o que quebra a comparação de texto no SQL (`retiradaFim <= :agora`).

> **Schema exportado:** `exportSchema = true` com o plugin `androidx.room` (`room { schemaDirectory("$projectDir/schemas") }`). O JSON gerado é versionado no Git. Enquanto o app não for entregue, mudanças de schema PODEM usar `fallbackToDestructiveMigration(dropAllTables = true)` com aumento de versão (o seed recria os dados); registre no commit.

### 5.2 DAOs (assinaturas mínimas)

```kotlin
@Dao interface AlimentoDao {
    @Query("SELECT * FROM alimentos WHERE status = 'DISPONIVEL' ORDER BY dataValidade, retiradaFim")
    fun observarDisponiveis(): Flow<List<AlimentoEntity>>
    @Query("SELECT * FROM alimentos WHERE id = :id") fun observarPorId(id: Long): Flow<AlimentoEntity?>
    @Query("SELECT * FROM alimentos WHERE id = :id") suspend fun buscarPorId(id: Long): AlimentoEntity?
    @Transaction @Query("SELECT * FROM alimentos WHERE estabelecimentoCnpj = :cnpj")
    fun observarDoEstabelecimento(cnpj: String): Flow<List<AlimentoComReservas>>
    @Insert suspend fun inserir(alimento: AlimentoEntity): Long
    @Insert suspend fun inserirTodos(alimentos: List<AlimentoEntity>): List<Long>
    @Query("UPDATE alimentos SET status = :status, atualizadoEm = :agora WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: StatusAlimento, agora: LocalDateTime)
    @Query("UPDATE alimentos SET favorito = NOT favorito WHERE id = :id") suspend fun alternarFavorito(id: Long)
    @Query("""UPDATE alimentos SET status = 'EXPIRADO', atualizadoEm = :agora
              WHERE status IN ('DISPONIVEL','RESERVADO') AND retiradaFim <= :agora""")
    suspend fun expirarVencidos(agora: LocalDateTime): Int
    @Query("SELECT COUNT(*) FROM alimentos") suspend fun contar(): Int
}

@Dao interface ReservaDao {
    @Transaction @Query("SELECT * FROM reservas WHERE ongCnpj = :cnpj")
    fun observarDaOng(cnpj: String): Flow<List<ReservaComAlimento>>
    @Transaction @Query("SELECT * FROM reservas WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReservaComAlimento?>
    @Query("SELECT * FROM reservas WHERE id = :id") suspend fun buscarPorId(id: Long): ReservaEntity?
    @Query("SELECT * FROM reservas WHERE alimentoId = :alimentoId AND status = 'ATIVA' LIMIT 1")
    suspend fun buscarAtivaDoAlimento(alimentoId: Long): ReservaEntity?
    @Insert suspend fun inserir(reserva: ReservaEntity): Long
    @Query("UPDATE reservas SET status = :status, atualizadaEm = :agora WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: StatusReserva, agora: LocalDateTime)
    @Query("""UPDATE reservas SET status = 'EXPIRADA', atualizadaEm = :agora WHERE status = 'ATIVA'
              AND alimentoId IN (SELECT id FROM alimentos WHERE retiradaFim <= :agora)""")
    suspend fun expirarVencidas(agora: LocalDateTime): Int
}
```

### 5.3 Repositórios

```kotlin
interface AlimentoRepository {
    fun observarDisponiveis(): Flow<List<Alimento>>                 // RN05 (o VM ainda filtra janela aberta)
    fun observarPorId(id: Long): Flow<Alimento?>
    fun observarAnuncios(cnpj: String): Flow<List<AnuncioDetalhado>> // RF17, já ordenado
    suspend fun publicar(form: NovoAnuncio, perfil: Perfil): Long    // revalida RN03; RegraNegocioException
    suspend fun alternarFavorito(id: Long)
    suspend fun cancelarAnuncio(id: Long, perfil: Perfil)            // RN09
    suspend fun expirarVencidos()                                    // RN11, transação: reservas antes, alimentos depois
}
interface ReservaRepository {
    fun observarDaOng(cnpj: String): Flow<List<ReservaDetalhada>>   // RF15, já ordenado
    fun observarPorId(id: Long): Flow<ReservaDetalhada?>
    suspend fun reservar(alimentoId: Long, perfil: Perfil): Long     // RN06 em database.withTransaction
    suspend fun cancelar(reservaId: Long, perfil: Perfil)            // RN08
    suspend fun confirmarRetirada(alimentoId: Long, codigo: String, perfil: Perfil) // RN10
}
interface PerfilRepository { val perfil: Flow<Perfil?>; suspend fun salvar(perfil: Perfil) }
interface OnboardingRepository {
    val deveMostrar: Flow<Boolean>                                   // RN01 (true = vai ao Onboarding)
    suspend fun concluir(naoMostrarNovamente: Boolean)
    suspend fun reexibir()                                           // "Ver introdução novamente"
}
```

- Transações multi-tabela usam `database.withTransaction { … }` (`room-ktx`). **A validação de estado acontece dentro da transação**, lendo o registro de novo.
- O "agora" vem sempre de `LocalDateTime.now(clock)` com o `Clock` injetado (ADR-08).
- Implementações: `RoomAlimentoRepository`, `RoomReservaRepository`, `DataStorePerfilRepository`, `DataStoreOnboardingRepository`.

### 5.4 DataStore (`pratin_prefs`)

| Chave | Tipo | Conteúdo |
| --- | --- | --- |
| `onboarding_visto` | Boolean | `true` após "Começar" |
| `onboarding_nao_mostrar` | Boolean | valor do checkbox |
| `perfil_tipo` | String | `ESTABELECIMENTO` / `ONG` |
| `perfil_nome` / `perfil_cnpj` / `perfil_endereco` | String | CNPJ só dígitos |

Perfil existe só se as 4 chaves de perfil existirem. Uma única instância de DataStore (`preferencesDataStore` delegado no nível de arquivo).

### 5.5 AppContainer (DI manual)

```kotlin
interface AppContainer {
    val clock: Clock
    val alimentoRepository: AlimentoRepository
    val reservaRepository: ReservaRepository
    val perfilRepository: PerfilRepository
    val onboardingRepository: OnboardingRepository
    val seedInitializer: SeedInitializer
}
class DefaultAppContainer(context: Context) : AppContainer { /* lazy: Room.databaseBuilder(...).build(), DataStore, repos */ }
class PratinApplication : Application() { lateinit var container: AppContainer; override fun onCreate() { … } }
```

## 6. Apresentação (ViewModel + Compose)

### 6.1 Padrão de tela (obrigatório)

```kotlin
// XxxUiState.kt — imutável, tudo que a tela precisa para desenhar
data class InicioUiState(
    val carregando: Boolean = true,
    val alimentos: List<Alimento> = emptyList(),
    val busca: String = "",
    val categoria: Categoria? = null,
    val somenteFavoritos: Boolean = false,
    val perfil: Perfil? = null,
    val atualizando: Boolean = false,
    val mensagem: UiText? = null,          // Snackbar de uso único
)

// XxxViewModel.kt
class InicioViewModel(
    private val alimentos: AlimentoRepository,
    perfilRepository: PerfilRepository,
    private val clock: Clock,
) : ViewModel() {
    private val filtros = MutableStateFlow(Filtros())
    val uiState: StateFlow<InicioUiState> = combine(alimentos.observarDisponiveis(), perfilRepository.perfil, filtros) { … }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())
    fun onBuscaChange(texto: String) { filtros.update { it.copy(busca = texto) } }
    fun atualizar() { viewModelScope.launch { … alimentos.expirarVencidos() … } }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PratinApplication
                InicioViewModel(app.container.alimentoRepository, app.container.perfilRepository, app.container.clock)
            }
        }
    }
}

// XxxScreen.kt — Route (com estado) + Screen (sem estado, com @Preview)
@Composable
fun InicioRoute(onAbrirDetalhes: (Long) -> Unit, onAnunciar: () -> Unit,
                viewModel: InicioViewModel = viewModel(factory = InicioViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    InicioScreen(state = state, onBuscaChange = viewModel::onBuscaChange, /* … */)
}
@Composable
fun InicioScreen(state: InicioUiState, onBuscaChange: (String) -> Unit, /* … */) { /* só desenha */ }
```

Regras:
- ViewModels de tela com argumento usam `SavedStateHandle` (`createSavedStateHandle()` no initializer) e `savedStateHandle.toRoute<DetalhesRoute>().alimentoId`.
- Estado de formulário (Novo anúncio, Perfil) fica no ViewModel (sobrevive à rotação, RF12-CA5).
- Mensagens de uso único: campo `mensagem: UiText?` no estado + `fun mensagemExibida()`; a tela mostra no `SnackbarHostState` via `LaunchedEffect(state.mensagem)`.
- Ações que navegam após sucesso (publicar, reservar) expõem um campo de estado (ex.: `reservaCriadaId: Long?`) consumido pela Route com `LaunchedEffect`, que chama o callback de navegação e avisa o ViewModel.
- `UiText`: `sealed interface UiText { data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()); data class Dinamico(val texto: String) }` com `@Composable fun UiText.asString()`.
- ON_RESUME de Início e Atividades: `LifecycleResumeEffect(Unit) { viewModel.atualizar(); onPauseOrDispose { } }`.

### 6.2 Navegação (ADR-05)

```kotlin
@Serializable object SplashRoute
@Serializable object OnboardingRoute
@Serializable object MainGraph                       // grafo aninhado das abas
@Serializable object InicioRoute
@Serializable object AtividadesRoute
@Serializable object PerfilRoute
@Serializable data class DetalhesRoute(val alimentoId: Long)
@Serializable object NovoAnuncioRoute
@Serializable data class ConfirmacaoReservaRoute(val reservaId: Long)
@Serializable object EditarPerfilRoute               // formulário em tela cheia (aberto pelo diálogo "Perfil necessário")
@Serializable object SobreRoute
```

- `NavHost(startDestination = SplashRoute)`. As 3 abas ficam em `navigation<MainGraph>(startDestination = InicioRoute)`.
- Splash → `navigate(OnboardingRoute or MainGraph) { popUpTo<SplashRoute> { inclusive = true } }`.
- Onboarding → `navigate(MainGraph) { popUpTo<OnboardingRoute> { inclusive = true } }`.
- Troca de aba: `navigate(aba) { popUpTo<InicioRoute> { saveState = true }; launchSingleTop = true; restoreState = true }`.
- Reservar → `navigate(ConfirmacaoReservaRoute(id)) { popUpTo<DetalhesRoute> { inclusive = true } }`.
- Confirmação → aba: `navigate(AtividadesRoute ou InicioRoute) { popUpTo<InicioRoute> { inclusive = false }; launchSingleTop = true }`.
- "Ver introdução novamente" (Perfil) → `reexibir()` e `navigate(OnboardingRoute) { popUpTo<MainGraph> { inclusive = true } }`.
- A barra inferior aparece só quando `currentDestination?.hierarchy` contém uma das 3 abas (`hasRoute(InicioRoute::class)` …).
- Destinos internos recebem **somente IDs** e callbacks (`onVoltar`, `onAbrirDetalhes(id)`); nunca o `NavController`.

## 7. Splash e inicialização

1. `MainActivity.onCreate`: `installSplashScreen()` antes de `super.onCreate`, `enableEdgeToEdge()`, `setContent { PratinTheme { PratinApp() } }`.
2. `Theme.Pratin.Starting` (`Theme.SplashScreen`): `windowSplashScreenBackground = @color/verde_primario`, ícone = logo, `postSplashScreenTheme = @style/Theme.Pratin`.
3. `SplashViewModel` em paralelo: `seedInitializer.executarSeNecessario()`, `alimentoRepository.expirarVencidos()`, lê `onboardingRepository.deveMostrar`, aguarda o mínimo de ~1,2 s e publica `destino: DestinoSplash?` (`ONBOARDING` ou `INICIO`).
4. `SplashScreen` (Compose) usa o mesmo fundo verde → transição sem "piscar".

## 8. Seed (RN16)

`SeedInitializer.executarSeNecessario()`: dentro de `withTransaction`, se `alimentoDao.contar() == 0`, insere os registros abaixo. Datas relativas a `hoje = LocalDate.now(clock)`: validade = `hoje + diasValidade`; retirada no dia `hoje + diaRetirada`, das `horaInicio` às `horaFim`.

**Estabelecimentos e ONG de demonstração** (CNPJs fictícios, válidos pela RN02 — os mesmos do Flutter):

| Perfil | Nome | CNPJ | Endereço |
| --- | --- | --- | --- |
| Estabelecimento | Padaria Pão Quente | 11222333000181 | Rua das Flores, 120 – São Paulo/SP |
| Estabelecimento | Mercado Bom Preço | 11444777000161 | Av. Paulista, 900 – São Paulo/SP |
| Estabelecimento | Restaurante Sabor Caseiro | 98765432000198 | Rua Augusta, 55 – São Paulo/SP |
| Estabelecimento | Supermercado Economia | 33444555000181 | Rua Voluntários da Pátria, 300 – São Paulo/SP |
| Estabelecimento | Mercearia São José | 55666777000181 | Rua Domingos de Morais, 1500 – São Paulo/SP |
| ONG | ONG Mesa Cheia | 12345678000195 | Rua da Esperança, 45 – São Paulo/SP |

**Anúncios** (foto: `https://images.unsplash.com/photo-<id>?w=800`; "—" = sem foto, mostra o ícone):

| Nome | Categoria | Qtd | Unidade | Loja | Validade (+d) | Retirada (+d, h) | Foto id |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Arroz branco tipo 1 – 5 kg | MERCEARIA | 10 | PACOTE | Mercado | 6 | +2, 9–17 | 1586201375761-83865001e31c |
| Feijão carioca 1 kg | MERCEARIA | 15 | PACOTE | Mercearia | 5 | +1, 8–12 | — |
| Farinha de trigo 1 kg | MERCEARIA | 20 | PACOTE | Mercearia | 4 | +2, 14–18 | 1610725664285-7c57e6eeac3f |
| Macarrão espaguete 500 g | MERCEARIA | 30 | PACOTE | Mercado | 7 | +4, 10–16 | 1551462147-ff29053bfc14 |
| Café torrado e moído 500 g | MERCEARIA | 10 | PACOTE | Mercado | 4 | +3, 8–11 | 1559056199-641a0ac8b55e |
| Biscoito de chocolate 130 g | MERCEARIA | 25 | PACOTE | Supermercado | 3 | +2, 10–14 | 1558961363-fa8fdf82db35 |
| Kit enlatados: milho e ervilha | MERCEARIA | 24 | UNIDADE | Supermercado | 4 | +1, 10–15 | 1584263347416-85a696b4eda7 |
| Sardinha em lata 125 g | MERCEARIA | 40 | UNIDADE | Mercado | 7 | +5, 14–18 | — |
| Pão de forma tradicional | PAES | 12 | PACOTE | Padaria | 2 | +1, 16–19 | — |
| Pão de queijo | PAES | 20 | UNIDADE | Padaria | 2 | +1, 19–21 | — |
| Bolo de fubá | PAES | 3 | UNIDADE | Padaria | 3 | +2, 15–18 | — |
| Banana prata | HORTIFRUTI | 8,5 | KG | Supermercado | 4 | +2, 14–18 | — |
| Iogurte natural 170 g | LATICINIOS | 24 | UNIDADE | Supermercado | 3 | +1, 9–13 | — |
| Marmita de arroz, feijão e frango | REFEICOES | 15 | PORCAO | Restaurante | 1 | +1, 11–14 | — |

**Histórico para a demonstração de Atividades:**

| Alimento | Status do alimento | Reserva | Observação |
| --- | --- | --- | --- |
| Marmita de arroz, feijão e frango | `RESERVADO` | `ATIVA`, ONG Mesa Cheia, código **`PX7K2Q`** | Permite demonstrar "Confirmar retirada" logo no 1º uso, com o perfil Restaurante Sabor Caseiro. |
| Pão francês (40 UNIDADE, PAES, Padaria, validade hoje, retirada ontem 16–19) | `RETIRADO` | `CONCLUIDA`, ONG Mesa Cheia, código `K3M9TR` | Mostra um item concluído no histórico. |

Todos os demais nascem `DISPONIVEL`, `declaracaoProprio = true`, `favorito = false`, `criadoEm = agora - (n) horas` (para a ordenação ficar estável).

> Se a demonstração for feita tarde da noite, alguns itens com retirada "+1" continuam válidos; itens com retirada "hoje" não existem no seed justamente para não expirarem durante a gravação.

## 9. Convenções de código

- Kotlin oficial (`kotlin.code.style=official`); formatação do Android Studio (Ctrl+Alt+L) antes de cada commit.
- Funções Composable em PascalCase; um Composable público por arquivo de tela (Route + Screen); componentes pequenos podem dividir arquivo.
- `@Preview` para cada `Screen` sem estado (com dados falsos), em tema claro.
- KDoc curto em classes de regra e repositórios, citando o ID (`/** Reserva o lote inteiro (RN06). */`).
- Sem código comentado "morto" nem `TODO` no commit final.
- `Modifier` como primeiro parâmetro opcional em componentes reutilizáveis (`modifier: Modifier = Modifier`).
