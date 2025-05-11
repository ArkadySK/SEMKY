package com.example.semky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.semky.ui.theme.SEMKYTheme
import com.example.semky.screens.SemPraceScreen
import com.example.semky.screens.BodyScreen
import com.example.semky.screens.KalendarScreen
import com.example.semky.data.database.SemkyDatabase
import com.example.semky.data.repository.SemPracaRepository
import com.example.semky.screens.AddEditSemPracaScreen
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaViewModelFactory

data class NavItem(
    var id: Int,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

class MainActivity : ComponentActivity() {
    private lateinit var database: SemkyDatabase
    private lateinit var repository: SemPracaRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = SemkyDatabase.getDatabase(applicationContext)
        repository = SemPracaRepository(database.semPracaDao())

        enableEdgeToEdge()
        setContent {
            SEMKYTheme {
                var selItemIndex by remember { mutableStateOf(0) }
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

                val viewModel = remember {
                    SemPracaViewModelFactory(repository).create(SemPracaViewModel::class.java)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = navItems[selItemIndex].title) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar() {
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
                                viewModel = viewModel,
                                onAddClick = { showAddDialog = true }
                            )
                            
                            if (showAddDialog) {
                                BasicAlertDialog(
                                    onDismissRequest = { showAddDialog = false }
                                ) {
                                    AddEditSemPracaScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { showAddDialog = false }
                                    )
                                }
                            }
                        }
                        1 -> BodyScreen(modifier = Modifier.padding(innerPadding))
                        else -> KalendarScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SEMKYTheme {
//        val database = remember {
//            val applicationContext = null
//            SemkyDatabase.getDatabase(applicationContext)
//        }
//        // Initialize repository
//        val repository = remember { SemPracaRepository(database.semPracaDao()) }
//        // Initialize ViewModel
//        val viewModel = remember {
//            SemPracaViewModelFactory(repository).create(SemPracaViewModel::class.java)
//        }
//        SemPraceScreen(
//            viewModel = viewModel,
//            onAddClick = {}
//        )
    }
}