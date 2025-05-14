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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.semky.data.repository.SemPracaRepository
import com.example.semky.data.repository.SemPracaPointsRepository
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaViewModelFactory
import com.example.semky.viewmodel.SemPracaPointsViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModelFactory
import com.example.semky.screens.EditSemPracaScreen

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = SemkyDatabase.getDatabase(applicationContext)
        repository = SemPracaRepository(database.semPracaDao())
        pointsRepository = SemPracaPointsRepository(database.semPracaPointsDao())

        enableEdgeToEdge()
        setContent {
            SEMKYTheme {
                var selItemIndex by remember { mutableIntStateOf(0) }
                var showAddDialog by remember { mutableStateOf(false) }

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
                    SemPracaPointsViewModelFactory(pointsRepository).create(
                        SemPracaPointsViewModel::class.java
                    )
                }

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
                                viewModel = semPraceViewModel
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