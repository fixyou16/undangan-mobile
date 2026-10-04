package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuestEntity
import com.example.data.model.InvitationEntity
import com.example.ui.MainViewModel

@Composable
fun CustomerGuestManagerScreen(
    viewModel: MainViewModel,
    orderId: Long,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var invitation by remember { mutableStateOf<InvitationEntity?>(null) }
    var guests by remember { mutableStateOf<List<GuestEntity>>(emptyList()) }
    var filterStatus by remember { mutableStateOf("all") } // "all", "attending", "declined", "pending"

    var showAddGuestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        val inv = viewModel.repository.getInvitationByOrderId(orderId)
        invitation = inv
        if (inv != null) {
            viewModel.repository.getGuestsByInvitation(inv.id).collect {
                guests = it
            }
        }
    }

    val invite = invitation
    if (invite == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val filteredGuests = remember(guests, filterStatus) {
        when (filterStatus) {
            "attending" -> guests.filter { it.rsvpStatus == "attending" }
            "declined" -> guests.filter { it.rsvpStatus == "declined" }
            "pending" -> guests.filter { it.rsvpStatus == "pending" }
            else -> guests
        }
    }

    val attendingCount = guests.filter { it.rsvpStatus == "attending" }.sumOf { it.guestCount }
    val totalGuests = guests.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("guest_manager_screen")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manajemen Tamu & WhatsApp",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Kelola daftar undangan & token RSVP privat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddGuestDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_guest_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats Box
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Undangan", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Text(
                            "$totalGuests Tamu",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Konfirmasi Hadir", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Text(
                            "$attendingCount Orang",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "all",
                    onClick = { filterStatus = "all" },
                    label = { Text("Semua ($totalGuests)") },
                    modifier = Modifier.testTag("filter_all_guests")
                )
                FilterChip(
                    selected = filterStatus == "attending",
                    onClick = { filterStatus = "attending" },
                    label = { Text("Hadir (${guests.count { it.rsvpStatus == "attending" }})") },
                    modifier = Modifier.testTag("filter_attending_guests")
                )
                FilterChip(
                    selected = filterStatus == "pending",
                    onClick = { filterStatus = "pending" },
                    label = { Text("Belum (${guests.count { it.rsvpStatus == "pending" }})") },
                    modifier = Modifier.testTag("filter_pending_guests")
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (filteredGuests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ada tamu dalam kategori ini.", color = Color.Gray)
                    }
                }
            }
        } else {
            items(filteredGuests) { guest ->
                GuestCardItem(
                    guest = guest,
                    invitation = invite,
                    onSendWhatsApp = {
                        val message = viewModel.repository.generateWhatsAppInvitationMessage(guest, invite)
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                            if (!guest.phone.isNullOrBlank()) {
                                putExtra("jid", "${guest.phone.replace("+", "")}@s.whatsapp.net")
                            }
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Kirim Undangan WhatsApp ke ${guest.name}")
                        context.startActivity(shareIntent)
                    },
                    onCopyLink = {
                        val link = "https://undangan.id/i/${invite.slug}?to=${Uri.encode(guest.name)}&token=${guest.token}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Link Undangan", link)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showSnackbar("Tautan undangan untuk ${guest.name} telah disalin!")
                    },
                    onDelete = {
                        viewModel.deleteGuest(guest)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    if (showAddGuestDialog) {
        var newGuestName by remember { mutableStateOf("") }
        var newGuestPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddGuestDialog = false },
            title = { Text("Tambah Tamu Undangan", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newGuestName,
                        onValueChange = { newGuestName = it },
                        label = { Text("Nama Tamu *") },
                        placeholder = { Text("Contoh: Bpk. H. Rahmat & Partner") },
                        modifier = Modifier.fillMaxWidth().testTag("add_guest_name_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newGuestPhone,
                        onValueChange = { newGuestPhone = it },
                        label = { Text("No. WhatsApp (Opsional)") },
                        placeholder = { Text("Contoh: +628123456789") },
                        modifier = Modifier.fillMaxWidth().testTag("add_guest_phone_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGuestName.isNotBlank()) {
                            viewModel.addGuest(invite.id, newGuestName.trim(), newGuestPhone.trim().ifBlank { null })
                            showAddGuestDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_guest_button")
                ) {
                    Text("Simpan Tamu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGuestDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun GuestCardItem(
    guest: GuestEntity,
    invitation: InvitationEntity,
    onSendWhatsApp: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guest_item_${guest.token}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = guest.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Token: ${guest.token} • ${guest.phone ?: "Tanpa No. HP"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (guest.rsvpStatus) {
                        "attending" -> Color(0xFFE8F5E9)
                        "declined" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF8E1)
                    }
                ) {
                    Text(
                        text = when (guest.rsvpStatus) {
                            "attending" -> "Hadir (${guest.guestCount})"
                            "declined" -> "Tidak Hadir"
                            else -> "Belum Konfirmasi"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (guest.rsvpStatus) {
                                "attending" -> Color(0xFF2E7D32)
                                "declined" -> Color(0xFFC62828)
                                else -> Color(0xFFB45309)
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (!guest.message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${guest.message}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.DarkGray,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Actions: WhatsApp, Copy Link, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSendWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("send_wa_button_${guest.token}")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kirim WA", fontSize = 11.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onCopyLink,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("copy_link_button_${guest.token}")
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin Link", fontSize = 11.sp)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_guest_button_${guest.token}")
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
