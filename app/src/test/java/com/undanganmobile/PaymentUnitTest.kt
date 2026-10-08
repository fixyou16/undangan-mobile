package com.undanganmobile

import com.undanganmobile.data.PaymentRepository
import org.junit.Assert.*
import org.junit.Test

class PaymentUnitTest {

    @Test
    fun testPaymentMethodsAvailable() {
        val methods = PaymentRepository.paymentMethods
        assertTrue(methods.isNotEmpty())
        assertTrue(methods.any { it.id == "bca" })
        assertTrue(methods.any { it.id == "mandiri" })
        assertTrue(methods.any { it.id == "bri" })
        assertTrue(methods.any { it.id == "qris" })
    }

    @Test
    fun testFormatRupiah() {
        val formatted = PaymentRepository.formatRupiah(125000L)
        assertTrue(formatted.contains("125"))
        assertTrue(formatted.contains("Rp"))
    }
}
