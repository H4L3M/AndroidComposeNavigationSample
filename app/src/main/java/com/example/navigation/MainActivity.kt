package com.example.navigation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme
import java.util.*


val items = listOf(
    Screen.Home,
    Screen.Home2,
)

val icons = listOf(
    Icons.Rounded.Home,
    Icons.Rounded.Favorite,
)

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()

                var canPop by remember { mutableStateOf(false) }

                navController.addOnDestinationChangedListener { controller, _, _ ->
                    canPop = controller.previousBackStackEntry != null
                }

                val navigationIcon: (@Composable () -> Unit)? =
                    if (canPop) {
                        {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        null
                    }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        SmallTopAppBar(
                            title = { Text(text = "Toolbar") },
                            navigationIcon = {
                                if (navigationIcon != null) {
                                    navigationIcon()
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.primary)
                        )
                    },

                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon!!,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(text = screen.route.uppercase(Locale.getDefault())) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(Modifier.padding(paddingValues = paddingValues)) {
                        Main(navController = navController)
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun Main(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            Home {
                navController.navigate(route = Screen.Detail.withArgs(it))
            }
        }

        composable(route = Screen.Home2.route) {
            Home2(navController = navController)
        }

        composable(
            route = Screen.Detail.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "zmar safi"
                    nullable = true
                },
            ),
        ) {
            Detail(name = it.arguments?.getString("name"))
        }
    }

}

@Composable
fun Home(onNavigate: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(all = 50.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(text = "insert some characters here a zmer") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (text.isNotEmpty()) {
                    onNavigate(text)
                } else {
                    Toast.makeText(
                        context,
                        "atkhli dak l7za9 khawi? aytra lik mochkil a sat",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            Text(text = "brek")
        }
    }
}

@Composable
fun Home2(navController: NavHostController) {

    Column(
        modifier = Modifier
            .padding(all = 50.dp)
            .background(color = Color(0xFF00BCD4))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Home 2", fontSize = 36.sp)

    }
}

@ExperimentalMaterial3Api
@Composable
fun Detail(name: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(text = "afiin : $name", fontSize = 16.sp)
    }
}

sealed class Screen(val route: String, val icon: ImageVector?) {
    object Home : Screen(route = "home", icon = Icons.Rounded.Home)
    object Home2 : Screen(route = "home2", Icons.Rounded.Favorite)
    object Detail : Screen(route = "detail", null)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}