package by.autohelper.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import by.autohelper.features.auth.ui.LoginScreen
import by.autohelper.features.expenses.ui.ExpensesScreen
import by.autohelper.features.fines.ui.FinesScreen
import by.autohelper.features.garage.ui.GarageScreen
import by.autohelper.features.reminders.ui.RemindersScreen
import by.autohelper.features.sto.ui.StoScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Garage    : Screen("garage",    "Гараж",        Icons.Default.DirectionsCar)
    object Fines     : Screen("fines",     "Штрафы",       Icons.Default.Receipt)
    object Reminders : Screen("reminders", "Напоминания",  Icons.Default.Notifications)
    object Expenses  : Screen("expenses",  "Расходы",      Icons.Default.AccountBalanceWallet)
    object Sto       : Screen("sto",       "СТО",          Icons.Default.Build)
}

val bottomNavItems = listOf(
    Screen.Garage,
    Screen.Fines,
    Screen.Reminders,
    Screen.Expenses,
    Screen.Sto,
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Проверяем залогинен ли пользователь
    // TODO: получать из DataStore
    val isLoggedIn = remember { mutableStateOf(false) }

    if (!isLoggedIn.value) {
        LoginScreen(onLoginSuccess = { isLoggedIn.value = true })
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon     = { Icon(screen.icon, contentDescription = screen.title) },
                        label    = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Garage.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Garage.route)    { GarageScreen() }
            composable(Screen.Fines.route)     { FinesScreen() }
            composable(Screen.Reminders.route) { RemindersScreen() }
            composable(Screen.Expenses.route)  { ExpensesScreen() }
            composable(Screen.Sto.route)       { StoScreen() }
        }
    }
}
