package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReadingSettingsEntity
import com.example.ui.theme.PortalCrimson
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalRedLightning
import com.example.ui.theme.PortalSky
import com.example.ui.theme.ReaderThemes
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.theme.VoidSurface
import com.example.ui.theme.glassmorphic
import com.example.ui.theme.neomorphic

data class ThemeOption(
    val id: String,
    val name: String,
    val bgColor: Color,
    val textColor: Color,
    val accentColor: Color
)

val availableThemes = listOf(
    ThemeOption("CRIMSON_RIFT", "Red Lightning", ReaderThemes.CrimsonRiftBg, ReaderThemes.CrimsonRiftText, ReaderThemes.CrimsonRiftAccent),
    ThemeOption("MIDNIGHT_NEBULA", "Midnight", ReaderThemes.MidnightBg, ReaderThemes.MidnightText, ReaderThemes.MidnightAccent),
    ThemeOption("DARK_VOID", "Void OLED", ReaderThemes.VoidBg, ReaderThemes.VoidText, ReaderThemes.VoidAccent),
    ThemeOption("SEPIA_PARCHMENT", "Parchment", ReaderThemes.SepiaBg, ReaderThemes.SepiaText, ReaderThemes.SepiaAccent),
    ThemeOption("CYBER_MINT", "Cyber Mint", ReaderThemes.CyberBg, ReaderThemes.CyberText, ReaderThemes.CyberAccent),
    ThemeOption("SOLAR_LIGHT", "Solar Day", ReaderThemes.SolarBg, ReaderThemes.SolarText, ReaderThemes.SolarAccent)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSettingsSheet(
    settings: ReadingSettingsEntity,
    onSettingsChanged: (ReadingSettingsEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VoidDark.copy(alpha = 0.96f),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.testTag("reading_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = PortalRedLightning,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Reading & Typography",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Typography Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = VoidCard.copy(alpha = 0.8f),
                        borderColor = PortalRedLightning.copy(alpha = 0.4f)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PortalRedLightning,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The cosmic rift resonated with pure red lightning, warping the laws of spatial physics.",
                        fontSize = settings.fontSizeSp.sp,
                        lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                        fontFamily = when (settings.fontFamilyType) {
                            "SERIF" -> FontFamily.Serif
                            "MONOSPACE" -> FontFamily.Monospace
                            else -> FontFamily.Default
                        },
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reading Theme Selection
            Text(
                text = "READING CANVAS THEME",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PortalCrimson,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                availableThemes.forEach { theme ->
                    val isSelected = settings.themeId == theme.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                onSettingsChanged(settings.copy(themeId = theme.id))
                            }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(theme.bgColor)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) PortalRedLightning else VoidBorder,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) PortalRedLightning else Color.LightGray,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Font Family Choice
            Text(
                text = "FONT TYPEFACE",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PortalCrimson,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "SERIF" to "Classic Book (Serif)",
                    "SANS" to "Modern Clean (Sans)",
                    "MONOSPACE" to "Cosmic Rune (Mono)"
                ).forEach { (type, label) ->
                    val isSelected = settings.fontFamilyType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSettingsChanged(settings.copy(fontFamilyType = type)) },
                        label = {
                            Text(
                                text = label,
                                fontFamily = when (type) {
                                    "SERIF" -> FontFamily.Serif
                                    "MONOSPACE" -> FontFamily.Monospace
                                    else -> FontFamily.Default
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PortalRedLightning.copy(alpha = 0.2f),
                            selectedLabelColor = PortalRedLightning,
                            containerColor = VoidSurface,
                            labelColor = Color.LightGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PortalRedLightning else VoidBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Font Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FONT SIZE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PortalCrimson,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "${settings.fontSizeSp.toInt()} sp",
                    color = PortalRedLightning,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Slider(
                value = settings.fontSizeSp,
                onValueChange = { onSettingsChanged(settings.copy(fontSizeSp = it)) },
                valueRange = 13f..26f,
                steps = 12,
                colors = SliderDefaults.colors(
                    thumbColor = PortalRedLightning,
                    activeTrackColor = PortalRedLightning,
                    inactiveTrackColor = VoidBorder
                ),
                modifier = Modifier.testTag("font_size_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Line Spacing Multiplier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LINE SPACING",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PortalCrimson,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "${String.format("%.1f", settings.lineSpacingMultiplier)}x",
                    color = PortalRedLightning,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Slider(
                value = settings.lineSpacingMultiplier,
                onValueChange = { onSettingsChanged(settings.copy(lineSpacingMultiplier = it)) },
                valueRange = 1.3f..2.2f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = PortalRedLightning,
                    activeTrackColor = PortalRedLightning,
                    inactiveTrackColor = VoidBorder
                ),
                modifier = Modifier.testTag("line_spacing_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Text Alignment & Format Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Justified Text Alignment",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Clean novel book edge alignment",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }
                Switch(
                    checked = settings.isJustified,
                    onCheckedChange = { onSettingsChanged(settings.copy(isJustified = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PortalRedLightning,
                        checkedTrackColor = PortalRedLightning.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = VoidSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Paragraph Indentation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Paragraph First-Line Indent",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Traditional literary publication format",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }
                Switch(
                    checked = settings.paragraphIndent,
                    onCheckedChange = { onSettingsChanged(settings.copy(paragraphIndent = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PortalRedLightning,
                        checkedTrackColor = PortalRedLightning.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = VoidSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Keep Screen On
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Keep Display Awake",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Prevent screen timeout while immersed in reading",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }
                Switch(
                    checked = settings.keepScreenOn,
                    onCheckedChange = { onSettingsChanged(settings.copy(keepScreenOn = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PortalRedLightning,
                        checkedTrackColor = PortalRedLightning.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = VoidSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
