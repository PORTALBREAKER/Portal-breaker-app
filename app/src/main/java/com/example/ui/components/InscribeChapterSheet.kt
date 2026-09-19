package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.ui.theme.PortalCyan
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalSky
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.theme.VoidSurface
import com.example.ui.theme.glassmorphic
import com.example.ui.theme.neomorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscribeChapterSheet(
    existingChapter: ChapterEntity?,
    nextSuggestedNumber: Int,
    onSave: (
        existingId: Long?,
        chapterNumber: Int,
        title: String,
        volume: String,
        synopsis: String,
        content: String,
        authorNote: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var chapterNumStr by remember {
        mutableStateOf(existingChapter?.chapterNumber?.toString() ?: nextSuggestedNumber.toString())
    }
    var title by remember { mutableStateOf(existingChapter?.title ?: "") }
    var volume by remember {
        mutableStateOf(existingChapter?.volume ?: "Volume 1")
    }
    var synopsis by remember { mutableStateOf(existingChapter?.synopsis ?: "") }
    var content by remember { mutableStateOf(existingChapter?.content ?: "") }
    var authorNote by remember { mutableStateOf(existingChapter?.authorNote ?: "") }

    val wordCount = remember(content) {
        if (content.isBlank()) 0 else content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }
    val estimatedMinutes = remember(wordCount) {
        (wordCount / 200).coerceAtLeast(1)
    }

    var titleError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VoidDark.copy(alpha = 0.98f),
        scrimColor = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inscribe_chapter_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .glassmorphic(
                                shape = RoundedCornerShape(10.dp),
                                backgroundColor = PortalCyan.copy(alpha = 0.15f),
                                borderColor = PortalCyan
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = PortalCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (existingChapter == null) "INSCRIBE & PUBLISH" else "EDIT CHAPTER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Author Sanctuary • Worldwide Cloud Sync",
                            style = MaterialTheme.typography.labelSmall.copy(color = PortalCyan)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats HUD Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = VoidSurface.copy(alpha = 0.6f),
                        borderColor = VoidBorder
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Word Count", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                    Text("$wordCount words", style = MaterialTheme.typography.labelLarge.copy(color = PortalCyan))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Reading Time", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                    Text("~$estimatedMinutes min", style = MaterialTheme.typography.labelLarge.copy(color = PortalSky))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Target Volume", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                    Text("Vol 1", style = MaterialTheme.typography.labelLarge.copy(color = PortalPurple))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chapter Number & Volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = chapterNumStr,
                    onValueChange = { chapterNumStr = it },
                    label = { Text("Chapter #", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PortalCyan,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(0.35f)
                        .testTag("chapter_number_input")
                )

                OutlinedTextField(
                    value = volume,
                    onValueChange = { volume = it },
                    label = { Text("Story Arc / Volume", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PortalCyan,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(0.65f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chapter Title
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Chapter Title *", color = Color.Gray) },
                singleLine = true,
                isError = titleError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PortalCyan,
                    unfocusedBorderColor = VoidBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = VoidCard,
                    unfocusedContainerColor = VoidCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter_title_input")
            )
            if (titleError) {
                Text(
                    text = "Please provide a chapter title",
                    color = Color(0xFFEF4444),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Synopsis / Teaser
            OutlinedTextField(
                value = synopsis,
                onValueChange = { synopsis = it },
                label = { Text("Short Synopsis / Hook (Optional)", color = Color.Gray) },
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PortalCyan,
                    unfocusedBorderColor = VoidBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = VoidCard,
                    unfocusedContainerColor = VoidCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chapter Body Content
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    contentError = false
                },
                label = { Text("Chapter Body Content *", color = Color.Gray) },
                minLines = 8,
                maxLines = 16,
                isError = contentError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PortalCyan,
                    unfocusedBorderColor = VoidBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = VoidCard,
                    unfocusedContainerColor = VoidCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter_content_input")
            )
            if (contentError) {
                Text(
                    text = "Chapter story content cannot be empty",
                    color = Color(0xFFEF4444),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Author Note
            OutlinedTextField(
                value = authorNote,
                onValueChange = { authorNote = it },
                label = { Text("Author Note / Message to Readers (Optional)", color = Color.Gray) },
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PortalCyan,
                    unfocusedBorderColor = VoidBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = VoidCard,
                    unfocusedContainerColor = VoidCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.4f)
                        .height(50.dp)
                ) {
                    Text("Cancel", color = Color.LightGray)
                }

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                            return@Button
                        }
                        if (content.isBlank()) {
                            contentError = true
                            return@Button
                        }
                        val num = chapterNumStr.toIntOrNull() ?: nextSuggestedNumber
                        onSave(
                            existingChapter?.id,
                            num,
                            title,
                            volume,
                            synopsis,
                            content,
                            authorNote
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PortalCyan,
                        contentColor = VoidDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.6f)
                        .height(50.dp)
                        .testTag("publish_chapter_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Publish,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (existingChapter == null) "Publish Chapter" else "Save Changes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
