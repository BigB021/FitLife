package com.fitlife.app.ui.screens


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitlife.app.domain.model.Exercise
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.utils.todayAsString
import com.fitlife.app.viewModel.ExerciseFilter
import com.fitlife.app.viewModel.ExerciseViewModel
import com.fitlife.app.viewModel.WorkoutViewModel

// ── Muscle group options ───────────────────────────────────────────────────────
val MUSCLE_GROUPS = listOf(
    "abdominals","abductors","adductors","biceps","calves",
    "chest","forearms","glutes","hamstrings","lats",
    "lower_back","middle_back","neck","quadriceps","traps","triceps"
)

val EXERCISE_TYPES = listOf(
    "cardio","olympic_weightlifting","plyometrics",
    "powerlifting","strength","stretching","strongman"
)

enum class WorkoutStep { SELECT_MUSCLE, SEARCH_EXERCISES, LOG_EXERCISE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLoggingScreen(
    exerciseViewModel: ExerciseViewModel,
    workoutViewModel: WorkoutViewModel,
    targetDate: String = todayAsString(),
    onDismiss: () -> Unit
) {
    val activeSession     by workoutViewModel.activeSession.observeAsState()
    val sessionExercises  by workoutViewModel.sessionExercises.observeAsState(emptyList())
    val localExercises    by exerciseViewModel.localExercises.observeAsState(emptyList())
    val apiResults        by exerciseViewModel.apiResults.observeAsState(emptyList())
    val isLoading         by exerciseViewModel.isLoading.observeAsState(false)
    val error             by exerciseViewModel.error.observeAsState()
    val workoutError      by workoutViewModel.error.observeAsState()
    val savedResult       by exerciseViewModel.savedResult.observeAsState()

    var step by remember { mutableStateOf(WorkoutStep.SELECT_MUSCLE) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var searchTab by remember { mutableStateOf(0) } // 0=Local 1=API
    var query by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { exerciseViewModel.loadLocalExercises() }

    LaunchedEffect(error) {
        error?.let { snackbarHostState.showSnackbar(it); exerciseViewModel.clearError() }
    }

    LaunchedEffect(savedResult) {
        savedResult?.let {
            snackbarHostState.showSnackbar("${it.name} saved to your library!")
            exerciseViewModel.clearSavedResult()
            exerciseViewModel.loadLocalExercises()
        }
    }

    // Once session created, advance to search step
    LaunchedEffect(activeSession) {
        if (activeSession != null && step == WorkoutStep.SELECT_MUSCLE) {
            step = WorkoutStep.SEARCH_EXERCISES
        }
    }

    LaunchedEffect(error) {
        error?.let { snackbarHostState.showSnackbar(it); exerciseViewModel.clearError() }
    }
    LaunchedEffect(workoutError) {
        workoutError?.let { snackbarHostState.showSnackbar(it); workoutViewModel.clearError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (step) {
                                WorkoutStep.SELECT_MUSCLE    -> "New Workout"
                                WorkoutStep.SEARCH_EXERCISES -> activeSession?.muscleGroup
                                    ?.replaceFirstChar { it.uppercase() } ?: "Add Exercises"
                                WorkoutStep.LOG_EXERCISE     -> selectedExercise?.name ?: "Log Exercise"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                        if (step != WorkoutStep.SELECT_MUSCLE) {
                            Text(
                                "${sessionExercises.size} exercise${if (sessionExercises.size != 1) "s" else ""} logged",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (step) {
                            WorkoutStep.SELECT_MUSCLE -> {
                                workoutViewModel.discardSession()
                                onDismiss()
                            }
                            WorkoutStep.SEARCH_EXERCISES -> {
                                if (sessionExercises.isEmpty()) {
                                    workoutViewModel.discardSession()
                                    onDismiss()
                                } else {
                                    onDismiss() // keep session with exercises
                                }
                            }
                            WorkoutStep.LOG_EXERCISE -> {
                                selectedExercise = null
                                step = WorkoutStep.SEARCH_EXERCISES
                            }
                        }
                    }) {
                        Icon(
                            if (step == WorkoutStep.LOG_EXERCISE) Icons.Outlined.ArrowBack
                            else Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (step == WorkoutStep.SEARCH_EXERCISES && sessionExercises.isNotEmpty()) {
                        TextButton(onClick = {
                            workoutViewModel.finishSession()
                            onDismiss()
                        }) {
                            Text("Done", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = step,
            label       = "workout_step",
            transitionSpec = { slideInHorizontally { it } togetherWith slideOutHorizontally { -it } }
        ) { currentStep ->
            when (currentStep) {

                // ── Step 1: Pick muscle group ──────────────────────────────
                WorkoutStep.SELECT_MUSCLE -> {
                    MuscleGroupSelector(
                        modifier = Modifier.padding(paddingValues),
                        onSelected = { muscle ->
                            workoutViewModel.startSession(muscle, targetDate)
                            selectedMuscle = muscle
                        }
                    )
                }

                // ── Step 2: Search + add exercises ─────────────────────────
                WorkoutStep.SEARCH_EXERCISES -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Session summary
                        if (sessionExercises.isNotEmpty()) {
                            SessionSummaryBanner(
                                exercises = sessionExercises,
                                onRemove  = { workoutViewModel.removeExerciseFromSession(it) }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        // Local / API tabs
                        TabRow(selectedTabIndex = searchTab) {
                            Tab(selected = searchTab == 0, onClick = {
                                searchTab = 0
                                exerciseViewModel.loadLocalExercises()
                            }) { Text("My Library", modifier = Modifier.padding(vertical = 12.dp)) }
                            Tab(selected = searchTab == 1, onClick = {
                                searchTab = 1
                                exerciseViewModel.clearApiResults()
                            }) { Text("Discover", modifier = Modifier.padding(vertical = 12.dp)) }
                        }

                        // Search bar
                        ExerciseSearchBar(
                            query           = query,
                            onQueryChange   = { query = it },
                            onSearch        = {
                                focusManager.clearFocus()
                                val filter = ExerciseFilter(
                                    query  = query,
                                    muscle = selectedMuscle,
                                    type   = selectedType
                                )
                                if (searchTab == 0) exerciseViewModel.searchLocal(filter)
                                else exerciseViewModel.searchApi(filter)
                            },
                            isLoading       = isLoading,
                            focusRequester  = focusRequester
                        )

                        // Muscle filter chips
                        MuscleFilterChips(
                            selected   = selectedMuscle,
                            onSelected = { m ->
                                selectedMuscle = if (selectedMuscle == m) "" else m
                                val filter = ExerciseFilter(query = query, muscle = selectedMuscle)
                                if (searchTab == 0) exerciseViewModel.searchLocal(filter)
                                else exerciseViewModel.searchApi(filter)
                            }
                        )

                        // Results
                        val displayList = if (searchTab == 0) localExercises else apiResults
                        when {
                            isLoading -> LoadingIndicator()
                            displayList.isEmpty() && searchTab == 1 ->
                                SearchHint()
                            displayList.isEmpty() ->
                                EmptyLibraryHint()
                            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(displayList, key = { it.name }) { exercise ->
                                    ExerciseCard(
                                        exercise    = exercise,
                                        isFromApi   = searchTab == 1,
                                        onLog       = {
                                            selectedExercise = exercise
                                            step = WorkoutStep.LOG_EXERCISE
                                        },
                                        onSaveToLib = {
                                            exerciseViewModel.saveExerciseToLibrary(exercise)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Step 3: Log sets/reps/weight ───────────────────────────
                WorkoutStep.LOG_EXERCISE -> {
                    selectedExercise?.let { exercise ->
                        LogExerciseForm(
                            exercise  = exercise,
                            modifier  = Modifier.padding(paddingValues),
                            onLog     = { sets, reps, weight, notes ->
                                workoutViewModel.logExercise(
                                    exerciseId = exercise.id,
                                    sets       = sets,
                                    reps       = reps,
                                    weight     = weight,
                                    notes      = notes
                                )
                                step = WorkoutStep.SEARCH_EXERCISES
                                selectedExercise = null
                            }
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REUSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun MuscleGroupSelector(modifier: Modifier = Modifier, onSelected: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Target muscle group",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MUSCLE_GROUPS) { muscle ->
                Card(
                    onClick = { onSelected(muscle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            muscle.replace("_", " ").replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean,
    focusRequester: FocusRequester
) {
    OutlinedTextField(
        value           = query,
        onValueChange   = onQueryChange,
        modifier        = Modifier.fillMaxWidth().focusRequester(focusRequester),
        placeholder     = { Text("Search exercises...") },
        leadingIcon     = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon    = {
            if (query.isNotBlank()) {
                IconButton(onClick = onSearch, enabled = !isLoading) {
                    Icon(Icons.Outlined.ArrowForward, contentDescription = "Search")
                }
            }
        },
        singleLine      = true,
        shape           = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

@Composable
fun MuscleFilterChips(selected: String, onSelected: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(MUSCLE_GROUPS) { muscle ->
            FilterChip(
                selected = selected == muscle,
                onClick  = { onSelected(muscle) },
                label    = {
                    Text(muscle.replace("_", " ").replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall)
                }
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    isFromApi: Boolean,
    onLog: () -> Unit,
    onSaveToLib: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.name, style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ExerciseTag(exercise.muscle.replace("_"," "), MaterialTheme.colorScheme.primary)
                        ExerciseTag(exercise.difficulty, MaterialTheme.colorScheme.tertiary)
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            // Equipment chips
            if (exercise.equipmentList().isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(exercise.equipmentList()) { eq ->
                        ExerciseTag(eq, MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            // Instructions
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        exercise.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (exercise.safetyInfo.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Warning, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp))
                            Text(exercise.safetyInfo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isFromApi) {
                    OutlinedButton(
                        onClick = onSaveToLib,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.BookmarkAdd, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Save", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Button(
                    onClick = onLog,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Log", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun ExerciseTag(label: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun SessionSummaryBanner(
    exercises: List<WorkoutExercise>,
    onRemove: (WorkoutExercise) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Text("${exercises.size} exercise${if (exercises.size != 1) "s" else ""} logged",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                }
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(if (expanded) "Hide" else "Review",
                        style = MaterialTheme.typography.labelMedium)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    exercises.forEach { we ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${we.sets}×${we.reps}  ${we.weight}kg",
                                style = MaterialTheme.typography.bodySmall)
                            IconButton(onClick = { onRemove(we) },
                                modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Outlined.Close, contentDescription = "Remove",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogExerciseForm(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onLog: (sets: Int, reps: Int, weight: Float, notes: String) -> Unit
) {
    var sets   by remember { mutableStateOf("3") }
    var reps   by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("") }
    var notes  by remember { mutableStateOf("") }

    val isValid = sets.toIntOrNull() != null && reps.toIntOrNull() != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Exercise info header
        Card(
            shape  = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExerciseTag(exercise.muscle.replace("_"," "), MaterialTheme.colorScheme.primary)
                    ExerciseTag(exercise.type, MaterialTheme.colorScheme.secondary)
                    ExerciseTag(exercise.difficulty, MaterialTheme.colorScheme.tertiary)
                }
            }
        }

        Text("Log your set", style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value         = sets,
                onValueChange = { sets = it },
                modifier      = Modifier.weight(1f),
                label         = { Text("Sets") },
                singleLine    = true,
                shape         = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value         = reps,
                onValueChange = { reps = it },
                modifier      = Modifier.weight(1f),
                label         = { Text("Reps") },
                singleLine    = true,
                shape         = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value         = weight,
                onValueChange = { weight = it },
                modifier      = Modifier.weight(1f),
                label         = { Text("Weight") },
                suffix        = { Text("kg") },
                singleLine    = true,
                shape         = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        OutlinedTextField(
            value         = notes,
            onValueChange = { notes = it },
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Notes (optional)") },
            placeholder   = { Text("e.g. felt strong, increase next time") },
            shape         = RoundedCornerShape(10.dp),
            maxLines      = 3
        )

        Button(
            onClick  = {
                onLog(
                    sets.toIntOrNull() ?: 0,
                    reps.toIntOrNull() ?: 0,
                    weight.toFloatOrNull() ?: 0f,
                    notes
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled  = isValid,
            shape    = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Log Exercise", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun EmptyLibraryHint() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.FitnessCenter, contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outlineVariant)
            Text("Your library is empty",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant)
            Text("Search the Discover tab to find and save exercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}