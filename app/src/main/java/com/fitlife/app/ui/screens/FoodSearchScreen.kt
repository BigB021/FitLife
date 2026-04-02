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
import com.fitlife.app.ui.components.BarcodeInputField
import com.fitlife.app.ui.components.FoodResultCard
import com.fitlife.app.ui.components.NameSearchField
import com.fitlife.app.ui.components.SearchModeToggle
import com.fitlife.app.viewModel.FoodViewModel
import com.fitlife.app.viewModel.MealViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    foodViewModel: FoodViewModel,
    mealViewModel: MealViewModel,
    targetMealId: Int,
    targetDate: String,
    onDismiss: () -> Unit
) {
    val searchResults by foodViewModel.searchResults.observeAsState(emptyList())
    val barcodeResult by foodViewModel.barcodeResult.observeAsState()
    val isLoading by foodViewModel.isLoading.observeAsState(false)

    var query by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(SearchMode.NAME) }
    var barcodeInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }

    // Consolidate results from both sources
    val displayResults = remember(searchResults, barcodeResult) {
        barcodeResult?.let { listOf(it) } ?: searchResults
    }

    // Snackbar on barcode not found
    val searchError by foodViewModel.searchError.observeAsState()

    LaunchedEffect(searchError) {
        searchError?.let {
            snackbarHostState.showSnackbar("Search failed: $it")
            println("Search failed: $it")
            foodViewModel.clearError()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Food", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {
                        foodViewModel.clearSearch()
                        onDismiss()
                    }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Search Mode Toggle
            SearchModeToggle(
                selected = searchMode,
                onSelect = {
                    searchMode = it
                    query = ""
                    barcodeInput = ""
                    foodViewModel.clearSearch()
                }
            )

            // Search Input
            AnimatedContent(targetState = searchMode, label = "search_input") { mode ->
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
                        focusRequester = searchFocusRequester
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
                displayResults.isEmpty() && query.isNotBlank() -> EmptyResultsHint(mode = searchMode)
                displayResults.isNotEmpty() -> {
                    Text(
                        text = "${displayResults.size} result${if (displayResults.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(displayResults) { food ->
                            FoodResultCard(
                                food = food,
                                onLog = { quantity ->
                                    val entry = food.copy(
                                        mealId = targetMealId,
                                        quantity = quantity
                                    )
                                    mealViewModel.addFood(entry)
                                    mealViewModel.updateDailyMacros(targetDate)
                                    snackbarHostState.showSnackbar("${food.foodName} logged!")
                                    foodViewModel.clearSearch()
                                    onDismiss()
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