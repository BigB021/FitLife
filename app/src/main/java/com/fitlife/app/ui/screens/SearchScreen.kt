package com.fitlife.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.viewModel.FoodViewModel

@Composable
fun SearchScreen(viewModel: FoodViewModel) {

    val barcodeQuery = remember { mutableStateOf("") }
    val searchQuery = remember { mutableStateOf("") }

    val results by viewModel.searchResults.observeAsState(emptyList())
    val barcodeResult by viewModel.barcodeResult.observeAsState()
    barcodeResult?.let {
        FoodItemCard(it)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Food API Test",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // 🔹 Barcode Search
        SearchField(
            label = "Search by Barcode",
            value = barcodeQuery.value,
            onValueChange = { barcodeQuery.value = it },
            onSearch = {
                viewModel.searchByBarcode(barcodeQuery.value)
            }
        )

        // 🔹 Keyword Search
        SearchField(
            label = "Search by Name",
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            onSearch = {
                viewModel.searchByName(searchQuery.value)
            }
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "Results",
            style = MaterialTheme.typography.titleMedium
        )

        results.forEach {
            FoodItemCard(it)
        }
    }
}


@Composable
fun SearchField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column {
        Text(text = label)

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = onSearch,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("Search")
        }
    }
}

@Composable
fun FoodItemCard(food: FoodEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = food.foodName, fontWeight = FontWeight.Bold)

            Text("Calories: ${food.calories}")
            Text("Protein: ${food.protein}")
            Text("Carbs: ${food.carbs}")
            Text("Fat: ${food.fat}")
        }
    }
}
