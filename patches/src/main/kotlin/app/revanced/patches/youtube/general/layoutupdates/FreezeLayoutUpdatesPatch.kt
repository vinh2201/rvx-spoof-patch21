package app.revanced.patches.youtube.general.layoutupdates

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.revanced.patches.youtube.utils.patch.PatchList.FREEZE_LAYOUT_UPDATES
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.settingsPatch
import app.revanced.util.fingerprint.matchOrThrow

private const val EXTENSION_CLASS_DESCRIPTOR = "$UTILS_PATH/FreezeLayoutUpdatesPatch;"

@Suppress("unused")
val freezeLayoutUpdatesPatch = bytecodePatch(
    FREEZE_LAYOUT_UPDATES.title,
    FREEZE_LAYOUT_UPDATES.summary
) {
    compatibleWith(COMPATIBLE_PACKAGE)
    dependsOn(settingsPatch)

    execute {
        // [BLOCK 1] Xử lý Hot Config
        hotConfigPreferenceFingerprint.matchOrThrow().let { match ->
            match.method.apply {
                val stringMatches = match.stringMatches!!

                // Hot Config Group (Dùng index của chuỗi match được + offset 3)
                val hotConfigGroupIndex = stringMatches.first { it.string == "com.google.android.libraries.youtube.innertube.hot_config_group" }.index
                addInstructions(hotConfigGroupIndex + 3, """
                    invoke-static {v1}, $EXTENSION_CLASS_DESCRIPTOR->getHotConfigGroup(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v1
                """)

                // Hot Hash Data (Dùng index của chuỗi match được + offset 2)
                val hotHashDataIndex = stringMatches.first { it.string == "com.google.android.libraries.youtube.innertube.hot_hash_data" }.index
                addInstructions(hotHashDataIndex + 2, """
                    invoke-static {v1}, $EXTENSION_CLASS_DESCRIPTOR->getHotHashData(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v1
                """)
            }
        }

        // [BLOCK 2] Xử lý Cold Config
        coldConfigPreferenceFingerprint.matchOrThrow().let { match ->
            match.method.apply {
                val stringMatches = match.stringMatches!!

                // Cold Config Group (Dùng index của chuỗi match được + offset 3)
                val coldConfigGroupIndex = stringMatches.first { it.string == "com.google.android.libraries.youtube.innertube.cold_config_group" }.index
                addInstructions(coldConfigGroupIndex + 3, """
                    invoke-static {v1}, $EXTENSION_CLASS_DESCRIPTOR->getColdConfigGroup(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v1
                """)

                // Cold Hash Data (Dùng index của chuỗi match được + offset 2)
                val coldHashDataIndex = stringMatches.first { it.string == "com.google.android.libraries.youtube.innertube.cold_hash_data" }.index
                addInstructions(coldHashDataIndex + 2, """
                    invoke-static {v1}, $EXTENSION_CLASS_DESCRIPTOR->getColdHashData(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v1
                """)
            }
        }

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: SPOOFING",
                "SETTINGS: FREEZE_LAYOUT_UPDATES"
            ),
            FREEZE_LAYOUT_UPDATES
        )
    }
}