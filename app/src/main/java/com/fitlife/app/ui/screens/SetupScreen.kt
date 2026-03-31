import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlife.app.viewModel.UserFormState

import com.fitlife.app.viewModel.UserViewModel

// todo: this is a basic form we'll need to switch text fields for better ux
@Composable
fun SetupScreen(viewModel: UserViewModel){
    val formState = viewModel.formState.observeAsState(UserFormState())
    Column(
        modifier = Modifier
            .padding(16.dp,16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text(
            text = "Setup Screen",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "Name",
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.name,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = {Text("eg. Ali Ljabali")}
        )

        Text("Age")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.age,
            onValueChange = { viewModel.onAgeChange(it) },
            placeholder = { Text("eg. 22") }
        )

        Text("Height (cm)")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.height,
            onValueChange = { viewModel.onHeightChange(it) },
            placeholder = { Text("eg. 180") }
        )

        Text("Weight (kg)")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.weight,
            onValueChange = { viewModel.onWeightChange(it) },
            placeholder = { Text("eg. 75") }
        )

        Text("Gender")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.gender,
            onValueChange = { viewModel.onGenderChange(it) },
            placeholder = { Text("Male / Female") }
        )

        Text("Activity Level")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.activityLevel,
            onValueChange = { viewModel.onActivityLevelChange(it) },
            placeholder = { Text("Sedentary / Moderate / Active") }
        )

        Text("Goal")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.value.goalType,
            onValueChange = { viewModel.onGoalChange(it) },
            placeholder = { Text("Lose / Maintain / Gain") }
        )

        Spacer(modifier = Modifier.padding(8.dp))
        androidx.compose.material3.Button(
            onClick = {
                viewModel.saveUser()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate & Save")
        }
    }
}