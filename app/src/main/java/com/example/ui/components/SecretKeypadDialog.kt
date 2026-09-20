package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PortalCrimson
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalRedLightning
import com.example.ui.theme.PortalSky
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.theme.glassmorphic
import com.example.ui.theme.neomorphic

@Composable
fun SecretKeypadDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    verifyKey: (String) -> Boolean
) {
    var passcodeInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphic(
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = VoidDark.copy(alpha = 0.94f),
                    borderColor = PortalRedLightning.copy(alpha = 0.6f),
                    secondaryBorderColor = PortalCrimson.copy(alpha = 0.4f)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                // Top close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                // Glowing Rune Seal Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    PortalRedLightning.copy(alpha = 0.35f),
                                    PortalPurple.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, PortalRedLightning, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Void Lock",
                        tint = PortalRedLightning,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AUTHOR AUTHENTICATION",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = PortalRedLightning,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Author Console",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter your verified Author Gmail or the 4-digit author passcode to unlock management controls.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Passcode or Email text field
                OutlinedTextField(
                    value = passcodeInput,
                    onValueChange = {
                        passcodeInput = it
                        isError = false
                    },
                    label = { Text("Author Email or Passcode", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (verifyKey(passcodeInput)) {
                            onSuccess()
                        } else {
                            isError = true
                        }
                    }),
                    isError = isError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PortalRedLightning,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("secret_key_input")
                )

                AnimatedVisibility(visible = isError) {
                    Text(
                        text = "Access denied. Only divakaryased123@gmail.com or valid author key is authorized.",
                        color = Color(0xFFEF4444),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Unlock Button
                Button(
                    onClick = {
                        if (verifyKey(passcodeInput)) {
                            onSuccess()
                        } else {
                            isError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PortalRedLightning,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_author_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verify Author Access",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
