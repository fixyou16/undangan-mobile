package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val role: String, // "super_admin", "reseller", "customer"
    val referredByResellerId: Long? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "themes")
data class ThemeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val slug: String,
    val description: String?,
    val previewImage: String?,
    val price: Long, // IDR rupiah
    val category: String = "Modern",
    val isActive: Boolean = true,
    val accentColorHex: String = "#871A5B",
    val features: String = "Musik Otomatis, RSVP Online, Buku Tamu, Amplop Digital, Galeri Foto"
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String, // ORD-ULID/UUID
    val userId: Long,
    val resellerId: Long? = null,
    val themeId: Long,
    val amount: Long, // IDR rupiah snapshot
    val currency: String = "IDR",
    val status: String = "pending", // "pending", "in_progress", "completed", "cancelled"
    val paymentStatus: String = "pending", // "pending", "paid", "failed", "expired", "refunded"
    val paymentProvider: String? = null, // "Midtrans"
    val paymentReference: String? = null,
    val paymentUrl: String? = null,
    val paidAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "invitations")
data class InvitationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val slug: String, // /i/{slug}
    val groomName: String,
    val brideName: String,
    val groomBio: String = "Putra dari Bpk. Bambang & Ibu Sri",
    val brideBio: String = "Putri dari Bpk. Hendra & Ibu Dewi",
    val eventAt: Long, // Epoch timestamp in ms
    val venue: String,
    val address: String? = null,
    val mapsUrl: String? = null,
    val story: String? = null,
    val quote: String? = "Dan di antara tanda-tanda kebesaran-Nya ialah Dia menciptakan pasangan-pasangan untukmu dari jenismu sendiri, agar kamu cenderung dan merasa tenteram kepadanya (QS. Ar-Rum: 21)",
    val galleryJson: String? = null, // JSON string or comma-separated list of image urls/descriptions
    val themeColorHex: String = "#871A5B",
    val musicTitle: String = "Janji Suci - Romantic Acoustic",
    val bankName: String = "BCA",
    val bankAccount: String = "8720192831",
    val bankHolder: String = "Fajar Pratama",
    val isPublished: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invitationId: Long,
    val name: String,
    val phone: String? = null,
    val token: String = UUID.randomUUID().toString().substring(0, 8),
    val rsvpStatus: String = "pending", // "pending", "attending", "declined"
    val guestCount: Int = 1,
    val message: String? = null,
    val respondedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "greetings")
data class GreetingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invitationId: Long,
    val guestName: String,
    val message: String,
    val statusAttendance: String = "attending", // "attending", "declined", "pending"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_events")
data class PaymentEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: String,
    val orderNumber: String,
    val provider: String = "Midtrans",
    val grossAmount: Long,
    val status: String,
    val signatureVerified: Boolean = true,
    val receivedAt: Long = System.currentTimeMillis()
)
