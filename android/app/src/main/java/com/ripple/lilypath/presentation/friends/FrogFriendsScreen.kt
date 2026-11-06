package com.ripple.lilypath.presentation.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.R
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.theme.FriendsOverlay
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.TextMuted
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.core.ui.AppIcons
import com.ripple.lilypath.core.ui.GlassCard
import com.ripple.lilypath.core.ui.ScreenBackground
import com.ripple.lilypath.core.ui.seasonAccent
import com.ripple.lilypath.core.ui.seasonName
import com.ripple.lilypath.domain.model.FrogFriend

@Composable
fun FrogFriendsScreen(onBack: () -> Unit) {
    val friendsViewModel: FrogFriendsViewModel = viewModel(factory = AppViewModelFactory)
    val state by friendsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        friendsViewModel.refresh()
    }

    FrogFriendsContent(state = state, onBack = onBack)
}

@Composable
private fun FrogFriendsContent(
    state: FrogFriendsUiState,
    onBack: () -> Unit
) {
    ScreenBackground(overlay = FriendsOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = stringResource(R.string.menu),
                        tint = PondPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.frog_friends),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (state.empty) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.solve_season),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.friends, key = { it.season.name }) { friend ->
                        FrogCard(friend = friend)
                    }
                }
            }
        }
    }
}

@Composable
private fun FrogCard(friend: FrogFriend) {
    val state = stringResource(
        if (friend.unlocked) R.string.state_open else R.string.state_locked
    )
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = state }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = if (friend.unlocked) friend.name else null,
                modifier = Modifier
                    .size(96.dp)
                    .alpha(if (friend.unlocked) 1f else 0.18f),
                contentScale = ContentScale.Fit
            )
            if (!friend.unlocked) {
                Icon(
                    imageVector = AppIcons.Lock,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (friend.unlocked) friend.name else stringResource(R.string.locked_name),
            style = MaterialTheme.typography.titleMedium,
            color = if (friend.unlocked) TextPrimary else TextMuted,
            textAlign = TextAlign.Center
        )
        Text(
            text = seasonName(friend.season),
            style = MaterialTheme.typography.labelSmall,
            color = seasonAccent(friend.season),
            textAlign = TextAlign.Center
        )
    }
}
