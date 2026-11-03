package com.example.dz6.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.dz6.di.ProfileDependencies
import com.example.dz6.presentation.edit.EditProfileScreen
import com.example.dz6.presentation.edit.EditProfileViewModel
import com.example.dz6.presentation.profile.ProfileScreen
import com.example.dz6.presentation.profile.ProfileViewModel

const val PROFILE_GRAPH = "profile_graph"
private const val PROFILE = "profile"
private const val PROFILE_EDIT = "profile/edit"

fun NavGraphBuilder.profileGraph(navController: NavHostController) {
    navigation(startDestination = PROFILE, route = PROFILE_GRAPH) {
        composable(PROFILE) {
            val deps = ProfileDependencies.get(LocalContext.current)
            ProfileScreen(
                viewModel = viewModel(factory = ProfileViewModel.factory(deps)),
                onEditClick = { navController.navigate(PROFILE_EDIT) }
            )
        }
        composable(PROFILE_EDIT) {
            val deps = ProfileDependencies.get(LocalContext.current)
            EditProfileScreen(
                viewModel = viewModel(factory = EditProfileViewModel.factory(deps)),
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }
    }
}