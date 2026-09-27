package com.example

import com.example.model.TextDrawElement
import com.example.model.TextDrawProject
import com.example.util.PawnCodeGenerator
import com.example.util.PawnCodeParser
import com.example.util.SampColorUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TextDrawPawnUnitTest {

    @Test
    fun testColorConversion() {
        val sampWhite = 0xFFFFFFFFL
        val color = SampColorUtils.sampHexToComposeColor(sampWhite)
        val convertedBack = SampColorUtils.composeColorToSampHex(color)
        assertEquals(sampWhite, convertedBack)

        val hexStr = SampColorUtils.formatToSampPawnHex(0x33CC33FFL)
        assertEquals("0x33CC33FF", hexStr)
    }

    @Test
    fun testPawnCodeGeneratorAndParserRoundtrip() {
        val project = TextDrawProject(name = "TestProject", isPlayerTextDraw = false)
        val elements = listOf(
            TextDrawElement(
                id = "1",
                varName = "TD_Speedo",
                text = "SPEED: 120 KM/H",
                font = 2,
                posX = 500f,
                posY = 400f,
                letterSizeX = 0.45f,
                letterSizeY = 1.8f,
                textSizeX = 120f,
                textSizeY = 25f,
                color = 0xFFFFFFFFL,
                useBox = true,
                boxColor = 0x00000080L,
                selectable = true
            )
        )

        val script = PawnCodeGenerator.generatePawnScript(project, elements)
        assertTrue(script.contains("TextDrawCreate(500.000f, 400.000f, \"SPEED: 120 KM/H\");"))
        assertTrue(script.contains("TextDrawFont(TD_Speedo, 2);"))

        val parsed = PawnCodeParser.parseScript(script)
        assertEquals(1, parsed.size)
        val first = parsed[0]
        assertEquals("TD_Speedo", first.varName)
        assertEquals("SPEED: 120 KM/H", first.text)
        assertEquals(2, first.font)
        assertEquals(500f, first.posX)
        assertEquals(400f, first.posY)
        assertTrue(first.useBox)
        assertTrue(first.selectable)
    }

    @Test
    fun testParseRealWorldUserScript() {
        val userSnippet = """
            new Text: Text_Global[1];

            Text_Global[0] = TextDrawCreate(595.000, 340.000, "250");
            TextDrawLetterSize(Text_Global[0], 0.359, 2.399);
            TextDrawTextSize(Text_Global[0], 0.000, 6.000);
            TextDrawAlignment(Text_Global[0], 1);
            TextDrawColor(Text_Global[0], -1);
            TextDrawSetShadow(Text_Global[0], 1);
            TextDrawSetOutline(Text_Global[0], 1);
            TextDrawBackgroundColor(Text_Global[0], 150);
            TextDrawFont(Text_Global[0], 3);
            TextDrawSetProportional(Text_Global[0], 1);

            ####################################################################################################

            new PlayerText: Text_Player[MAX_PLAYERS][16];

            Text_Player[playerid][0] = CreatePlayerTextDraw(playerid, 8.000, 242.000, "ld_bum:blkdot");
            PlayerTextDrawLetterSize(playerid, Text_Player[playerid][0], 0.550, 5.250);
            PlayerTextDrawTextSize(playerid, Text_Player[playerid][0], 61.000, 50.000);
            PlayerTextDrawAlignment(playerid, Text_Player[playerid][0], 2);
            PlayerTextDrawColor(playerid, Text_Player[playerid][0], 1887473919);
            PlayerTextDrawUseBox(playerid, Text_Player[playerid][0], 1);
            PlayerTextDrawBoxColor(playerid, Text_Player[playerid][0], 135);
            PlayerTextDrawSetShadow(playerid, Text_Player[playerid][0], 0);
            PlayerTextDrawSetOutline(playerid, Text_Player[playerid][0], 1);
            PlayerTextDrawBackgroundColor(playerid, Text_Player[playerid][0], 255);
            PlayerTextDrawFont(playerid, Text_Player[playerid][0], 4);
            PlayerTextDrawSetProportional(playerid, Text_Player[playerid][0], 1);

            Text_Player[playerid][7] = CreatePlayerTextDraw(playerid, 554.000, 362.000, "SADLER");
            PlayerTextDrawLetterSize(playerid, Text_Player[playerid][7], 0.190, 1.500);
            PlayerTextDrawAlignment(playerid, Text_Player[playerid][7], 1);
            PlayerTextDrawColor(playerid, Text_Player[playerid][7], -1);
            PlayerTextDrawSetShadow(playerid, Text_Player[playerid][7], 1);
            PlayerTextDrawSetOutline(playerid, Text_Player[playerid][7], 1);
            PlayerTextDrawBackgroundColor(playerid, Text_Player[playerid][7], 150);
            PlayerTextDrawFont(playerid, Text_Player[playerid][7], 2);
            PlayerTextDrawSetProportional(playerid, Text_Player[playerid][7], 1);
        """.trimIndent()

        val parsed = PawnCodeParser.parseScript(userSnippet)
        assertEquals(3, parsed.size)

        // Element 0: Global textdraw
        val globalTd = parsed[0]
        assertEquals("250", globalTd.text)
        assertEquals(3, globalTd.font)
        assertEquals(595f, globalTd.posX)
        assertEquals(340f, globalTd.posY)
        assertEquals(0.359f, globalTd.letterSizeX)
        assertEquals(0xFFFFFFFFL, globalTd.color)

        // Element 1: Player sprite
        val playerSprite = parsed[1]
        assertEquals("ld_bum:blkdot", playerSprite.text)
        assertEquals(4, playerSprite.font)
        assertEquals(8f, playerSprite.posX)
        assertEquals(242f, playerSprite.posY)
        assertEquals(61f, playerSprite.textSizeX)
        assertEquals(50f, playerSprite.textSizeY)
        assertEquals(1887473919L, playerSprite.color)
        assertTrue(playerSprite.useBox)

        // Element 2: Sadler car text
        val sadler = parsed[2]
        assertEquals("SADLER", sadler.text)
        assertEquals(2, sadler.font)
        assertEquals(554f, sadler.posX)
        assertEquals(362f, sadler.posY)
    }
}
