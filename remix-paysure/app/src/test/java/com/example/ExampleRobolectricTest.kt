package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.TrustLevel
import com.example.data.repository.PaysureRepository
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        assertEquals("Paysure", appName)
    }

    @Test
    fun `format amount in words correctly`() {
        assertEquals("Fifty thousand rupees", Formatters.amountInWords(50000))
        assertEquals("One thousand two hundred fifty rupees", Formatters.amountInWords(1250))
        assertEquals("Five hundred rupees", Formatters.amountInWords(500))
        assertEquals("Zero rupees", Formatters.amountInWords(0))
    }

    @Test
    fun `trust levels assign correctly based on transaction count`() {
        assertEquals(TrustLevel.NEW, TrustLevel.fromCount(0))
        assertEquals(TrustLevel.FAMILIAR, TrustLevel.fromCount(1))
        assertEquals(TrustLevel.FAMILIAR, TrustLevel.fromCount(2))
        assertEquals(TrustLevel.FREQUENT, TrustLevel.fromCount(3))
        assertEquals(TrustLevel.FREQUENT, TrustLevel.fromCount(5))
        assertEquals(TrustLevel.HIGHLY_TRUSTED, TrustLevel.fromCount(6))
        assertEquals(TrustLevel.HIGHLY_TRUSTED, TrustLevel.fromCount(12))
    }

    @Test
    fun `spend pattern detects unusually high amount`() {
        val repo = PaysureRepository()
        val contact = repo.contacts.value.first { it.name == "Ramesh Kumar" } // typical ~180
        val alert = repo.evaluateSpendPattern(contact, 1800.0)
        assertNotNull(alert)
        assertTrue(alert!!.contains("10x your usual payment"))
    }
}
