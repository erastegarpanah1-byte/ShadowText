package ai.zaro.shadowtext.core.engine

interface CarrierTextProvider {
    fun provide(): String
}

class GeneratedCarrierTextProvider(
    private val minSentences: Int = 4,
) : CarrierTextProvider {

    override fun provide(): String {
        val sentences = listOf(
            "The quick brown fox jumps over the lazy dog near the riverbank.",
            "Technology advances at a pace that transforms every aspect of modern life.",
            "A well-designed system prioritizes both performance and maintainability.",
            "The most elegant solutions arise from clear and focused thinking.",
            "Data should flow through well-defined interfaces between independent modules.",
            "Every great product starts with a deep understanding of user needs.",
            "Simplicity is the ultimate sophistication in software architecture.",
            "Reading is to the mind what exercise is to the body.",
            "Innovation distinguishes between a leader and a follower.",
            "The best way to predict the future is to create it yourself.",
            "Clean code always looks like it was written by someone who cares.",
            "First solve the problem, then write the code to implement the solution.",
            "A picture is worth a thousand words, but a good interface is worth a thousand clicks.",
            "The only way to do great work is to love what you do and keep learning.",
            "Debugging is twice as hard as writing the code in the first place.",
        )
        val sb = StringBuilder()
        val count = maxOf(minSentences, 4)
        val shuffled = sentences.shuffled()
        for (i in 0 until count) {
            if (i > 0) sb.append(' ')
            sb.append(shuffled[i % shuffled.size])
        }
        return sb.toString()
    }
}

class EmptyCarrierTextProvider : CarrierTextProvider {
    override fun provide(): String = ""
}
