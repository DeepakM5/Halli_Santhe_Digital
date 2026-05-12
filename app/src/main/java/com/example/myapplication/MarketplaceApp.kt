package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private enum class AuthStage { ROLE, PHONE, OTP }
private enum class AppDestination { AUTH, VENDOR, CUSTOMER }

@Composable
fun MarketplaceApp() {
    val currentUser = MarketplaceStore.currentUser

    val destination = when {
        currentUser == null -> AppDestination.AUTH
        currentUser.role == UserRole.VENDOR -> AppDestination.VENDOR
        else -> AppDestination.CUSTOMER
    }

    Crossfade(targetState = destination, animationSpec = tween(320), label = "app_navigation") { screen ->
        when (screen) {
            AppDestination.AUTH -> AuthFlow()
            AppDestination.VENDOR -> VendorFlow()
            AppDestination.CUSTOMER -> CustomerFlow()
        }
    }
}

@Composable
private fun AuthFlow() {
    var stage by rememberSaveable { mutableStateOf(AuthStage.ROLE) }
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.CUSTOMER.name) }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var infoText by rememberSaveable { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = stage, animationSpec = tween(260), label = "auth_navigation") { authStage ->
            when (authStage) {
                AuthStage.ROLE -> RoleSelectionScreen(
                    onRoleSelected = { role ->
                        selectedRole = role.name
                        stage = AuthStage.PHONE
                    }
                )

                AuthStage.PHONE -> PhoneEntryScreen(
                    selectedRole = UserRole.valueOf(selectedRole),
                    phoneNumber = phoneNumber,
                    infoText = infoText,
                    onBack = { stage = AuthStage.ROLE },
                    onPhoneChange = { phoneNumber = it },
                    onSendOtp = { role, phone ->
                        val session = MarketplaceStore.startOtpSession(role, phone)
                        infoText = "Demo OTP: ${session.code}"
                        stage = AuthStage.OTP
                    }
                )

                AuthStage.OTP -> OtpVerificationScreen(
                    selectedRole = UserRole.valueOf(selectedRole),
                    phoneNumber = phoneNumber,
                    demoCode = MarketplaceStore.pendingSession?.code.orEmpty(),
                    onBack = { stage = AuthStage.PHONE },
                    onResend = {
                        val session = MarketplaceStore.startOtpSession(UserRole.valueOf(selectedRole), phoneNumber)
                        infoText = "Demo OTP: ${session.code}"
                    },
                    onVerify = { code ->
                        val success = MarketplaceStore.verifyOtp(code)
                        if (!success) {
                            infoText = "Invalid OTP. Try again."
                        }
                        success
                    },
                    infoText = infoText
                )
            }
        }
    }
}

