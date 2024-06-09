import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class FileAccess(private val filePath: String) {


//    fun readFileOfCoinsAndGames( fileName: String ): Pair<Int, Int> {
//        val file = File(fileName)
//        var coins = 0
//        var numOfGames = 0
//
//        try {
//            val bufferedReader = BufferedReader(FileReader(file))
//            var line: String?
//
//            while (true) {
//                bufferedReader.readLine().also { line = it }
//                if (line == null){                                      //!!!!!!!!explicitar no relatorio que o primeiro parametro tem de ser coins ou o numero de jogos
//                    break                                               // como primeiro parametro apens esperamos essas duas strings e como segundo esperamos um Int
//                } else {
//                    val splited = line!!.split(";")
//
//                    if (splited[0].uppercase() == "NUMBEROFGAMES" && splited[1].isNotBlank())
//                        numOfGames = splited[1].toInt()
//
//                    else if (splited[0].uppercase() == "COINS" && splited[1].isNotBlank())
//                        coins = splited[1].toInt()
//
//                }
//            }
//            bufferedReader.close()
//
//        } catch (e: Exception) {
//            println(e)
//        }
//
//        return Pair<Int, Int>(coins, numOfGames)
//    }
//
//    fun readFileOfScores(fileName: String, scores: Top20Players){
//        val file = File(fileName)
//
//        try {
//            val bufferedReader = BufferedReader(FileReader(file))
//            var line: String?
//
//            // lê cada linha do arquivo enquanto houver conteúdo
//            while (true) {
//                bufferedReader.readLine().also { line = it }
//                if (line == null){
//                    break
//                }
//                else if (line!!.isBlank() || line!!.isEmpty()){                     //se uma linha estiver vazia e nao for o fim do ficheiro, vamos diretamente
//                    continue                                                        //para a próxima iteração
//                }
//                val splited = line!!.split(";")                         //!!!!!!temos de especificar no relatório que
//                scores.addPlayer( Player(splited[0], splited[1].toInt()) )        //o primeiro elemento é obrigatorio ser o nome do player e o segundo ser o score
//            }
//
//            bufferedReader.close() // fecha o BufferedReader após a leitura
//        } catch (e: Exception) {
//            println(e)
//        }
//    }

    fun readFile(fileName: String = filePath): Map<String, Int> {
        val file = File(fileName)
        val map: MutableMap<String, Int> = mutableMapOf()

        try {
            val bufferedReader = BufferedReader(FileReader(file))
            var line: String?

            // lê cada linha do arquivo enquanto houver conteúdo
            while (true) {
                bufferedReader.readLine().also { line = it }
                if (line == null){
                    break
                }
                else if (line!!.isBlank() || line!!.isEmpty()){
                    continue
                }

                val splited = line!!.split(";")

                if ((splited[0].uppercase() == "NUMBEROFGAMES" || splited[0].uppercase() == "COINS") && splited[1].isNotBlank())
                    map[splited[0]] = splited[1].toInt()

                else if (splited[0].isNotBlank() && splited[1].isNotBlank())
                    map[splited[0]] = splited[1].toInt()
            }

            bufferedReader.close() // fecha o BufferedReader após a leitura
        } catch (e: Exception) {
            println(e)
        }
        return map
    }

    fun writeFile(fileName: String = filePath, scores: List<Player>? = null, statistics: Map<String, Int>? = null) {
        val file = File(fileName)

        try {
            require(scores != null || statistics != null) { "scores or statistics must be not null" }

            val bufferedWriter = BufferedWriter(FileWriter(file))

            if(scores != null){
                for(player in scores){

                    val line = "${player.name};${player.score}"

                    bufferedWriter.write(line).also { bufferedWriter.newLine() }
                }
            } else if (statistics != null){
                for((key, value) in statistics){
                    val line = "$key;$value"
                    bufferedWriter.write(line).also { bufferedWriter.newLine() }
                }
            }

            bufferedWriter.close()
        } catch (e: Exception) {
            println(e)
        }
    }

//    fun writeFileScores(fileName: String, scores: Top20Players){
//        val file = File(fileName)
//
//        try {
//            val bufferedWriter = BufferedWriter(FileWriter(file))
//
//            for(player in scores){
//
//                val line = "${player.name};${player.score}"
//
//                bufferedWriter.write(line).also { bufferedWriter.newLine() }
//            }
//
//            bufferedWriter.close()
//        } catch (e: Exception) {
//            println(e)
//        }
//    }
//
//    fun writeFileCoinsAndNumOfGames(fileName: String, numOfGames: Int, coins: Int){
//        val file = File(fileName)
//
//        try {
//            val bufferedWriter = BufferedWriter(FileWriter(file))
//
//            val line_numOfGames = "numberOfGames;$numOfGames"
//            val line_coins = "coins;$coins"
//
//            bufferedWriter.write(line_numOfGames).also { bufferedWriter.newLine() }
//            bufferedWriter.write(line_coins)
//
//            bufferedWriter.close()
//        } catch (e: Exception) {
//            println(e)
//        }
//    }


}

fun main() {

//    val map: Top20Players = Top20Players()
//
//    map.addPlayer(Player("bernardo", 20))
//    map.addPlayer(Player("constanca", 30))
//    map.addPlayer(Player("Maria", 15))
//    map.addPlayer(Player("Maria", 14))
//
//    println( "map =  ${map.getTopPlayers()} ")
//
//    FileAccess.writeFileScores("scores.txt", map)
//
//
//    val map1: Top20Players = Top20Players()
//    FileAccess.readFileOfScores("scores.txt", map1)
//
//    println( "map1 = ${map1.getTopPlayers()} ")

//    FileAccess.readFileOfCoinsAndGames("CoinsAndNumOfGames.txt")
//    println(FileAccess.numberOfGames)
//    println(FileAccess.coins)

    //FileAccess.writeFileCoinsAndNumOfGames("CoinsAndNumOfGames.txt", 10, 20)
}