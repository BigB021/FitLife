package com.fitlife.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fitlife.app.viewModel.UserFormState
import com.fitlife.app.ui.components.SectionHeader
import com.fitlife.app.ui.components.DropdownField
import com.fitlife.app.viewModel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(viewModel: UserViewModel) {
    val formState = viewModel.formState.observeAsState(UserFormState())
    val saveResult = viewModel.saveResult.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Validation error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }

    // React to save result
    LaunchedEffect(saveResult.value) {
        saveResult.value?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                snackbarHostState.showSnackbar(
                    message = "Profile saved successfully!",
                    duration = SnackbarDuration.Short
                )
            } else {
                snackbarHostState.showSnackbar(
                    message = result.exceptionOrNull()?.message ?: "Failed to save. Please try again.",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    fun validate(): Boolean {
        var valid = true
        nameError = if (formState.value.name.isBlank()) { valid = false; "Name is required" } else null
        ageError = when {
            formState.value.age.isBlank() -> { valid = false; "Age is required" }
            formState.value.age.toIntOrNull() == null -> { valid = false; "Enter a valid number" }
            formState.value.age.toInt() !in 10..120 -> { valid = false; "Age must be between 10 and 120" }
            else -> null
        }
        heightError = when {
            formState.value.height.isBlank() -> { valid = false; "Height is required" }
            formState.value.height.toFloatOrNull() == null -> { valid = false; "Enter a valid number" }
            else -> null
        }
        weightError = when {
            formState.value.weight.isBlank() -> { valid = false; "Weight is required" }
            formState.value.weight.toFloatOrNull() == null -> { valid = false; "Enter a valid number" }
            else -> null
        }
        return valid
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = data.visuals.message.startsWith("Failed") || data.visuals.message.startsWith("Error")
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Create Your Profile",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Help us personalize your fitness journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Section: Personal Info ──────────────────────────────────────
            SectionHeader(icon = Icons.Outlined.Person, title = "Personal Info")

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.value.name,
                onValueChange = {
                    viewModel.onNameChange(it)
                    nameError = null
                },
                label = { Text("Full Name") },
                placeholder = { Text("eg. Ali Jabali") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                isError = nameError != null,
                supportingText = { if (nameError != null) Text(nameError!!) },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.value.age,
                    onValueChange = {
                        viewModel.onAgeChange(it)
                        ageError = null
                    },
                    label = { Text("Age") },
                    placeholder = { Text("22") },
                    leadingIcon = { Icon(Icons.Outlined.Cake, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ageError != null,
                    supportingText = { if (ageError != null) Text(ageError!!) },
                    singleLine = true
                )

                DropdownField(
                    modifier = Modifier.weight(1f),
                    label = "Gender",
                    options = listOf("Male", "Female", "Other"),
                    selected = formState.value.gender,
                    onSelected = { viewModel.onGenderChange(it) },
                    leadingIcon = Icons.Outlined.Wc
                )
            }

            // ── Section: Body Measurements ──────────────────────────────────
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(icon = Icons.Outlined.Straighten, title = "Body Measurements")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.value.height,
                    onValueChange = {
                        viewModel.onHeightChange(it)
                        heightError = null
                    },
                    label = { Text("Height") },
                    placeholder = { Text("180") },
                    suffix = { Text("cm") },
                    leadingIcon = { Icon(Icons.Outlined.Height, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = heightError != null,
                    supportingText = { if (heightError != null) Text(heightError!!) },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.value.weight,
                    onValueChange = {
                        viewModel.onWeightChange(it)
                        weightError = null
                    },
                    label = { Text("Weight") },
                    placeholder = { Text("75") },
                    suffix = { Text("kg") },
                    leadingIcon = { Icon(Icons.Outlined.MonitorWeight, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = weightError != null,
                    supportingText = { if (weightError != null) Text(weightError!!) },
                    singleLine = true
                )
            }

            // ── Section: Fitness Goals ──────────────────────────────────────
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(icon = Icons.Outlined.FitnessCenter, title = "Fitness Goals")

            DropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Activity Level",
                options = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extra Active"),
                selected = formState.value.activityLevel,
                onSelected = { viewModel.onActivityLevelChange(it) },
                leadingIcon = Icons.Outlined.DirectionsRun
            )

            DropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Goal",
                options = listOf("Lose Weight", "Maintain Weight", "Gain Weight"),
                selected = formState.value.goalType,
                onSelected = { viewModel.onGoalChange(it) },
                leadingIcon = Icons.Outlined.TrackChanges
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validate()) {
                        isLoading = true
                        viewModel.saveUser()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please fix the errors above before saving.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...")
                } else {
                    Icon(Icons.Outlined.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculate & Save", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