@Composable
private fun RoleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
    val context = LocalContext.current

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF9933).copy(alpha = 0.26f),
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.82f)
                        .height(210.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF7A1A),
                                        Color(0xFFD72638),
                                        Color(0xFF138808)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HalliSantheLogo(modifier = Modifier.size(92.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Halli Santhe Digital",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Connecting local artisans and farmers directly to your neighborhood.",
                                color = Color.White.copy(alpha = 0.88f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Halli Santhe Digital",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(18.dp))

                RoleActionCard(
                    title = "SELLER",
                    subtitle = "List products & grow business",
                    icon = "SHOP",
                    accent = MaterialTheme.colorScheme.primary,
                    onClick = { onRoleSelected(UserRole.VENDOR) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                RoleActionCard(
                    title = "CUSTOMER",
                    subtitle = "Buy fresh & local goods",
                    icon = "BUY",
                    accent = MaterialTheme.colorScheme.secondary,
                    onClick = { onRoleSelected(UserRole.CUSTOMER) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { Toast.makeText(context, "Use Vendor or Customer to continue", Toast.LENGTH_SHORT).show() }) {
                    Text("Already have an account? Login")
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { Toast.makeText(context, "Privacy policy will be added later", Toast.LENGTH_SHORT).show() }) { Text("PRIVACY") }
                    Text(text = "|", color = MaterialTheme.colorScheme.outline)
                    TextButton(onClick = { Toast.makeText(context, "Terms will be added later", Toast.LENGTH_SHORT).show() }) { Text("TERMS") }
                    Text(text = "|", color = MaterialTheme.colorScheme.outline)
                    TextButton(onClick = { Toast.makeText(context, "Help center coming soon", Toast.LENGTH_SHORT).show() }) { Text("HELP") }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniStat(value = "500+", label = "VENDORS", tint = MaterialTheme.colorScheme.primary)
                    VerticalDivider()
                    MiniStat(value = "20k+", label = "ITEMS SOLD", tint = MaterialTheme.colorScheme.secondary)
                    VerticalDivider()
                    MiniStat(value = "15+", label = "DISTRICTS", tint = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
private fun HalliSantheLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(22.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.08f
            drawCircle(Color.White.copy(alpha = 0.95f))
            drawArc(
                color = Color(0xFFFF9933),
                startAngle = 200f,
                sweepAngle = 110f,
                useCenter = false,
                style = Stroke(width = stroke)
            )
            drawArc(
                color = Color(0xFF138808),
                startAngle = 20f,
                sweepAngle = 110f,
                useCenter = false,
                style = Stroke(width = stroke)
            )
            drawRect(
                color = Color(0xFFD72638),
                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.52f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.22f)
            )
            drawRect(
                color = Color(0xFF138808),
                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.42f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.12f)
            )
        }
        Text(
            text = "HD",
            color = Color(0xFF4B1F12),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun RoleActionCard(title: String, subtitle: String, icon: String, accent: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedClickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        Brush.linearGradient(listOf(accent.copy(alpha = 0.24f), MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f))),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = ">", style = MaterialTheme.typography.headlineSmall, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PhoneEntryScreen(
    selectedRole: UserRole,
    phoneNumber: String,
    infoText: String,
    onBack: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onSendOtp: (UserRole, String) -> Unit
) {
    val context = LocalContext.current
    val cleanedPhone = remember(phoneNumber) { phoneNumber.filter { it.isDigit() }.take(10) }
    var localPhone by rememberSaveable(phoneNumber) { mutableStateOf(cleanedPhone) }

    LaunchedEffect(localPhone) {
        if (localPhone != phoneNumber) onPhoneChange(localPhone)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AuthHeaderBar(
                title = "Halli Santhe",
                showHelp = true,
                onBack = onBack,
                onHelp = { Toast.makeText(context, "Help will be available soon", Toast.LENGTH_SHORT).show() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(108.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(54.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔒", style = MaterialTheme.typography.displayLarge)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Secure phone verification", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Enter Your Phone Number",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We'll send you a verification code",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Phone Number",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🇮🇳")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "+91", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = localPhone,
                        onValueChange = {
                            localPhone = it.filter(Char::isDigit).take(10)
                            onPhoneChange(localPhone)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("00000 00000") },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                AnimatedButton(
                    onClick = {
                        if (localPhone.length == 10) {
                            onPhoneChange(localPhone)
                            onSendOtp(selectedRole, localPhone)
                        } else {
                            Toast.makeText(context, "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text("SEND OTP")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏪")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Joining as", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            Text(text = selectedRole.displayName, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        }
                        Text(text = "✓", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                        text = "By continuing, you agree to Halli Santhe's Terms of Service and Privacy Policy.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                if (infoText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = infoText, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun OtpVerificationScreen(
    selectedRole: UserRole,
    phoneNumber: String,
    demoCode: String,
    infoText: String,
    onBack: () -> Unit,
    onResend: () -> Unit,
    onVerify: (String) -> Boolean
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    var secondsLeft by remember { mutableStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    val allDigits = otpDigits.joinToString(separator = "")

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AuthHeaderBar(
                title = "Halli Santhe",
                showHelp = true,
                onBack = onBack,
                onHelp = { Toast.makeText(context, "Help will be available soon", Toast.LENGTH_SHORT).show() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(132.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(66.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔐", style = MaterialTheme.typography.displayLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Verification Code",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the 6-digit code sent to +91 $phoneNumber",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    otpDigits.forEachIndexed { index, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = { input ->
                                val digits = input.filter(Char::isDigit)
                                when {
                                    digits.length > 1 -> {
                                        digits.take(6 - index).forEachIndexed { offset, digit ->
                                            otpDigits[index + offset] = digit.toString()
                                        }
                                        val nextIndex = (index + digits.length).coerceAtMost(focusRequesters.lastIndex)
                                        focusRequesters[nextIndex].requestFocus()
                                    }

                                    digits.isNotEmpty() -> {
                                        otpDigits[index] = digits
                                        if (index < focusRequesters.lastIndex) {
                                            focusRequesters[index + 1].requestFocus()
                                        }
                                    }

                                    else -> {
                                        otpDigits[index] = ""
                                        if (index > 0) {
                                            focusRequesters[index - 1].requestFocus()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .focusRequester(focusRequesters[index])
                                .onPreviewKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                                        when {
                                            otpDigits[index].isNotBlank() -> {
                                                otpDigits[index] = ""
                                                if (index > 0) {
                                                    focusRequesters[index - 1].requestFocus()
                                                }
                                                true
                                            }

                                            index > 0 -> {
                                                otpDigits[index - 1] = ""
                                                focusRequesters[index - 1].requestFocus()
                                                true
                                            }

                                            else -> {
                                            true
                                            }
                                        }
                                    } else {
                                        false
                                    }
                                },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (demoCode.isBlank()) "Demo OTP available after sending code" else "Demo OTP: $demoCode",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
                if (infoText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = infoText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))
                AnimatedButton(
                    onClick = {
                        if (allDigits.length == 6) {
                            isVerifying = true
                            val success = onVerify(allDigits)
                            isVerifying = false
                            if (!success) {
                                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                            } else {
                                focusManager.clearFocus()
                            }
                        } else {
                            Toast.makeText(context, "Enter all 6 digits", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isVerifying && allDigits.length == 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text("VERIFY & CONTINUE")
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        if (secondsLeft == 0) {
                            otpDigits.indices.forEach { otpDigits[it] = "" }
                            secondsLeft = 60
                            onResend()
                        }
                    },
                    enabled = secondsLeft == 0,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (secondsLeft == 0) "Resend Code" else "Resend (${secondsLeft}s)")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = onBack) {
                    Text("Wrong number? Change Number")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Secure 256-bit encryption",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun AuthHeaderBar(title: String, showHelp: Boolean, onBack: () -> Unit, onHelp: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackArrowButton(onClick = onBack)

        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        if (showHelp && onHelp != null) {
            Button(
                onClick = onHelp,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("?")
            }
        } else {
            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun BackArrowButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = "←",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = (-5).dp)
        )
    }
}

@Composable
private fun MiniStat(value: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = tint, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(34.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    )
}

@Composable
private fun CustomerFlow() {
    var screen by rememberSaveable { mutableStateOf("HOME") }
    var selectedProductId by rememberSaveable { mutableStateOf("") }

    Crossfade(targetState = screen, animationSpec = tween(260), label = "customer_navigation") { page ->
        when (page) {
            "HOME" -> CustomerHomeScreen(
                onLogout = { MarketplaceStore.logout() },
                onOpenProduct = {
                    selectedProductId = it
                    screen = "DETAIL"
                }
            )

            "DETAIL" -> ProductDetailScreen(
                productId = selectedProductId,
                onBack = { screen = "HOME" },
                onLogout = { MarketplaceStore.logout() }
            )
        }
    }
}

@Composable
private fun VendorFlow() {
    var screen by rememberSaveable { mutableStateOf("HOME") }
    var selectedProductId by rememberSaveable { mutableStateOf("") }

    Crossfade(targetState = screen, animationSpec = tween(260), label = "vendor_navigation") { page ->
        when (page) {
            "HOME" -> VendorDashboardScreen(
                onLogout = { MarketplaceStore.logout() },
                onUpload = { screen = "UPLOAD" },
                onReviews = { screen = "REVIEWS" },
                onOpenProduct = {
                    selectedProductId = it
                    screen = "DETAIL"
                }
            )

            "UPLOAD" -> UploadProductScreen(
                onBack = { screen = "HOME" },
                onDone = { screen = "HOME" },
                onLogout = { MarketplaceStore.logout() }
            )

            "REVIEWS" -> VendorReviewsScreen(
                onBack = { screen = "HOME" },
                onLogout = { MarketplaceStore.logout() }
            )

            "DETAIL" -> ProductDetailScreen(
                productId = selectedProductId,
                onBack = { screen = "HOME" },
                onLogout = { MarketplaceStore.logout() }
            )
        }
    }
}

@Composable
private fun CustomerHomeScreen(onLogout: () -> Unit, onOpenProduct: (String) -> Unit) {
    val allProducts = MarketplaceStore.activeProducts()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val selectedCategories = remember { mutableStateListOf<String>() }

    val filteredProducts by remember(searchQuery, allProducts, selectedCategories.toList()) {
        derivedStateOf {
            val query = searchQuery.trim().lowercase(Locale.getDefault())
            allProducts.filter { product ->
                val matchesSearch = query.isBlank() || product.name.lowercase(Locale.getDefault()).contains(query) || product.description.lowercase(Locale.getDefault()).contains(query)
                val matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(product.category)
                matchesSearch && matchesCategory
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Customer Dashboard",
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Browse fresh local products",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search products") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategories.isEmpty(),
                        onClick = { selectedCategories.clear() },
                        label = { Text("All") }
                    )
                }
                items(MarketplaceStore.categories.size) { index ->
                    val category = MarketplaceStore.categories[index]
                    FilterChip(
                        selected = selectedCategories.contains(category),
                        onClick = {
                            if (selectedCategories.contains(category)) {
                                selectedCategories.remove(category)
                            } else {
                                selectedCategories.add(category)
                            }
                        },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (filteredProducts.isEmpty()) {
                EmptyStateCard(
                    title = "No products found",
                    message = "Try a different search or clear the category filters."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    gridItems(filteredProducts, key = { it.id }) { product ->
                        ProductCard(product = product, onClick = { onOpenProduct(product.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorDashboardScreen(
    onLogout: () -> Unit,
    onUpload: () -> Unit,
    onReviews: () -> Unit,
    onOpenProduct: (String) -> Unit
) {
    val currentUser = MarketplaceStore.currentUser ?: return
    val vendorProducts = MarketplaceStore.getVendorProducts(currentUser.uid)
    val vendorReviews = MarketplaceStore.getVendorReviews(currentUser.uid)
    val avgRating = if (vendorReviews.isEmpty()) 0.0 else vendorReviews.map { it.rating }.average()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Vendor Dashboard",
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Welcome, ${currentUser.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentUser.phoneNumber,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Products",
                        value = vendorProducts.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Reviews",
                        value = vendorReviews.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Rating",
                        value = if (vendorReviews.isEmpty()) "—" else DecimalFormat("0.0").format(avgRating),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                AnimatedButton(onClick = onUpload, modifier = Modifier.fillMaxWidth()) { Text("Upload Product") }
            }
            item {
                AnimatedOutlinedButton(onClick = onReviews, modifier = Modifier.fillMaxWidth()) { Text("View Reviews") }
            }
            item {
                Text(text = "My Products", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            if (vendorProducts.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No products yet",
                        message = "Upload your first product to start selling."
                    )
                }
            } else {
                lazyItems(vendorProducts, key = { it.id }) { product ->
                    VendorProductRow(product = product, onClick = { onOpenProduct(product.id) })
                }
            }
        }
    }
}

@Composable
private fun VendorProductRow(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedClickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            MarketplaceImage(label = product.name, subtitle = product.category, imageUrl = product.imageUrl, modifier = Modifier.size(84.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("₹${product.price} • ${product.category}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("★ ${formatRating(product.averageRating)} (${product.reviewCount} reviews)", color = MaterialTheme.colorScheme.primary)
                if (product.soldOut) {
                    Text("Sold Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(
                onClick = {
                    MarketplaceStore.deleteProduct(product.id)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Product",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun UploadProductScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    MarketplaceStore.currentUser ?: return
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                selectedImageUri = compressProductImage(context, uri)
                if (selectedImageUri == null) {
                    Toast.makeText(context, "Could not load this image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var freshness by rememberSaveable { mutableStateOf("Fresh Today") }
    var origin by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var freshnessMenu by rememberSaveable { mutableStateOf(false) }
    var categoryMenu by rememberSaveable { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    val freshnessOptions = listOf("Fresh Today", "1-2 Days Old", "3-5 Days Old")
    val showFreshness = MarketplaceStore.requiresFreshness(category)

    LaunchedEffect(category) {
        if (!MarketplaceStore.requiresFreshness(category)) {
            freshness = ""
        } else if (freshness.isBlank()) {
            freshness = freshnessOptions.first()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Upload Product",
                showBack = true,
                onBack = onBack,
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .animatedClickable { imagePicker.launch("image/*") },
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tap to add photo", fontWeight = FontWeight.SemiBold)
                            Text(text = "Product image required", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        MarketplaceImage(
                            label = name.ifBlank { "Selected photo" },
                            subtitle = selectedImageUri.orEmpty(),
                            imageUrl = selectedImageUri.orEmpty(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Product name") },
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Price (₹)") },
                singleLine = true
            )

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Origin / Source") }
            )

            MarketDropdown(
                label = "Category",
                value = category.ifBlank { "Select category" },
                expanded = categoryMenu,
                onExpandedChange = { categoryMenu = it },
                options = MarketplaceStore.categories,
                onOptionSelected = { category = it; categoryMenu = false }
            )

            if (showFreshness) {
                MarketDropdown(
                    label = "Freshness",
                    value = freshness,
                    expanded = freshnessMenu,
                    onExpandedChange = { freshnessMenu = it },
                    options = freshnessOptions,
                    onOptionSelected = { freshness = it; freshnessMenu = false }
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it.take(500) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 3
            )

            AnimatedButton(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    when {
                        selectedImageUri == null -> Toast.makeText(context, "Please select a photo", Toast.LENGTH_SHORT).show()
                        name.trim().length < 3 -> Toast.makeText(context, "Product name must be at least 3 characters", Toast.LENGTH_SHORT).show()
                        parsedPrice == null || parsedPrice <= 0 -> Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                        origin.isBlank() -> Toast.makeText(context, "Enter origin / source", Toast.LENGTH_SHORT).show()
                        category.isBlank() -> Toast.makeText(context, "Select a category", Toast.LENGTH_SHORT).show()
                        else -> {
                            isSaving = true
                            MarketplaceStore.addProduct(
                                name = name.trim(),
                                price = parsedPrice,
                                freshness = if (MarketplaceStore.requiresFreshness(category)) freshness else "",
                                origin = origin.trim(),
                                category = category,
                                description = description.trim(),
                                imageUrl = selectedImageUri.orEmpty()
                            )
                            isSaving = false
                            Toast.makeText(context, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                            onDone()
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Upload Product")
            }
        }
    }
}

@Composable
private fun VendorReviewsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val currentUser = MarketplaceStore.currentUser ?: return
    val vendorReviews = MarketplaceStore.getVendorReviews(currentUser.uid)
    val avgRating = if (vendorReviews.isEmpty()) 0.0 else vendorReviews.map { it.rating }.average()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Reviews",
                showBack = true,
                onBack = onBack,
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = formatRating(avgRating), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Text(text = if (vendorReviews.isEmpty()) "No reviews yet" else "Based on ${vendorReviews.size} reviews")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (vendorReviews.isEmpty()) {
                EmptyStateCard(title = "No reviews yet", message = "When customers rate your products, they’ll appear here.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    lazyItems(vendorReviews, key = { it.id }) { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val product = MarketplaceStore.getProductById(productId)
    var showReviewDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(productId) {
        MarketplaceStore.markViewed(productId)
    }

    if (product == null) {
        Scaffold(
            topBar = { AppTopBar(title = "Product Detail", showBack = true, onBack = onBack, onLogout = onLogout) }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Product not found")
            }
        }
        return
    }

    val recentReviews = MarketplaceStore.getProductReviews(productId)
    val vendor = MarketplaceStore.getUserById(product.vendorId)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Product Detail",
                showBack = true,
                onBack = onBack,
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            MarketplaceImage(
                label = product.name,
                subtitle = "${product.category} • ₹${formatPrice(product.price)}",
                imageUrl = product.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "★ ${formatRating(product.averageRating)} (${product.reviewCount} reviews)", color = MaterialTheme.colorScheme.primary)
                Text(text = "₹${formatPrice(product.price)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Card(shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (product.freshness.isNotBlank()) {
                            Text("Freshness: ${product.freshness}")
                        }
                        Text("Origin: ${product.origin}")
                        Text("Category: ${product.category}")
                        Text("Seller: ${vendor?.name ?: product.vendorName}")
                        Text("Phone: ${product.vendorPhone}")
                    }
                }
                Text(text = "Description", fontWeight = FontWeight.Bold)
                Text(text = product.description.ifBlank { "No description provided." }, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (product.soldOut) {
                    AnimatedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    ) {
                        Text("Sold Out")
                    }
                } else {
                    AnimatedButton(
                        onClick = {
                            openWhatsApp(context, product)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chat on WhatsApp")
                    }
                }

                if (MarketplaceStore.currentUser?.role == UserRole.CUSTOMER) {
                    AnimatedOutlinedButton(
                        onClick = { showReviewDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Write a Review")
                    }
                }

                HorizontalDivider()
                Text(text = "Customer Reviews", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                if (recentReviews.isEmpty()) {
                    Text(text = "No reviews yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recentReviews.take(5).forEach { review ->
                            ReviewCard(review = review)
                        }
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                MarketplaceStore.addReview(productId, rating, comment)
                showReviewDialog = false
                Toast.makeText(context, "Review submitted", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ReviewDialog(onDismiss: () -> Unit, onSubmit: (Double, String) -> Unit) {
    var rating by rememberSaveable { mutableStateOf(4.0f) }
    var comment by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Rating: ${rating.roundToInt()} / 5")
                androidx.compose.material3.Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..5f,
                    steps = 3
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it.take(300) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Comment") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(rating.toDouble(), comment.trim()) }) { Text("Submit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedClickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MarketplaceImage(
                label = product.name,
                subtitle = product.category,
                imageUrl = product.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )
            Text(text = product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "₹${formatPrice(product.price)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(text = "★ ${formatRating(product.averageRating)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = product.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(review.customerName, fontWeight = FontWeight.Bold)
            Text(text = "★ ${formatRating(review.rating)}", color = MaterialTheme.colorScheme.primary)
            Text(text = review.comment)
            Text(text = review.productName, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Text(text = timeAgo(review.timestamp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack && onBack != null) {
                Box(modifier = Modifier.padding(start = 8.dp)) {
                    BackArrowButton(onClick = onBack)
                }
            }
        },
        actions = {
            if (onLogout != null) {
                TextButton(onClick = onLogout) { Text("Logout") }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun MarketDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun ExposedDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().clickable { onExpandedChange(true) },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { Text("▾") }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onExpandedChange(true) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun Modifier.animatedClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "card_press"
    )
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        )
}

@Composable
private fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "button_press"
    )
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
private fun AnimatedOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "outlined_button_press"
    )
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
private fun MarketplaceImage(
    label: String,
    subtitle: String,
    imageUrl: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageModel = remember(imageUrl) { resolveImageModel(context, imageUrl) }

    Card(modifier = modifier, shape = RoundedCornerShape(18.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = label, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                    if (subtitle.isNotBlank()) {
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(text = "No image", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun resolveImageModel(context: Context, imageUrl: String): Any? {
    if (imageUrl.isBlank()) return null
    val trimmed = imageUrl.trim()
    return when {
        trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) -> trimmed
        trimmed.startsWith("content://", ignoreCase = true) || trimmed.startsWith("file://", ignoreCase = true) -> Uri.parse(trimmed)
        else -> {
            val candidates = listOf(
                File(trimmed),
                File(context.filesDir, trimmed),
                File(context.filesDir, "product_images/$trimmed"),
                File(context.filesDir, "product_images/${File(trimmed).name}")
            )
            candidates.firstOrNull { it.exists() } ?: Uri.parse(trimmed)
        }
    }
}

private fun openWhatsApp(context: Context, product: Product) {
    val phone = product.vendorPhone.filter(Char::isDigit)
    val message = "Hi, I'm interested in ${product.name}. Is it available?"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}

private fun formatPrice(price: Double): String = DecimalFormat("0.##").format(price)

private fun formatRating(rating: Double): String = DecimalFormat("0.0").format(rating)

private fun timeAgo(date: Date): String {
    val diff = System.currentTimeMillis() - date.time
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
        diff < 604_800_000 -> "${diff / 86_400_000} days ago"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }
}

private suspend fun compressProductImage(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
    runCatching {
        val original = decodeSampledBitmap(context, sourceUri, 1280) ?: return@withContext null
        val maxSide = 1280
        val scale = minOf(1f, maxSide.toFloat() / maxOf(original.width, original.height).toFloat())
        val outputBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * scale).roundToInt().coerceAtLeast(1),
                (original.height * scale).roundToInt().coerceAtLeast(1),
                true
            )
        } else {
            original
        }
        val imageDir = File(context.filesDir, "product_images").apply { mkdirs() }
        val outputFile = File(imageDir, "product_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outputFile).use { stream ->
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 78, stream)
        }
        if (outputBitmap !== original && !outputBitmap.isRecycled) outputBitmap.recycle()
        if (!original.isRecycled) original.recycle()
        outputFile.absolutePath
    }.getOrNull()
}

private fun decodeSampledBitmap(context: Context, uri: Uri, maxSide: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    val sampleOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxSide)
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, sampleOptions)
    }
}

private fun calculateInSampleSize(width: Int, height: Int, maxSide: Int): Int {
    var sampleSize = 1
    var halfWidth = width / 2
    var halfHeight = height / 2
    while (halfWidth / sampleSize >= maxSide || halfHeight / sampleSize >= maxSide) {
        sampleSize *= 2
    }
    return sampleSize.coerceAtLeast(1)
}

