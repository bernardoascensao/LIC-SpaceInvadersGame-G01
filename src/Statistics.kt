object Statistics {

    var coins = 0
    var numberOfGames = 0
    private val fileAccess = FileAccess("CoinsAndNumOfGames.txt")

    fun readStatistics(){
        val map = fileAccess.readFile()
        for ((key, value) in map) {
            when (key) {
                "coins" -> coins = value
                "numberOfGames" -> numberOfGames = value
            }
        }
    }

    fun writeStatistics() {
        val map = mutableMapOf<String, Int>()
        map["coins"] = coins
        map["numberOfGames"] = numberOfGames
        fileAccess.writeFile(statistics = map)
    }
}