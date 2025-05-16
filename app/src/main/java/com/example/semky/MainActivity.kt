package com.example.semky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.semky.ui.theme.SEMKYTheme
import com.example.semky.screens.SemPraceScreen
import com.example.semky.screens.BodyScreen
import com.example.semky.screens.KalendarScreen
import com.example.semky.data.database.SemkyDatabase
import com.example.semky.data.repository.DeadlineRepository
import com.example.semky.data.repository.SemPracaRepository
import com.example.semky.data.repository.SemPracaPointsRepository
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaViewModelFactory
import com.example.semky.viewmodel.SemPracaPointsViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModelFactory
import com.example.semky.screens.EditSemPracaScreen
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.DeadlineViewModelFactory

data class NavItem(
    var id: Int,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

class MainActivity : ComponentActivity() {
    private lateinit var database: SemkyDatabase
    private lateinit var repository: SemPracaRepository
    private lateinit var pointsRepository: SemPracaPointsRepository
    private lateinit var deadlineRepository: DeadlineRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = SemkyDatabase.getDatabase(applicationContext)
        repository = SemPracaRepository(database.semPracaDao())
        pointsRepository = SemPracaPointsRepository(database.semPracaPointsDao())
        deadlineRepository = DeadlineRepository(database.deadlineDao())

        enableEdgeToEdge()
        setContent {
            SEMKYTheme {
                var selItemIndex by rememberSaveable { mutableIntStateOf(0) }
                var showAddDialog by rememberSaveable { mutableStateOf(false) }

                val navItems = listOf(
                    NavItem(
                        id = 0,
                        title = "Sem. práce",
                        icon = Icons.Default.Menu,
                        selectedIcon = Icons.Filled.Menu
                    ),
                    NavItem(
                        id = 1,
                        title = "Body",
                        icon = Icons.Default.Info,
                        selectedIcon = Icons.Filled.Info
                    ),
                    NavItem(
                        id = 2,
                        title = "Kalendár",
                        icon = Icons.Default.DateRange,
                        selectedIcon = Icons.Filled.DateRange
                    )
                )

                val semPraceViewModel = remember {
                    SemPracaViewModelFactory(repository, applicationContext).create(
                        SemPracaViewModel::class.java
                    )
                }

                val pointsViewModel = remember {
                    SemPracaPointsViewModelFactory(pointsRepository, deadlineRepository).create(
                        SemPracaPointsViewModel::class.java
                    )
                }

                val deadlineViewModel = remember {
                    DeadlineViewModelFactory(deadlineRepository, applicationContext).create(
                        DeadlineViewModel::class.java
                    )
                }

                val allPoints by pointsViewModel.allPoints.collectAsState()
                val totalPoints = allPoints.sumOf { it.points }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    text = navItems[selItemIndex].title
                                )
                            },
                            actions = {
                                if (selItemIndex == 0) { // iba na stránke sem. práce
                                    Button(
                                        onClick = { showAddDialog = true },
                                        modifier = Modifier
                                            .padding(end = 16.dp),
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                else if(selItemIndex == 1) {
                                    Text(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        text = "$totalPoints bodov",
                                        modifier = Modifier
                                            .padding(end = 16.dp),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            for (navItem in navItems) {
                                NavigationBarItem(
                                    selected = (navItem.id == selItemIndex),
                                    onClick = { selItemIndex = navItem.id },
                                    icon = {
                                        BadgedBox(
                                            // TODO: neskôr ?
                                            badge = {
                                            }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = if (navItem.id == selItemIndex) navItem.selectedIcon else navItem.icon,
                                                    contentDescription = navItem.title
                                                )
                                                Text(text = navItem.title)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (selItemIndex) {
                        0 -> {
                            SemPraceScreen(
                                modifier = Modifier.padding(innerPadding),
                                pointsViewModel = pointsViewModel,
                                viewModel = semPraceViewModel,
                                deadlineViewModel = deadlineViewModel
                            )
                        }

                        1 -> BodyScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = semPraceViewModel,
                            pointsViewModel = pointsViewModel
                        )

                        else -> KalendarScreen(modifier = Modifier.padding(innerPadding))
                    }

                    if (showAddDialog) {
                        Dialog(
                            onDismissRequest = { showAddDialog = false }
                        ) {
                            EditSemPracaScreen(
                                viewModel = semPraceViewModel,
                                pointsViewModel = pointsViewModel,
                                deadlineViewModel = deadlineViewModel,
                                existingPraca = null,
                                isEditMode = true,
                                onNavigateBack = { showAddDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {

}