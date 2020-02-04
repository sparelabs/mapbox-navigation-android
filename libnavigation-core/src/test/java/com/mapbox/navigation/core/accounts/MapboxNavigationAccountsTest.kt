package com.mapbox.navigation.core.accounts

import org.junit.Assert.*

import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.mapbox.android.accounts.navigation.sku.v1.TripsSku
import com.mapbox.android.accounts.v1.AccountsConstants
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class MapboxNavigationAccountsTest {

    val ctx = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        ctx.applicationInfo.metaData = Bundle().also {
            it.putBoolean(AccountsConstants.KEY_META_DATA_MANAGE_SKU, true)
        }

        ctx.getSharedPreferences(
                AccountsConstants.MAPBOX_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
        ).edit().putString("com.mapbox.navigation.accounts.trips.skutoken", "myTestToken")
                .commit()


        ctx.getSharedPreferences(
                AccountsConstants.MAPBOX_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
        ).edit().putString("com.mapbox.navigation.accounts.mau.skutoken", "myTestToken")
                .commit()
    }

    @After
    fun tearDown() {
        ctx.applicationInfo.metaData = null
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_null() {


        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken(null, 4)

        assertNotNull(result)
        //assertThat(result, `is`("myTestToken"))//107bkJpaRxZu9
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_empty() {
        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken("", 4)

        assertThat(result, `is`("myTestToken"))
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_and_querySize_lessThan_zero() {
        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken("http://www.mapbox.com", -1)

        assertThat(result, `is`("myTestToken"))
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_and_BillingModel_TRIPS() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()

//        ctx.applicationInfo.metaData = Bundle().also {
//            it.putBoolean(AccountsConstants.KEY_META_DATA_MANAGE_SKU, true)
//        }

        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken("http://www.mapbox.com", 5)

        assertThat(result, `is`("http://www.mapbox.com&sku=myTestToken"))
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_and_BillingModel_MAU() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()

        ctx.applicationInfo.metaData = Bundle().also {
            it.putBoolean(AccountsConstants.KEY_META_DATA_MANAGE_SKU, false)
        }

        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken("http://www.mapbox.com", 5)

        assertThat(result, `is`("http://www.mapbox.com&sku=myTestToken"))
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_and_BillingModel_default() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()

        val instance = MapboxNavigationAccounts.getInstance(ctx)

        val result = instance.obtainSkuToken("http://www.mapbox.com", 5)

        assertThat(result, `is`("http://www.mapbox.com&sku=myTestToken"))
    }
}