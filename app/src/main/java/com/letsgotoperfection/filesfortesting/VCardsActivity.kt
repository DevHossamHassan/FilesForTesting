package com.letsgotoperfection.filesfortesting

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.VCardVersion
import ezvcard.VCardVersion.V2_1
import ezvcard.VCardVersion.V3_0
import ezvcard.VCardVersion.V4_0
import ezvcard.property.StructuredName


class VCardsActivity : AppCompatActivity() {
    lateinit var btnV4VCardGenerator: Button
    lateinit var btnV3VCardGenerator: Button
    lateinit var btnV21VCardGenerator: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FilesForTesting_Dark)
        setContentView(R.layout.activity_vcards)
        btnV4VCardGenerator = findViewById(R.id.btnGenerateVCardV4)
        btnV3VCardGenerator = findViewById(R.id.btnGenerateVCardV3)
        btnV21VCardGenerator = findViewById(R.id.btnGenerateVCardV21)
        btnV4VCardGenerator.setOnClickListener {
            v4VCard()
        }
        btnV3VCardGenerator.setOnClickListener {
            v3VCard()
        }
        btnV21VCardGenerator.setOnClickListener {
            v21VCard()
        }
    }

    private fun v4VCard() {
        vCard(V4_0)
    }

    private fun v3VCard() {
        vCard(V3_0)
    }

    private fun v21VCard() {
        vCard(V2_1)
    }

    private fun vCard(version: VCardVersion = V4_0) {
        val vcard = VCard()
        val n = StructuredName()
        n.family = "Doe"
        n.given = "Jonathan"
        n.prefixes.add("Mr")
        vcard.structuredName = n

        vcard.setFormattedName("John Doe")

        val str = Ezvcard.write(vcard).version(version).go()

        Utils(this).writeAttachment() { out ->
            str.byteInputStream().copyTo(out)
        }
        Toast.makeText(this, "Done created VCards version $version", Toast.LENGTH_SHORT).show()
    }
}