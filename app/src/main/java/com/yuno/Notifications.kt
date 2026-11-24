package com.yuno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuno.icons.SfIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen displaying notifications and recent uploads from subscribed channels.
 */
@Composable
fun NotificationsScreen(
    onOpen: (VideoItem) -> Unit,
    onSignIn: () -> Unit,
    contentPadding: PaddingValues,
) {
    val signedIn = Account.signedIn
    val listState = rememberLazyListState()
    val top = topBarHeight() + TopBarContentGap
    val scope = rememberCoroutineScope()
    val openChannel = LocalChannelOpener.current

    var loading by remember { mutableStateOf(false) }
    var refreshing by remember { mutableStateOf(false) }
    val notifications = remember { mutableStateListOf<VideoItem>() }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun loadNotifications(isRefresh: Boolean = false) {
        if (!signedIn) {
            notifications.clear()
            return
        }
        if (isRefresh) refreshing = true else if (notifications.isEmpty()) loading = true
        error = null
        try {
            val feed = withContext(Dispatchers.IO) {
                InnerTube.subscriptionsFeed()
            }
            notifications.clear()
            notifications.addAll(feed.videos)
        } catch (e: Exception) {
            error = e.message ?: "Failed to load notifications"
        } finally {
            loading = false
            refreshing = false
        }
    }

    LaunchedEffect(signedIn) {
        if (signedIn) {
            loadNotifications()
        } else {
            notifications.clear()
        }
    }

    if (!signedIn) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(state = listState, contentPadding = contentPadding.withTop(top)) {
                item(key = "title") {
                    LargePageTitle("Notifications", fontSize = LargePageTitleCompactFontSize, barAligned = false)
                }
                item(key = "empty") {
                    EmptyState(
                        icon = SfIcons.Notifications,
                        title = "Notifications",
                        body = "Sign in with Google to get alerts and new videos from your subscribed channels.",
                        action = { PillButton("Sign in with Google", onSignIn) },
                    )
                }
            }
            PageTopFade(listState)
        }
        return
    }

    BarPullRefresh(
        refreshing = refreshing,
        onRefresh = { scope.launch { loadNotifications(isRefresh = true) } },
    ) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = contentPadding.withTop(top),
            ) {
                item(key = "title") {
                    LargePageTitle("Notifications", fontSize = LargePageTitleCompactFontSize, barAligned = false)
                }

                if (loading && notifications.isEmpty()) {
                    item(key = "loading") {
                        FeedSkeletons(4)
                    }
                } else if (error != null && notifications.isEmpty()) {
                    item(key = "error") {
                        EmptyState(
                            icon = SfIcons.Notifications,
                            title = "Could not load notifications",
                            body = error ?: "An error occurred",
                            action = {
                                PillButton("Try again", { scope.launch { loadNotifications() } })
                            },
                        )
                    }
                } else if (notifications.isEmpty()) {
                    item(key = "empty_signed_in") {
                        EmptyState(
                            icon = SfIcons.Notifications,
                            title = "No notifications yet",
                            body = "When channels you follow upload new videos, they will appear here.",
                        )
                    }
                } else {
                    items(items = notifications, key = { it.id }) { video ->
                        NotificationCard(
                            video = video,
                            onClick = { onOpen(video) },
                            onChannelClick = {
                                video.channelUrl?.let(openChannel)
                            },
                        )
                    }
                }
            }
            PageTopFade(listState)
        }
    }
}

@Composable
private fun NotificationCard(
    video: VideoItem,
    onClick: () -> Unit,
    onChannelClick: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressable(onClick = onClick)
            .padding(horizontal = PageGutter, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Channel avatar with tap to open channel
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .clickable(onClick = onChannelClick),
        ) {
            ChannelAvatar(url = video.avatar, name = video.channel, size = 44)
        }

        Spacer(Modifier.width(12.dp))

        // Text information
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = video.channel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = c.onBackground,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onChannelClick),
            )
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = c.onSurfaceVariant,
                    lineHeight = 16.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!video.uploaded.isNullOrBlank()) {
                Text(
                    text = video.uploaded,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = c.onSurfaceVariant.copy(alpha = 0.7f),
                    ),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Video thumbnail preview with duration pill
        Box(
            modifier = Modifier
                .width(96.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp))
                .background(c.surfaceContainerHighest),
        ) {
            Thumbnail(
                url = video.thumbnail,
                videoId = video.id,
                radius = 10,
                modifier = Modifier.fillMaxSize(),
            )
            DurationPill(
                seconds = video.durationSec,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                small = true,
                live = video.live,
            )
        }
    }
}
