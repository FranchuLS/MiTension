package com.fxn.mitension.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.activity
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fxn.mitension.MainActivity
import com.fxn.mitension.R
import com.fxn.mitension.ui.AppNavigation
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MedicionScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavigation()
        }
    }

    @Test
    fun alPulsarVerCalendario_navegaACalendarioScreen() {
        val verCalendarioText = composeTestRule.activity.getString(R.string.ver_calendario)
        composeTestRule.onNodeWithText(verCalendarioText).assertIsDisplayed()
        composeTestRule.onNodeWithText(verCalendarioText).performClick()
        val rutaActual = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals("calendario", rutaActual)
    }

    @Test
    fun alAbrirDialog_yConfirmar_valorApareceEnPantalla() {
        val pulsaParaAnadirText = composeTestRule.activity.getString(R.string.pulsa_para_anadir)
        composeTestRule.onNodeWithText("La Alta (sistólica)").assertIsDisplayed()
        composeTestRule.onAllNodesWithText(pulsaParaAnadirText)[0].performClick()
        val confirmarText = composeTestRule.activity.getString(R.string.confirmar)
        composeTestRule.onNodeWithText(confirmarText).assertIsDisplayed()
        composeTestRule.onNodeWithText(confirmarText).performClick()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
    }
}
