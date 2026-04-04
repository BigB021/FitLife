package com.fitlife.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlife.app.viewModel.FoodViewModel
import com.fitlife.app.viewModel.MealViewModel
import com.fitlife.app.ui.components.*
import com.fitlife.app.utils.todayAsString


enum class LoggingStep { SELECT_TYPE, ADD_FOOD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealLoggingScreen(
    foodViewModel: FoodViewModel,
    mealViewModel: MealViewModel,
    targetDate: String = todayAsString(),
    onDismiss: () -> Unit
) {
    val activeMealId by mealViewModel.activeMealId.observeAsState()
    val currentMealWithFood by mealViewModel.currentMealWithFood.observeAsState()
    val searchResults by foodViewModel.searchResults.observeAsState(emptyList())
    val barcodeResult by foodViewModel.barcodeResult.observeAsState()
    val isLoading by foodViewModel.isLoading.observeAsState(false)
    val searchError by foodViewModel.searchError.observeAsState()

    var step by remember { mutableStateOf(LoggingStep.SELECT_TYPE) }
    var query by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(SearchMode.NAME) }
    var barcodeInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val displayResults = remember(searchResults, barcodeResult) {
        barcodeResult?.let { listOf(it) } ?: searchResults
    }

    // Show search errors
    LaunchedEffect(searchError) {
        searchError?.let {
            snackbarHostState.showSnackbar(it)
            foodViewModel.clearError()
        }
    }

    // Once meal is created, load its current state and move to food step
    LaunchedEffect(activeMealId) {
        activeMealId?.let {
            mealViewModel.loadCurrentMeal(it)
            step = LoggingStep.ADD_FOOD
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (step) {
                                LoggingStep.SELECT_TYPE -> "Log Meal"
                                LoggingStep.ADD_FOOD    -> currentMealWithFood?.meal?.type
                                    ?.replaceFirstChar { it.uppercase() } ?: "Add Food"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                        if (step == LoggingStep.ADD_FOOD) {
                            Text(
                                text = "${currentMealWithFood?.foods?.size ?: 0} items · " +
                                        "${currentMealWithFood?.foods?.sumOf { (it.calories * it.quantity / 100).toDouble() }?.toInt() ?: 0} kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == LoggingStep.ADD_FOOD) {
                            // Go back to type selection, delete meal if empty
                            activeMealId?.let { id ->
                                if (currentMealWithFood?.foods.isNullOrEmpty()) {
                                    mealViewModel.deleteMealIfEmpty(id)
                                }
                            }
                            mealViewModel.clearActiveMeal()
                            foodViewModel.clearSearch()
                            step = LoggingStep.SELECT_TYPE
                        } else {
                            onDismiss()
                        }
                    }) {
                        Icon(
                            if (step == LoggingStep.ADD_FOOD) Icons.Outlined.ArrowBack
                            else Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (step == LoggingStep.ADD_FOOD && currentMealWithFood?.foods?.isNotEmpty() == true) {
                        TextButton(onClick = {
                            foodViewModel.clearSearch()
                            mealViewModel.clearActiveMeal()
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
            label = "logging_step",
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            }
        ) { currentStep ->
            when (currentStep) {

                // Step 1: Pick meal type
                LoggingStep.SELECT_TYPE -> {
                    MealTypeSelector(
                        modifier = Modifier.padding(paddingValues),
                        onMealTypeSelected = { type ->
                            mealViewModel.getOrCreateMeal(type, targetDate)
                        }
                    )
                }

                // Step 2: Search and add foods
                LoggingStep.ADD_FOOD -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Current meal food summary
                        currentMealWithFood?.let { mwf ->
                            if (mwf.foods.isNotEmpty()) {
                                CurrentMealSummary(
                                    foods = mwf.foods,
                                    onRemoveFood = { food ->
                                        mealViewModel.deleteFood(food)
                                        activeMealId?.let { mealViewModel.loadCurrentMeal(it) }
                                    }
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Search toggle
                        SearchModeToggle(
                            selected = searchMode,
                            onSelect = {
                                searchMode = it
                                query = ""
                                barcodeInput = ""
                                foodViewModel.clearSearch()
                            }
                        )

                        // Search input
                        AnimatedContent(targetState = searchMode, label = "input_mode") { mode ->
                            when (mode) {
                                SearchMode.NAME -> NameSearchField(
                                    query = query,
                                    onQueryChange = { query = it },
                                    onSearch = {
                                        if (query.isNotBlank()) {
                                            focusManager.clearFocus()
                                            foodViewModel.searchByName(query)
                                        }
                                    },
                                    isLoading = isLoading,
                                    focusRequester = focusRequester
                                )
                                SearchMode.BARCODE -> BarcodeInputField(
                                    value = barcodeInput,
                                    onValueChange = { barcodeInput = it },
                                    onSearch = {
                                        if (barcodeInput.isNotBlank()) {
                                            focusManager.clearFocus()
                                            foodViewModel.searchByBarcode(barcodeInput)
                                        }
                                    },
                                    isLoading = isLoading
                                )
                            }
                        }

                        // Results
                        when {
                            isLoading -> LoadingIndicator()
                            displayResults.isEmpty() && query.isNotBlank() ->
                                EmptyResultsHint(mode = searchMode)
                            displayResults.isNotEmpty() -> {
                                Text(
                                    "${displayResults.size} result${if (displayResults.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(displayResults) { food ->
                                        FoodResultCard(
                                            food = food,
                                            onLog = { quantity ->
                                                activeMealId?.let { mealId ->
                                                    val entry = food.copy(
                                                        mealId   = mealId,
                                                        quantity = quantity
                                                    )
                                                    mealViewModel.addFood(entry)
                                                    // Refresh current meal view
                                                    mealViewModel.loadCurrentMeal(mealId)
                                                    // Clear search so user can search next item
                                                    foodViewModel.clearSearch()
                                                    query = ""
                                                    barcodeInput = ""
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            else -> SearchHint()
                        }
                    }
                }
            }
        }
    }
}
// REUSABLE COMPONENTS
enum class SearchMode { NAME, BARCODE }

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Text("Searching...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EmptyResultsHint(mode: SearchMode) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (mode == SearchMode.NAME) "No foods found. Try a different term."
                else "No product found for this barcode.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SearchHint() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                Icons.Outlined.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                "Search for a food to log it",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}