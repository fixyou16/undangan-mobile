package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE referredByResellerId = :resellerId")
    fun getCustomersByReseller(resellerId: Long): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface ThemeDao {
    @Query("SELECT * FROM themes ORDER BY id ASC")
    fun getAllThemes(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE isActive = 1 ORDER BY id ASC")
    fun getActiveThemes(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: Long): ThemeEntity?

    @Query("SELECT * FROM themes WHERE slug = :slug LIMIT 1")
    suspend fun getThemeBySlug(slug: String): ThemeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<ThemeEntity>)

    @Update
    suspend fun updateTheme(theme: ThemeEntity)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByCustomer(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE resellerId = :resellerId ORDER BY createdAt DESC")
    fun getOrdersByReseller(resellerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)
}

@Dao
interface InvitationDao {
    @Query("SELECT * FROM invitations ORDER BY id DESC")
    fun getAllInvitations(): Flow<List<InvitationEntity>>

    @Query("SELECT * FROM invitations WHERE orderId = :orderId LIMIT 1")
    suspend fun getInvitationByOrderId(orderId: Long): InvitationEntity?

    @Query("SELECT * FROM invitations WHERE slug = :slug LIMIT 1")
    suspend fun getInvitationBySlug(slug: String): InvitationEntity?

    @Query("SELECT * FROM invitations WHERE slug = :slug LIMIT 1")
    fun observeInvitationBySlug(slug: String): Flow<InvitationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: InvitationEntity): Long

    @Update
    suspend fun updateInvitation(invitation: InvitationEntity)
}

@Dao
interface GuestDao {
    @Query("SELECT * FROM guests WHERE invitationId = :invitationId ORDER BY id DESC")
    fun getGuestsByInvitation(invitationId: Long): Flow<List<GuestEntity>>

    @Query("SELECT * FROM guests WHERE token = :token LIMIT 1")
    suspend fun getGuestByToken(token: String): GuestEntity?

    @Query("SELECT * FROM guests WHERE invitationId = :invitationId AND token = :token LIMIT 1")
    suspend fun getGuestByInvitationAndToken(invitationId: Long, token: String): GuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: GuestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuests(guests: List<GuestEntity>)

    @Update
    suspend fun updateGuest(guest: GuestEntity)

    @Delete
    suspend fun deleteGuest(guest: GuestEntity)
}

@Dao
interface GreetingDao {
    @Query("SELECT * FROM greetings WHERE invitationId = :invitationId ORDER BY createdAt DESC")
    fun getGreetingsByInvitation(invitationId: Long): Flow<List<GreetingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGreeting(greeting: GreetingEntity): Long
}

@Dao
interface PaymentEventDao {
    @Query("SELECT * FROM payment_events ORDER BY receivedAt DESC")
    fun getAllEvents(): Flow<List<PaymentEventEntity>>

    @Query("SELECT * FROM payment_events WHERE eventId = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): PaymentEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentEvent(event: PaymentEventEntity): Long
}
