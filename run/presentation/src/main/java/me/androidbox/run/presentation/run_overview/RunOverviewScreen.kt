@file:OptIn(ExperimentalFoundationApi::class)

package me.androidbox.run.presentation.run_overview

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.core.presentation.designsystem.AnalyticIcon
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.LogoIcon
import me.androidbox.core.presentation.designsystem.LogoutIcon
import me.androidbox.core.presentation.designsystem.RunIcon
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerFloatingActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerScaffold
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerToolbar
import me.androidbox.core.presentation.designsystem.components.util.DropDownItem
import me.androidbox.run.presentation.R
import me.androidbox.run.presentation.run_overview.components.RunListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunOverviewScreen(
    runOverviewState: RunOverviewState,
    runOverviewAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    BusbyRunnerScaffold(
        topAppBar = {
            BusbyRunnerToolbar(
                scrollBehavior = scrollBehavior,
                shouldShowBackButton = false,
                title = stringResource(R.string.busby_runner),
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = stringResource(id = R.string.logo),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                },
                onMenuItemClicked = { index ->
                    when(index) {
                        0 -> runOverviewAction(RunOverviewAction.OnAnalyticsClicked)
                        1 -> runOverviewAction(RunOverviewAction.OnLogoutClicked)
                    }
                },
                menuItems = listOf(
                    DropDownItem(title = stringResource(R.string.analytics), icon = AnalyticIcon),
                    DropDownItem(title = stringResource(R.string.logout), icon = LogoutIcon))
            )
        },
        floatActionButton = {
            BusbyRunnerFloatingActionButton(
                icon = RunIcon,
                onButtonClicked = {
                    runOverviewAction(RunOverviewAction.OnStartClicked)
                })
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {

                items(
                    items = runOverviewState.listOfRuns,
                    key = { runUi ->
                        runUi.id
                    },
                    itemContent = { runUi ->
                        RunListItem(
                            runUi = runUi,
                            modifier = Modifier.animateItemPlacement(),
                            onDeleteClicked = {
                                runOverviewAction(RunOverviewAction.OnDeleteRun(runUi.id))
                            })
                    }
                )
            }
        })
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRunOverviewScreen() {
    BusbyRunnerTheme {
        RunOverviewScreen(
            runOverviewAction = {},
            runOverviewState = RunOverviewState()
        )
    }
}