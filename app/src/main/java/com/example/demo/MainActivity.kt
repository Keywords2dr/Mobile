package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demo.ui.screens.ContactDetailScreen
import com.example.demo.ui.screens.ContactListScreen
import com.example.demo.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: ContactViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        ContactListScreen(
                            viewModel = viewModel,
                            onContactClick = { contact ->
                                navController.navigate("detail/${contact.id}")
                            }
                        )
                    }
                    composable(
                        route = "detail/{contactId}",
                        arguments = listOf(navArgument("contactId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val contactId = backStackEntry.arguments?.getInt("contactId")
                        val contacts by viewModel.contacts.collectAsState()
                        val contact = contacts.firstOrNull { it.id == contactId }
                        if (contact != null) {
                            ContactDetailScreen(
                                contact = contact,
                                onSave = { name, phone ->
                                    viewModel.updateContact(contact.id, name, phone)
                                    navController.popBackStack()
                                },
                                onDelete = {
                                    viewModel.deleteContact(contact)
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}