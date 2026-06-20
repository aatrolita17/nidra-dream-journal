package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Dream
import com.example.ui.AddDreamUiState
import com.example.ui.DreamViewModel
import com.example.ui.OracleUiState
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_screen")
                ) { innerPadding ->
                    NidraAppContainer(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NidraAppContainer(
    modifier: Modifier = Modifier,
    viewModel: DreamViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(0) } // 0 = Journal, 1 = Oracle, 2 = Insights
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBackground,
                        Color(0xFF07030D)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Header Area
            TitleHeaderSection()

            // Main Tab Content Area
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    0 -> JournalTabContent(viewModel = viewModel)
                    1 -> OracleTabContent(viewModel = viewModel)
                    2 -> InsightsTabContent(viewModel = viewModel)
                }
            }

            // Beautiful Bottom Navigation Bar
            NidraNavigationBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    }
}

@Composable
fun TitleHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Brightness3,
                contentDescription = "Crescent Moon Logo",
                tint = AmethystSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = "NIDRA",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 4.sp,
                color = MoonSilver,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "AI Subconscious Journal & Mythos Oracle",
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 1.sp,
            color = AmethystSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NidraNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = ObsidianSurface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationBarItem(
                isSelected = selectedTab == 0,
                icon = Icons.Default.Book,
                label = "Journal",
                onClick = { onTabSelected(0) },
                testTag = "tab_journal"
            )
            NavigationBarItem(
                isSelected = selectedTab == 1,
                icon = Icons.Default.AutoAwesome,
                label = "Oracle",
                onClick = { onTabSelected(1) },
                testTag = "tab_oracle"
            )
            NavigationBarItem(
                isSelected = selectedTab == 2,
                icon = Icons.Default.Analytics,
                label = "Insights",
                onClick = { onTabSelected(2) },
                testTag = "tab_insights"
            )
        }
    }
}

