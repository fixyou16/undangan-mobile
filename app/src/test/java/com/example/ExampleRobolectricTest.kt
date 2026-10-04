package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.repository.InvitationSaaSRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("UndanganKu", appName)
  }

  @Test
  fun `database seeding and order creation test`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val repository = InvitationSaaSRepository(db)

    repository.seedInitialDataIfEmpty()

    val user = repository.getUserById(1)
    assertNotNull(user)
    assertEquals("super_admin", user?.role)

    val themes = repository.getThemeById(1)
    assertNotNull(themes)
    assertEquals("Royal Javanese Luxury", themes?.name)

    // Test order creation
    val newOrder = repository.createOrderAndDraftInvitation(
      customerId = 3L,
      resellerId = 2L,
      themeId = 1L,
      groomName = "Fajar",
      brideName = "Dina",
      eventAt = System.currentTimeMillis() + 86400000L,
      venue = "Hotel Sahid",
      address = "Jakarta",
      mapsUrl = null
    )
    assertNotNull(newOrder)
    assertEquals("pending", newOrder.paymentStatus)

    // Test idempotent webhook payment simulation
    val paidResult = repository.processWebhookPayment(newOrder.orderNumber, isSuccess = true)
    assertTrue(paidResult)

    val updatedOrder = repository.getOrderById(newOrder.id)
    assertEquals("paid", updatedOrder?.paymentStatus)
    assertEquals("completed", updatedOrder?.status)
  }
}
