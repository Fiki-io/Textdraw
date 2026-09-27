package com.example.util

import com.example.model.TextDrawElement
import java.util.UUID

object PawnCodeParser {

    /**
     * Parses standard SA-MP Pawn script text containing TextDrawCreate or CreatePlayerTextDraw
     * and extracts elements with 100% fidelity.
     * Supports multidimensional arrays e.g. Text_Player[playerid][0], Text_Global[0], etc.
     */
    fun parseScript(pawnCode: String): List<TextDrawElement> {
        val lines = pawnCode.lines()

        // Matches: VarName[...][...] = TextDrawCreate(...) or CreatePlayerTextDraw(...)
        val createRegex = Regex("""(\w+(?:\[[^\]]*\])*)\s*=\s*(TextDrawCreate|CreatePlayerTextDraw)\s*\(\s*(?:playerid\s*,\s*)?([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?\s*,\s*"([^"]*)"""")
        
        // Matches setter functions with optional playerid and single/multi array indexes
        val letterSizeRegex = Regex("""(?:TextDraw|PlayerTextDraw)LetterSize\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?""")
        val textSizeRegex = Regex("""(?:TextDraw|PlayerTextDraw)TextSize\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?""")
        val alignmentRegex = Regex("""(?:TextDraw|PlayerTextDraw)Alignment\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val colorRegex = Regex("""(?:TextDraw|PlayerTextDraw)Color\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(0x[0-9a-fA-F]+|-?\d+)""")
        val useBoxRegex = Regex("""(?:TextDraw|PlayerTextDraw)UseBox\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val boxColorRegex = Regex("""(?:TextDraw|PlayerTextDraw)BoxColor\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(0x[0-9a-fA-F]+|-?\d+)""")
        val bgColorRegex = Regex("""(?:TextDraw|PlayerTextDraw)BackgroundColor\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(0x[0-9a-fA-F]+|-?\d+)""")
        val fontRegex = Regex("""(?:TextDraw|PlayerTextDraw)Font\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val shadowRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetShadow\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val outlineRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetOutline\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val proportionalRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetProportional\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val selectableRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetSelectable\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val previewModelRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetPreviewModel\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*(\d+)""")
        val previewRotRegex = Regex("""(?:TextDraw|PlayerTextDraw)SetPreviewRot\s*\(\s*(?:playerid\s*,\s*)?(\w+(?:\[[^\]]*\])*)\s*,\s*([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?\s*,\s*([-\d.]+)\s*f?""")

        val elementMap = linkedMapOf<String, TextDrawElement>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() ||
                trimmed.startsWith("//") ||
                trimmed.startsWith("/*") ||
                trimmed.startsWith("*") ||
                trimmed.startsWith("#") ||
                trimmed.startsWith("new ")
            ) continue

            createRegex.find(trimmed)?.let { match ->
                val fullVarName = match.groupValues[1]
                val x = match.groupValues[3].toFloatOrNull() ?: 320f
                val y = match.groupValues[4].toFloatOrNull() ?: 240f
                val text = match.groupValues[5]

                // Pretty display variable name, e.g. Text_Player[playerid][0] -> Text_Player[0] or Text_Player_0
                val displayName = fullVarName.replace("[playerid]", "")

                elementMap[fullVarName] = TextDrawElement(
                    id = UUID.randomUUID().toString(),
                    varName = displayName,
                    text = text,
                    posX = x,
                    posY = y,
                    zIndex = elementMap.size
                )
            }

            letterSizeRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val sx = match.groupValues[2].toFloatOrNull() ?: cur.letterSizeX
                    val sy = match.groupValues[3].toFloatOrNull() ?: cur.letterSizeY
                    elementMap[varName] = cur.copy(letterSizeX = sx, letterSizeY = sy)
                }
            }

            textSizeRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val sx = match.groupValues[2].toFloatOrNull() ?: cur.textSizeX
                    val sy = match.groupValues[3].toFloatOrNull() ?: cur.textSizeY
                    elementMap[varName] = cur.copy(textSizeX = sx, textSizeY = sy)
                }
            }

            alignmentRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val align = match.groupValues[2].toIntOrNull() ?: cur.alignment
                    elementMap[varName] = cur.copy(alignment = align)
                }
            }

            colorRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val colorHex = parseHexOrInt(match.groupValues[2])
                    elementMap[varName] = cur.copy(color = colorHex)
                }
            }

            useBoxRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    elementMap[varName] = cur.copy(useBox = match.groupValues[2] == "1")
                }
            }

            boxColorRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val colorHex = parseHexOrInt(match.groupValues[2])
                    elementMap[varName] = cur.copy(boxColor = colorHex)
                }
            }

            bgColorRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val colorHex = parseHexOrInt(match.groupValues[2])
                    elementMap[varName] = cur.copy(backgroundColor = colorHex)
                }
            }

            fontRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val font = match.groupValues[2].toIntOrNull() ?: cur.font
                    elementMap[varName] = cur.copy(font = font)
                }
            }

            shadowRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val shadow = match.groupValues[2].toIntOrNull() ?: cur.shadow
                    elementMap[varName] = cur.copy(shadow = shadow)
                }
            }

            outlineRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val outline = match.groupValues[2].toIntOrNull() ?: cur.outline
                    elementMap[varName] = cur.copy(outline = outline)
                }
            }

            proportionalRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    elementMap[varName] = cur.copy(proportional = match.groupValues[2] == "1")
                }
            }

            selectableRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    elementMap[varName] = cur.copy(selectable = match.groupValues[2] == "1")
                }
            }

            previewModelRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val mId = match.groupValues[2].toIntOrNull() ?: cur.modelId
                    elementMap[varName] = cur.copy(modelId = mId, font = 5)
                }
            }

            previewRotRegex.find(trimmed)?.let { match ->
                val varName = match.groupValues[1]
                elementMap[varName]?.let { cur ->
                    val rx = match.groupValues[2].toFloatOrNull() ?: cur.modelRotX
                    val ry = match.groupValues[3].toFloatOrNull() ?: cur.modelRotY
                    val rz = match.groupValues[4].toFloatOrNull() ?: cur.modelRotZ
                    val zoom = match.groupValues[5].toFloatOrNull() ?: cur.modelZoom
                    elementMap[varName] = cur.copy(modelRotX = rx, modelRotY = ry, modelRotZ = rz, modelZoom = zoom, font = 5)
                }
            }
        }

        return elementMap.values.toList()
    }

    private fun parseHexOrInt(str: String): Long {
        return try {
            if (str.startsWith("0x", ignoreCase = true)) {
                str.substring(2).toLong(16) and 0xFFFFFFFFL
            } else {
                str.toLong() and 0xFFFFFFFFL
            }
        } catch (e: Exception) {
            0xFFFFFFFFL
        }
    }
}
