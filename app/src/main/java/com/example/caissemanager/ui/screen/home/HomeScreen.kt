package com.example.caissemanager.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Addchart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Outbox
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caissemanager.ui.component.AnimatedContentTransition
import com.example.caissemanager.ui.screen.compte.CompteViewModel
import com.example.caissemanager.ui.screen.encaissement.DeCaissementScreen
import com.example.caissemanager.ui.screen.encaissement.EncaissementScreen
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.Decoration
import java.text.NumberFormat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToCompte: () -> Unit,
    compteViewModel: CompteViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uistate = homeViewModel.uistate.value

    HomeScreenContent(
        onNavigateToCompte = onNavigateToCompte,
        compteViewModel = compteViewModel,
        uistate = uistate,
        onMenuChange = homeViewModel::onChangingMenu,
        onSearchClick = homeViewModel::onSearchClick,
        onMenuClick = homeViewModel::onMenuClick,
        onMenuDismiss = homeViewModel::onMenuDismiss,
        onTotClick = homeViewModel::onMenuTotClick,
        onTotDialogDismiss = homeViewModel::onTotDialogDismiss,
        onGraPhicShown = homeViewModel::onGraphicShown,
        onGraPhicDismiss = homeViewModel::onGraphicDismiss
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uistate: HomeViewModelUiState,
    onNavigateToCompte: () -> Unit,
    compteViewModel: CompteViewModel,
    onMenuChange: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onTotClick: () -> Unit,
    onTotDialogDismiss: () -> Unit,
    onGraPhicShown: () -> Unit,
    onGraPhicDismiss: () -> Unit,
) {

    var selectedItem by remember { mutableIntStateOf(0) }
    Scaffold(

        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContentTransition(
                        targetState = uistate.title,
                        initialState = uistate.title
                    ) { target ->
                        Text(
                            target.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }

                },
                actions = {

                    IconButton(onClick = { onSearchClick() }) {


                        AnimatedContent(targetState = uistate.isSearchShown) { tagert ->
                            Icon(
                                imageVector = if (tagert) Icons.Outlined.SearchOff else Icons.Rounded.Search,
                                contentDescription = null
                            )
                        }

                    }
                    IconButton(onClick = { onNavigateToCompte() }) {
                        Icon(
                            Icons.Outlined.Addchart,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = { onMenuClick() }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = null
                        )


                        DropdownMenu(
                            expanded = uistate.isMenuShow,
                            onDismissRequest = { onMenuDismiss() }) {


                            DropdownMenuItem(
                                text = { Text(text = "Livre de caisse") },
                                onClick = {},
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Book,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Total dans la caisse") },
                                onClick = {
                                    onTotClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.MonetizationOn,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }


                })
        },
        bottomBar = {

            NavigationBar(content = {
                bottomNavItems.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = index == selectedItem,
                        onClick = {
                            onMenuChange(index)
                            selectedItem = index
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = null) },
                        label = { Text(item.title) })
                }

            })
        }) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            when (selectedItem) {

                0 -> EncaissementScreen(compteViewModel = compteViewModel, homeUiState = uistate)
                1 -> DeCaissementScreen(compteViewModel = compteViewModel, homeUiState = uistate)
            }

        }
    }

    if (uistate.isTotDialogShown) {

        AlertDialog(
            onDismissRequest = { onTotDialogDismiss() },
            dismissButton = {
                TextButton(onClick = { onTotDialogDismiss(); onGraPhicShown() }) {
                    Text(
                        "Grahpick"
                    )
                }
            },
            confirmButton = { TextButton(onClick = { onTotDialogDismiss() }) { Text("Ok") } },
            title = {
                Text(
                    "Apperçu rapid de la caisse",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Column {
                    Row {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Recettes", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${
                                    NumberFormat.getInstance().format(uistate.totalUSDEncaissement)
                                } $"
                            )
                            Text(
                                "${
                                    NumberFormat.getInstance().format(uistate.totalCDFEncaissement)
                                } Fc"
                            )


                        }

                        VerticalDivider(
                            Modifier
                                .fillMaxHeight(0.1f)
                                .padding(horizontal = 16.dp)
                        )
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.Start
                        ) {

                            Text("Depenses", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${
                                    NumberFormat.getInstance().format(uistate.totalUSDDecaissement)
                                } $"
                            )
                            Text(
                                "${
                                    NumberFormat.getInstance().format(uistate.totalCDFDecaissement)
                                } Fc"
                            )

                        }


                    }

                    Spacer(Modifier.height(22.dp))
                    Text(
                        "Solde USD  :  ${NumberFormat.getInstance().format(uistate.soldeUSD)} $",
                        fontWeight = FontWeight.W900
                    )
                    Text(
                        "Solde CDF  :  ${NumberFormat.getInstance().format(uistate.soldeCDF)} Fc",
                        fontWeight = FontWeight.W900
                    )
                }
            }
        )
    }


    if (uistate.isGraphicShown) {


        AlertDialog(onDismissRequest = { onGraPhicDismiss() }, text = {


            val modelProducer = remember { CartesianChartModelProducer() }
            LaunchedEffect(Unit) {
                modelProducer.runTransaction {
                    columnSeries {
                        series(
                            y = listOf(
                                uistate.totalUSDEncaissement,
                                uistate.totalCDFEncaissement,
                                uistate.totalUSDDecaissement,
                                uistate.totalCDFDecaissement
                            ),

                            )
                    }
                }
            }
            CartesianChartHost(
                rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(),

                    ),
                modelProducer,
                animationSpec = spring()
            )
        }, confirmButton = {})
    }
}


val bottomNavItems = listOf(
    BottomNavItem("Recettes", Icons.Outlined.Inbox),
    BottomNavItem("Dépenses", Icons.Outlined.Outbox),

    )

data class BottomNavItem(val title: String, val icon: ImageVector)
