
data class Player(val name: String, val score: Int)
class Top20Players{
    private val list: MutableList<Player> = mutableListOf()
    fun addPlayer(player: Player) {
        list.add(player)
        list.sortByDescending { it.score }

        if (list.size > 20) {
            list.removeAt(list.lastIndex)
        }
    }
    fun getTopPlayers(): List<Player> {
        return list.toList()
    }
    operator fun iterator(): Iterator<Player> {
        return list.iterator()
    }
    val size get() = list.size
}



object Scores {
    private val fileAccess = FileAccess("scores.txt")
    val list = Top20Players()

    fun readScores(){
        val map = fileAccess.readFile()
        for ((name, score) in map) {
            list.addPlayer(Player(name, score))
        }
    }

    fun writeScores() {
        fileAccess.writeFile(scores = list.getTopPlayers())
    }
}

fun main() {
    Scores.readScores()
    println(Scores.list.getTopPlayers())
}

