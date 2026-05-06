package com.example.banksmsreader

import java.text.SimpleDateFormat
import java.util.*

data class BankSmsMessage(
    val id: Long,
    val address: String,
    val body: String,
    val date: Long,
    val type: Int
) {

    private val commonBanks = listOf(
        "HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "YESBANK", "PNB", "CANARA",
        "IDBI", "INDUSIND", "UNION", "BOB", "BOI", "HSBC", "CITI"
    )

    fun isBankTransaction(): Boolean {
        val addressLower = address.lowercase()
        val bodyLower = body.lowercase()

        val isFromBank = commonBanks.any { bank ->
            addressLower.contains(bank.lowercase()) ||
                    bodyLower.contains("${bank.lowercase()} bank") ||
                    bodyLower.contains("${bank.lowercase()} account")
        }

        val transactionKeywords = listOf(
            "debited", "credited", "spent", "paid", "purchase", "transaction",
            "withdrawn", "deposited", "account", "balance", "atm", "card",
            "upi", "imps", "neft", "rtgs", "trf", "transfer",
            "txn", "payment", "sent", "received"
        )

        val hasTransactionKeyword = transactionKeywords.any { bodyLower.contains(it) }
        val hasAmount = hasAmountPattern()

        return (isFromBank || hasTransactionKeyword) && hasAmount
    }

    private fun hasAmountPattern(): Boolean {
        val amountPatterns = listOf(
            "rs\\.?\\s*\\d+(?:[.,]\\d+)?".toRegex(RegexOption.IGNORE_CASE),
            "inr\\s*\\d+(?:[.,]\\d+)?".toRegex(RegexOption.IGNORE_CASE),
            "amt\\s*(?:rs\\.?)?\\s*\\d+(?:[.,]\\d+)?".toRegex(RegexOption.IGNORE_CASE),
            "[₹]\\s*\\d+(?:[.,]\\d+)?".toRegex(),
            "\\d+(?:[.,]\\d+)?\\s*(?:rs|inr)".toRegex(RegexOption.IGNORE_CASE)
        )
        return amountPatterns.any { it.containsMatchIn(body) }
    }

    fun parseAmount(): Double? {
        val patterns = listOf(
            "rs\\.?\\s*(\\d+(?:[.,]\\d+)?)".toRegex(RegexOption.IGNORE_CASE),
            "inr\\s*(\\d+(?:[.,]\\d+)?)".toRegex(RegexOption.IGNORE_CASE),
            "credited with\\s*(?:rs\\.?)?\\s*(\\d+(?:[.,]\\d+)?)".toRegex(RegexOption.IGNORE_CASE),
            "debited with\\s*(?:rs\\.?)?\\s*(\\d+(?:[.,]\\d+)?)".toRegex(RegexOption.IGNORE_CASE),
            "amt\\s*(?:rs\\.?)?\\s*(\\d+(?:[.,]\\d+)?)".toRegex(RegexOption.IGNORE_CASE),
            "[₹]\\s*(\\d+(?:[.,]\\d+)?)".toRegex(),
            "(\\d+(?:[.,]\\d+)?)\\s*(?:rs|inr)".toRegex(RegexOption.IGNORE_CASE)
        )

        for (pattern in patterns) {
            val match = pattern.find(body)
            match?.let {
                try {
                    val amountStr = it.groupValues[1].replace(",", "").replace(" ", "")
                    return amountStr.toDoubleOrNull()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val numberPattern = "\\b(\\d+(?:[.,]\\d+)?)\\b".toRegex()
        val numbers = numberPattern.findAll(body).map { it.groupValues[1] }.toList()

        for (num in numbers) {
            try {
                val amount = num.replace(",", "").toDouble()
                if (amount in 10.0..1000000.0) return amount
            } catch (e: Exception) {
                continue
            }
        }

        return null
    }

    fun parseMerchant(): String {
        val patterns = listOf(
            "at\\s+([A-Za-z0-9\\s&]+?)(?=\\s+on|\\s+amt|\\s+rs|\\.|$|\\n)".toRegex(RegexOption.IGNORE_CASE),
            "to\\s+([A-Za-z0-9\\s&]+?)(?=\\s+on|\\s+amt|\\s+rs|\\.|$|\\n)".toRegex(RegexOption.IGNORE_CASE),
            "from\\s+([A-Za-z0-9\\s&]+?)(?=\\s+on|\\s+amt|\\s+rs|\\.|$|\\n)".toRegex(RegexOption.IGNORE_CASE),
            "purchase at\\s+([A-Za-z0-9\\s&]+?)(?=\\s+on|\\s+amt|\\s+rs|\\.|$|\\n)".toRegex(RegexOption.IGNORE_CASE)
        )

        for (pattern in patterns) {
            val match = pattern.find(body)
            match?.let {
                val merchant = it.groupValues[1].trim()
                if (merchant.length > 2 && !merchant.contains(Regex("\\d"))) return merchant
            }
        }

        val commonMerchants = listOf(
            "AMAZON", "FLIPKART", "MYNTRA", "SWIGGY", "ZOMATO", "UBER", "OLA",
            "NETFLIX", "PRIME", "HOTSTAR", "SPOTIFY", "YOUTUBE"
        )

        val bodyUpper = body.uppercase()
        for (merchant in commonMerchants) {
            if (bodyUpper.contains(merchant)) return merchant
        }

        for (bank in commonBanks) {
            if (address.uppercase().contains(bank) || bodyUpper.contains(bank)) return bank
        }

        return address.takeIf { it.length > 3 && it.length < 15 } ?: "Unknown"
    }

    fun getCategory(): String {
        val bodyLower = body.lowercase()
        val merchantLower = parseMerchant().lowercase()

        return when {
            merchantLower.contains(Regex("swiggy|zomato|dominos|kfc|starbucks|restaurant")) ||
                    bodyLower.contains(Regex("food|restaurant|cafe|dinner|lunch")) -> "Food"

            merchantLower.contains(Regex("uber|ola|metro|fuel|petrol")) ||
                    bodyLower.contains(Regex("fuel|petrol|transport|travel")) -> "Transport"

            merchantLower.contains(Regex("amazon|flipkart|myntra")) ||
                    bodyLower.contains(Regex("shopping|purchase|order")) -> "Shopping"

            merchantLower.contains(Regex("netflix|prime|hotstar|spotify")) ||
                    bodyLower.contains(Regex("movie|entertainment|subscription")) -> "Entertainment"

            bodyLower.contains(Regex("bill|electricity|water|gas|recharge")) -> "Utilities"

            bodyLower.contains(Regex("salary|credited.*salary")) -> "Income"

            else -> "Others"
        }
    }

    fun getTransactionType(): String {
        val bodyLower = body.lowercase()
        return when {
            bodyLower.contains(Regex("credited|received|deposited|salary|refund")) -> "credit"
            bodyLower.contains(Regex("debited|spent|paid|withdrawn|purchase|payment")) -> "debit"
            else -> "debit"
        }
    }

    fun getStatus(): String {
        val bodyLower = body.lowercase()
        return when {
            bodyLower.contains(Regex("pending|processing")) -> "pending"
            bodyLower.contains(Regex("failed|declined|error|blocked")) -> "flagged"
            else -> "completed"
        }
    }

    fun getFormattedDate(): String {
        return try {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(date))
        } catch (e: Exception) {
            "Unknown"
        }
    }
}