@Composable
fun RowScope.NavigationBarItem(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    val scale by animateFloatAsState(if (isSelected) 1.2f else 1.0f)
    val color = if (isSelected) AmethystSecondary else Color.Gray.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 28.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) AmethystPrimary.copy(alpha = 0.25f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier
                    .size(20.dp)
                    .animateContentSize()
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

/* ==========================================================================
   JOURNAL TAB
   ========================================================================== */

@Composable
fun JournalTabContent(
    viewModel: DreamViewModel
) {
    val dreams by viewModel.allDreams.collectAsStateWithLifecycle()
    val addDreamState by viewModel.addDreamUiState.collectAsStateWithLifecycle()
    var showAddDreamDialog by remember { mutableStateOf(false) }
    var selectedDreamForDetail by remember { mutableStateOf<Dream?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDreams = remember(dreams, searchQuery) {
        if (searchQuery.isBlank()) dreams else {
            dreams.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true) ||
                        it.moodTag.contains(searchQuery, ignoreCase = true) ||
                        it.typeTag.contains(searchQuery, ignoreCase = true) ||
                        (it.archetypes != null && it.archetypes.contains(searchQuery, ignoreCase = true))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant celestial canvas drawn dynamically as header
            CelestialHeaderCanvas(dreamCount = dreams.size)

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("dream_search_input"),
                placeholder = { Text("Search tags, symbols, or dream text...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = AmethystSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmethystSecondary,
                    unfocusedBorderColor = ObsidianSurfaceVariant,
                    focusedContainerColor = ObsidianSurface,
                    unfocusedContainerColor = ObsidianSurface,
                    focusedTextColor = MoonSilver,
                    unfocusedTextColor = MoonSilver
                )
            )

            // Dream List
            if (filteredDreams.isEmpty()) {
                EmptyStateView(
                    hasSearchQuery = searchQuery.isNotEmpty(),
                    onAddPromptClicked = { showAddDreamDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDreams, key = { it.id }) { dream ->
                        DreamCard(
                            dream = dream,
                            onClick = { selectedDreamForDetail = dream },
                            onDelete = { viewModel.deleteDream(dream) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                viewModel.resetAddDreamState()
                showAddDreamDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_dream_fab"),
            containerColor = AmethystPrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log Dream")
        }

        // Add Dream Dialog
        if (showAddDreamDialog) {
            AddDreamDialog(
                uiState = addDreamState,
                onDismiss = { showAddDreamDialog = false },
                onSave = { content, lucidity, intensity, mood, type, phase ->
                    viewModel.analyzeAndSaveDream(content, lucidity, intensity, mood, type, phase)
                }
            )
        }

        // Dream Detailed Interpretation Overlay Dialog
        if (selectedDreamForDetail != null) {
            DreamInterpretationDialog(
                dream = selectedDreamForDetail!!,
                onDismiss = { selectedDreamForDetail = null }
            )
        }
    }
}

@Composable
fun CelestialHeaderCanvas(dreamCount: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, AmethystPrimary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ObsidianSurface,
                        Color(0xFF1E1035)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Star Coordinates drawing with animated opacity
            val stars = listOf(
                Offset(width * 0.15f, height * 0.2f),
                Offset(width * 0.4f, height * 0.15f),
                Offset(width * 0.75f, height * 0.25f),
                Offset(width * 0.9f, height * 0.45f),
                Offset(width * 0.25f, height * 0.7f),
                Offset(width * 0.6f, height * 0.75f),
                Offset(width * 0.8f, height * 0.78f)
            )

            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = 2.dp.toPx(),
                    center = star
                )
            }

            // Draw a subtle mountain landscape baseline outline
            val path = Path().apply {
                moveTo(0f, height)
                quadraticTo(width * 0.25f, height * 0.65f, width * 0.5f, height * 0.85f)
                quadraticTo(width * 0.75f, height * 0.55f, width, height)
                close()
            }
            drawPath(path, Brush.verticalGradient(listOf(AmethystPrimary.copy(alpha = 0.08f), Color.Transparent)))

            // Draw Crescent Moon
            val moonCenter = Offset(width * 0.85f, height * 0.4f)
            val moonRadius = 18.dp.toPx()
            drawCircle(
                color = MoonSilver,
                radius = moonRadius,
                center = moonCenter
            )
            // Crescent shadow overlay
            drawCircle(
                color = Color(0xFF1E1035),
                radius = moonRadius,
                center = Offset(moonCenter.x - 8.dp.toPx(), moonCenter.y - 3.dp.toPx())
            )
        }

        // Text display on Celestial Banner
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
        ) {
            Text(
                text = "Your Subconscious Realm",
                fontSize = 15.sp,
                color = MoonSilver,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$dreamCount Cosmic Journeys Logged",
                fontSize = 12.sp,
                color = AmethystTertiary,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun EmptyStateView(
    hasSearchQuery: Boolean,
    onAddPromptClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(
                        Brush.radialGradient(
                            listOf(AmethystSecondary.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        radius = size.width * 0.7f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (hasSearchQuery) Icons.Default.SearchOff else Icons.Default.NightsStay,
                contentDescription = "Cosmic Sleep Empty State",
                tint = AmethystSecondary.copy(alpha = 0.75f),
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasSearchQuery) "No Echoes Found" else "No Dreams Whispered Yet",
            fontSize = 18.sp,
            color = MoonSilver,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (hasSearchQuery) "Your database holds no memories of those symbols. Try typing another search term."
            else "Log your first dream. Nidra will automatically translate your subconscious cues into archetypes and psycho-metaphorical wisdom.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (!hasSearchQuery) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAddPromptClicked,
                colors = ButtonDefaults.buttonColors(containerColor = AmethystPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Whisper Your First Dream", color = Color.White)
            }
        }
    }
}

@Composable
fun DreamCard(
    dream: Dream,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val moodColor = when (dream.moodTag) {
        "Serene" -> SereneGreen
        "Anxious" -> AnxiousRed
        "Vivid" -> VividBlue
        "Mystical" -> MysticalIndigo
        "Eerie" -> EerieOrange
        else -> AmethystSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("dream_card_${dream.id}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(18.dp),
        border = borderStrokeGradient()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Card Top Row Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Moon Phase Indicator + Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Nocturnal Phase",
                        tint = AmethystTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dream.moonPhase,
                        fontSize = 10.sp,
                        color = AmethystSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Dream",
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = dream.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MoonSilver,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Content Preview text
            Text(
                text = dream.content,
                fontSize = 13.sp,
                color = MoonSilver.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Mood badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(moodColor.copy(alpha = 0.15f))
                            .border(0.5.dp, moodColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dream.moodTag,
                            fontSize = 10.sp,
                            color = moodColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmethystPrimary.copy(alpha = 0.12f))
                            .border(0.5.dp, AmethystPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dream.typeTag,
                            fontSize = 10.sp,
                            color = AmethystTertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Lucidity Star Gauge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Lucidity Stars",
                        tint = StarGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Lucidity: ${dream.lucidity}/5",
                        fontSize = 10.sp,
                        color = MoonSilver.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun borderStrokeGradient() = androidx.compose.foundation.BorderStroke(
    width = 0.75.dp,
    brush = Brush.linearGradient(
        listOf(
            AmethystPrimary.copy(alpha = 0.35f),
            Color.Transparent,
            AmethystSecondary.copy(alpha = 0.15f)
        )
    )
)

/* ==========================================================================
   ADD DREAM DIALOG
   ========================================================================== */

@Composable
fun AddDreamDialog(
    uiState: AddDreamUiState,
    onDismiss: () -> Unit,
    onSave: (content: String, lucidity: Int, intensity: Int, mood: String, type: String, phase: String) -> Unit
) {
    var contentText by remember { mutableStateOf("") }
    var lucidityValue by remember { mutableStateOf(3) }
    var intensityValue by remember { mutableStateOf(3) }
    var selectedMood by remember { mutableStateOf("Serene") }
    var selectedType by remember { mutableStateOf("Mundane") }
    var selectedPhase by remember { mutableStateOf("Waxing Gibbous") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val moods = listOf("Serene", "Anxious", "Vivid", "Mystical", "Eerie")
    val types = listOf("Lucid", "Nightmare", "Prophetic", "Cosmic", "Healing", "Mundane")
    val phases = listOf("New Moon", "Waxing Crescent", "Full Moon", "Waning Gibbous", "Solar Eclipse")

    Dialog(onDismissRequest = { if (uiState !is AddDreamUiState.Analyzing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("add_dream_dialog"),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            shape = RoundedCornerShape(24.dp),
            border = borderStrokeGradient()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState is AddDreamUiState.Analyzing) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AmethystSecondary)
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Nidra is decoding your subconscious...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoonSilver,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Consulting ancient dream tablets & mapping planetary alignments. Please wait.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header Dialog
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Whisper a Dream",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoonSilver
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = MoonSilver)
                            }
                        }

                        // LazyColumn of parameters inside content
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Your Subconscious Description",
                                    fontSize = 12.sp,
                                    color = AmethystSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = contentText,
                                    onValueChange = { contentText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .testTag("dream_content_input"),
                                    placeholder = {
                                        Text(
                                            "Describe what happened, who was there, what symbols you saw, and how you felt...",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                         )
                                    },
                                    minLines = 4,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AmethystSecondary,
                                        unfocusedBorderColor = ObsidianSurfaceVariant,
                                        focusedTextColor = MoonSilver,
                                        unfocusedTextColor = MoonSilver
                                    )
                                )
                            }

                            // Sliders for Lucidity
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Lucidity (How aware were you?)",
                                            fontSize = 12.sp,
                                            color = AmethystSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(text = "$lucidityValue/5", fontSize = 12.sp, color = MoonSilver)
                                    }
                                    Slider(
                                        value = lucidityValue.toFloat(),
                                        onValueChange = { lucidityValue = it.toInt() },
                                        valueRange = 1f..5f,
                                        steps = 3,
                                        colors = SliderDefaults.colors(
                                            thumbColor = AmethystSecondary,
                                            activeTrackColor = AmethystPrimary
                                        )
                                    )
                                }
                            }

                            // Sliders for Emotional intensity
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Intensity (How vivid or intense?)",
                                            fontSize = 12.sp,
                                            color = AmethystSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(text = "$intensityValue/5", fontSize = 12.sp, color = MoonSilver)
                                    }
                                    Slider(
                                        value = intensityValue.toFloat(),
                                        onValueChange = { intensityValue = it.toInt() },
                                        valueRange = 1f..5f,
                                        steps = 3,
                                        colors = SliderDefaults.colors(
                                            thumbColor = AmethystSecondary,
                                            activeTrackColor = AmethystPrimary
                                        )
                                    )
                                }
                            }

                            // Horizontal Tag Selectors for fallback (will use if Gemini is empty or offline)
                            item {
                                Text(
                                    text = "Dream Mood Alignment",
                                    fontSize = 12.sp,
                                    color = AmethystSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    moods.forEach { m ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (selectedMood == m) AmethystPrimary else ObsidianSurfaceVariant
                                                )
                                                .clickable { selectedMood = m }
                                                .border(
                                                    0.5.dp,
                                                    if (selectedMood == m) AmethystSecondary else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = m,
                                                fontSize = 11.sp,
                                                color = if (selectedMood == m) Color.White else MoonSilver.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Dream Type Selectors for fallback
                            item {
                                Text(
                                    text = "Predicted Dream Sphere",
                                    fontSize = 12.sp,
                                    color = AmethystSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    types.take(4).forEach { t ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (selectedType == t) AmethystPrimary else ObsidianSurfaceVariant
                                                )
                                                .clickable { selectedType = t }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = t,
                                                fontSize = 10.sp,
                                                color = if (selectedType == t) Color.White else MoonSilver.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Moon Phase Selector
                            item {
                                Text(
                                    text = "Current Earth Moon Phase",
                                    fontSize = 12.sp,
                                    color = AmethystSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    phases.take(3).forEach { p ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (selectedPhase == p) AmethystPrimary else ObsidianSurfaceVariant
                                                )
                                                .clickable { selectedPhase = p }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = p,
                                                fontSize = 10.sp,
                                                color = if (selectedPhase == p) Color.White else MoonSilver.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Trigger Button
                        Button(
                            onClick = {
                                if (contentText.isBlank()) {
                                    Toast.makeText(context, "Please describe your dream first.", Toast.LENGTH_SHORT).show()
                                } else {
                                    onSave(contentText, lucidityValue, intensityValue, selectedMood, selectedType, selectedPhase)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("save_dream_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AmethystPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Consult Oracle & Log Dream", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Success Auto dismiss triggered in dialog internally
                LaunchedEffect(uiState) {
                    if (uiState is AddDreamUiState.Success) {
                        onDismiss()
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   DREAM INTERPRETATION OVERLAY DIALOG
   ========================================================================== */

@Composable
fun DreamInterpretationDialog(
    dream: Dream,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .testTag("dream_interpretation_dialog"),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            shape = RoundedCornerShape(24.dp),
            border = borderStrokeGradient()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Headline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flare,
                            contentDescription = "Interpreter Logo",
                            tint = StarGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nidra Oracle Decipher",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AmethystSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MoonSilver)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poetic Dream Title
                    item {
                        Text(
                            text = dream.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoonSilver,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    // Original content description transcript
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "ORIGINAL IMPRESSION",
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = AmethystSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dream.content,
                                fontSize = 12.sp,
                                color = MoonSilver.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Interpretation
                    item {
                        Column {
                            Text(
                                text = "ORACULAR MEANING",
                                fontSize = 11.sp,
                                letterSpacing = 1.5.sp,
                                color = AmethystSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = dream.interpretation ?: "Decoding was interrupted. Look within: what does this image say to you?",
                                fontSize = 13.sp,
                                color = MoonSilver,
                                lineHeight = 20.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    // Archetypes
                    item {
                        Column {
                            Text(
                                text = "SUBSEDENT ARCHETYPES DETECTED",
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = AmethystSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val splitList = dream.archetypes?.split(",") ?: listOf("Cosmic Glow", "Ego")
                                splitList.forEach { valStr ->
                                    val cleanText = valStr.trim()
                                    if (cleanText.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(AmethystSecondary.copy(alpha = 0.15f))
                                                .border(0.5.dp, AmethystSecondary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = cleanText,
                                                fontSize = 11.sp,
                                                color = MoonSilver,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom spacer with info
                Divider(color = ObsidianSurfaceVariant, modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vibrancy score: Rank ${dream.intensity}/5",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceVariant)
                    ) {
                        Text("Close Interpretation", fontSize = 12.sp, color = MoonSilver)
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   ORACLE TAB
   ========================================================================== */

@Composable
fun OracleTabContent(
    viewModel: DreamViewModel
) {
    val oracleState by viewModel.oracleUiState.collectAsStateWithLifecycle()
    var symbolInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val archetypesGlossary = listOf(
        Pair("Water Depth", "Emotional undercurrents, purification, state of flow, and fear of sinking."),
        Pair("Locked Gates", "Barriers of belief, secret vaults of knowledge, or blockages in waking tasks."),
        Pair("Flying Path", "Ascending above terrestrial constraints, psychological freedom, and fresh scope."),
        Pair("Chasing Shape", "An ignored calling or avoided challenge running close behind your heels.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Descriptive Header
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Consult Nidra Oracle",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MoonSilver
        )
        Text(
            text = "Input any symbol, color, or emotional projection encountered in your dreams to receive immediate deep-mythology answers.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Box
        OutlinedTextField(
            value = symbolInput,
            onValueChange = { symbolInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("oracle_symbol_input"),
            placeholder = { Text("e.g. A silver rabbit, black keys, a library in fire...", color = Color.Gray, fontSize = 13.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AmethystSecondary,
                unfocusedBorderColor = ObsidianSurfaceVariant,
                focusedContainerColor = ObsidianSurface,
                unfocusedContainerColor = ObsidianSurface,
                focusedTextColor = MoonSilver,
                unfocusedTextColor = MoonSilver
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (symbolInput.isNotBlank()) {
                            viewModel.queryOracle(symbolInput)
                        } else {
                            Toast.makeText(context, "State your curiosity first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("consult_oracle_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Query", tint = AmethystSecondary)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Decipher Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            when (oracleState) {
                is OracleUiState.Idle -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "PRIMORDIAL ARCHETYPES GLOSSARY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmethystSecondary,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(archetypesGlossary) { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(ObsidianSurface)
                                        .border(0.5.dp, ObsidianSurfaceVariant, RoundedCornerShape(14.dp))
                                        .clickable {
                                            symbolInput = item.first
                                            viewModel.queryOracle(item.first)
                                        }
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.first,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MoonSilver
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.second,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is OracleUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AmethystSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Deciphering code scrolls...", fontSize = 13.sp, color = AmethystTertiary)
                    }
                }
                is OracleUiState.Success -> {
                    val result = (oracleState as OracleUiState.Success).response
                    OracleResultCard(result = result, onReset = {
                        symbolInput = ""
                        viewModel.resetOracleState()
                    })
                }
                is OracleUiState.Error -> {
                    val errorMsg = (oracleState as OracleUiState.Error).message
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Communication with Aether faded.", color = AnxiousRed, fontWeight = FontWeight.Bold)
                        Text(text = errorMsg, color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun OracleResultCard(
    result: com.example.network.OracleResponse,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .testTag("oracle_result_card"),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(20.dp),
        border = borderStrokeGradient()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Brightness2,
                        contentDescription = "Deciphered Icon",
                        tint = StarGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ORACULAR DECODE",
                        fontSize = 11.sp,
                        color = AmethystSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Clear result", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.symbolTitle,
                fontSize = 18.sp,
                color = MoonSilver,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Meaning Section
            Text(
                text = "SYMBOLIC PROFILE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AmethystTertiary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = result.symbolMeaning,
                fontSize = 13.sp,
                color = MoonSilver.copy(alpha = 0.9f),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Archetype Section
            Text(
                text = "MYTHICAL CARD LINK",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AmethystTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = result.archetypalConnection,
                fontSize = 13.sp,
                color = AmethystSecondary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lucid navigation Tip
            Text(
                text = "LUCID INTENT ADVICE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AmethystTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = result.lucidTip,
                fontSize = 12.sp,
                color = MoonSilver,
                lineHeight = 18.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Inquire Another Symbol", color = MoonSilver)
            }
        }
    }
}

/* ==========================================================================
   INSIGHTS TAB
   ========================================================================== */

@Composable
fun InsightsTabContent(
    viewModel: DreamViewModel
) {
    val dreams by viewModel.allDreams.collectAsStateWithLifecycle()

    val totalCount = dreams.size
    val averageLucidity = if (totalCount > 0) String.format("%.1f", dreams.map { it.lucidity }.average()) else "0.0"
    val averageIntensity = if (totalCount > 0) String.format("%.1f", dreams.map { it.intensity }.average()) else "0.0"

    // Grouping calculations
    val moodGroups = remember(dreams) {
        dreams.groupBy { it.moodTag }
            .mapValues { it.value.size }
    }

    val typeGroups = remember(dreams) {
        dreams.groupBy { it.typeTag }
            .mapValues { it.value.size }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Subconscious Analytics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MoonSilver
            )
            Text(
                text = "Statistical alignment patterns mapped from your stored subconscious logs.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Metrics Grid Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Average Lucidity",
                    value = averageLucidity,
                    icon = Icons.Default.FilterVintage,
                    color = AmethystSecondary
                )
                MetricGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Vivid Intensity",
                    value = averageIntensity,
                    icon = Icons.Default.Whatshot,
                    color = StarGold
                )
            }
        }

        // Custom drawn Mood chart using Compose Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp),
                border = borderStrokeGradient()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Subconscious Mood Frequencies",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmethystTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (totalCount == 0) {
                        Text(
                            text = "No parameters logged. Whisper a dream to initialize calculations.",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    } else {
                        val moodsList = listOf("Serene", "Anxious", "Vivid", "Mystical", "Eerie")
                        moodsList.forEach { mood ->
                            val count = moodGroups[mood] ?: 0
                            val share = if (totalCount > 0) count.toFloat() / totalCount else 0f
                            val color = when (mood) {
                                "Serene" -> SereneGreen
                                "Anxious" -> AnxiousRed
                                "Vivid" -> VividBlue
                                "Mystical" -> MysticalIndigo
                                "Eerie" -> EerieOrange
                                else -> AmethystSecondary
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mood,
                                    modifier = Modifier.width(76.dp),
                                    fontSize = 11.sp,
                                    color = MoonSilver
                                )

                                // Horizontal Bar
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(if (share > 0) share else 0.02f)
                                            .background(color)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "$count logs",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom drawn Dream Sphere type chart using Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp),
                border = borderStrokeGradient()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dream Spheres Imprint Map",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmethystTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (totalCount == 0) {
                        Text(
                            text = "Log dreams to map your astral types.",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    } else {
                        val spheres = listOf("Lucid", "Nightmare", "Prophetic", "Cosmic", "Healing", "Mundane")
                        spheres.forEach { t ->
                            val count = typeGroups[t] ?: 0
                            val share = if (totalCount > 0) count.toFloat() / totalCount else 0f

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t,
                                    modifier = Modifier.width(76.dp),
                                    fontSize = 11.sp,
                                    color = MoonSilver
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(if (share > 0) share else 0.02f)
                                            .background(AmethystPrimary)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = String.format("%.0f%%", share * 100),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mystic Insight Oracle advice card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = StarGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRIMORDIAL WISDOM ADVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmethystSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val computedMessage = when {
                        totalCount == 0 -> "Your nocturnal chronicle remains clear. Whisper dreams to populate your psychological alignment maps."
                        dreams.any { it.lucidity >= 4 } -> "You are experiencing high lucidity waves. Your awake mind is crossing seamlessly into sleeping dimensions. Focus on locking portals of fear when they occur."
                        dreams.any { it.moodTag == "Anxious" } -> "Tensions of conscious cycles seem to reflect in your night realm. Meditate upon calm pool symbols or water tags to ease your transition into sleep."
                        else -> "Your sleeping current remains exceptionally steady. Continue keeping records of your dream glyphs to discover deeper archetypes over time."
                    }

                    Text(
                        text = computedMessage,
                        fontSize = 12.sp,
                        color = MoonSilver.copy(alpha = 0.9f),
                        lineHeight = 18.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun MetricGridCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(18.dp),
        border = borderStrokeGradient()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MoonSilver
            )
        }
    }
}
