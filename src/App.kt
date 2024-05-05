import isel.leic.utils.Time

class App {

    var coins = 0
    private var numberOfGames = 0
    private val scores = Top20Players()
    private val listOfInvadors = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private var currScore: Int

    init {
        TUI.init()
        ScoreDisplay.init()
        CoinAcceptor.init()
        ScoreDisplay.off(false)
        //M.init()
        coins = 0
        numberOfGames = 0
        currScore = 0
    }

    /**
     * Carrega o número de coins,o número de jogos realizados
     * e as estatísticas dos jogadores
     **/
    fun loadScoreAndStatistics(){
        val coinsAndGames = FileAccess.readFileOfCoinsAndGames("CoinsAndNumOfGames.txt")
        coins = coinsAndGames.first
        numberOfGames = coinsAndGames.second

        FileAccess.readFileOfScores("scores.txt", scores)
    }

    /**
     * Escreve uma mensagem no LCD
     * @param message mensagem a ser escrita
     **/
    private fun writeMessage(message: String){
        clearDisplay()
        TUI.writeString(message)
    }

    /**
     * Escreve uma mensagem centrada no LCD
     * @param message mensagem a ser escrita
     * @param splitInTwoLines se a mensagem vai ser dividida em duas linhas ou não
     **/
    fun writeMessageCenterelized(message: String, splitInTwoLines: Boolean = false){
        clearDisplay()
        TUI.writeStringCenterelized(message, splitInTwoLines)
    }

    /**
     * Apresenta no LCD a lista de 'player' e do respetivo score,
     * se houver coins, apresenta intermitentemente a lista de 'players' e o número de coins
     * até o 'user' premir '*' para começar o jogo
     **/
    fun printScoresOnLCD(){
        var key = ' '
        while (key != '*' || coins >= 0) {
            var playerRanking = 1
            for (player in scores){
                clearDisplay()
                writeMessageInLine("Space Invaders", 0, centerelized = true)
                writeMessageInLine("$playerRanking-${player.name}: ${player.score}", 1, centerelized = true)
                //ScoreDisplay.setScore(player.score)
                Time.sleep(500)

                key = readKey(1500)
                if(key == '5') {
                    coins++
                } else if (key == '*' && coins > 0) break

                if (coins > 0) {
                    clearDisplay()
                    writeMessageInLine("Space Invaders", 0, centerelized = true)
                    writeMessageInLine("$coins$", 1, centerelized = false)
                    //ScoreDisplay.setScore(player.score)
                    Time.sleep(500)

                    key = readKey(1500)
                    if (key == '*') break
                    else if (key == '5') coins++
                }

                playerRanking++
            }
            if(key == '*' && coins > 0) break
        }
    }

    /**
     * Escreve uma mensagem na linha especificada
     * @param msg mensagem a ser escrita
     * @param line linha onde a mensagem vai ser escrita
     * @param centerelized se a mensagem vai ser centrada ou não
     **/
    private fun writeMessageInLine(msg: String, line: Int, centerelized: Boolean){
        TUI.writeMessageInLine(msg, line, centerelized)
    }

    /**
     * Atualiza a lista de invasores, ou seja, desloca todos os invasores uma posição para a esquerda
     * e coloca um novo invasor na última posição da lista
     * @param list lista de invasores
     * @param newInvasor novo invasor a ser colocado na última posição da lista
     **/
    private fun updateListInvasors(list: MutableList<Char>, newInvasor: Char){
        for (i in 0..< list.size){
            if (i == list.size - 1){
                list[i] = newInvasor
            } else {
                list[i] = list[i + 1]
            }
        }
    }

    /**
     * Verifica qual o tipo de invasor na linha da frente, a seguir verifica se o
     * tipo de invasor observado é do mesmo tipo que o canhão disparou, e caso seja,
     * remove o invasor da linha da lista e retorna true, caso contrário retorna false
     * @param list lista de invasores
     * @param cannon o tipo que o canhão disparou
     **/
    private fun shotInvasorSucceeded(list: MutableList<Char>, cannon: Char): Boolean{
        val firstInvasor = list.firstOrNull { it != ' ' }
        if (firstInvasor == cannon){
            val indexFirstInvasor = list.indexOf(firstInvasor)
            list[indexFirstInvasor] = ' '
            return true
        }
        return false
    }

    /**
     * Imprime as bases para os canhões
     **/
    private fun printBasesForCannons(){
        TUI.writeCharAt(0, 0, ']')
        TUI.writeCharAt(1, 0, ']')
    }

    /**
     * Posiciona o cursor na linha e coluna especificadas
     * @param line linha onde o cursor vai ser posicionado
     **/
    private fun setCannonOnLine(line: Int){
        TUI.setCursor(line, 1)
    }

    /**
     * Inicia um novo jogo, caso o 'user' tenha coins suficietes.
     * Os invasores vão aparecer no LCD no lado direito e vão deslocar-se para a esquerda.
     * O jogo termina quando os invasores chegarem à posiçãop da nave, escrevendo no LCD "End! Score: "
     * @param teste se é um teste ou não (caso seja o score não é contabilizado)
     **/
    fun startNewGame(teste: Boolean){
        if (!teste && coins <= 0) {
            writeMessage("No credits. Press '*' to exit")
            while (true){
                val c = readKey(1000)
                if (c == '*') break
            }
            clearDisplay()
            return
        }

        writeMessage("Press '#' to start")
        while (true){
            val c = readKey(1000)
            if (c == '#') break
        }

        writeMessageCenterelized("New Game!", splitInTwoLines = true)
        Time.sleep(1000)
        val list0 = MutableList(14) {' '}       //estado atual da linha 0 do LCD
        val list1 = MutableList(14) {' '}       //estado atual da linha 1 do LCD
        var cannon0 = ' '                           //o canhao começa vazio
        var cannon1 = ' '
        currScore = 0
        var invasorPos0 = 15                        //os invasores começam a vir da esquerda
        var invasorPos1 = 15
        var myPos =  0                              //comecamos no canhao 0
        clearDisplay()
        printBasesForCannons()
        setCannonOnLine(0)

        while (true){
            if (invasorPos0 in 2 .. 15 && invasorPos1 in 2 .. 15) {
                //se os invasores ainda nao chegram
                val newInvasrorLine0 = listOfInvadors.random()
                val newInvasrorLine1 = listOfInvadors.random()
                updateListInvasors(list0, newInvasrorLine0)    //atualiza apenas a lista
                printInvasor(0, list0)                      //invasor na linha de cima
                //list0[invasorPos0] = invasor_line_0
                invasorPos0--
                updateListInvasors(list1, newInvasrorLine1)    //atualiza apenas a lista
                printInvasor(1, list1)                      //printa sempre na coluna 15
                //list1[invasorPos1] = invasor_line_1
                invasorPos1--
            } else {
                //os invasores chegaram
                writeMessage("End! Score: $currScore")
                Time.sleep(2000)
                clearDisplay()
                break
            }

            //criamos este while true para garantir que o 'user' tem tempo de clicar em mais do que pelo menos 1 tecla (neste caso tem 1 seg)
            val startTime = System.currentTimeMillis()
            while (true){
                val currentTime = System.currentTimeMillis()
                if (currentTime - startTime > 2000){                                            //tempo que o utilizador tem até à proxima chegada de invasores
                    break
                }

                //ler tecla do jogador
                when (val key = readKey(500)) {                                         //espera até 2 seg até o 'user' premir uma tecla, ou seja,
                                                                                                //os invasores chegam a cada 2 seg no máximo
                    '*' -> {                                                                    //muda de linha
                        if (myPos == 0){
                            myPos = 1
                            setCannonOnLine(1)
                        } else {
                            myPos = 0
                            setCannonOnLine(0)
                        }
                    }
                    in '0'..'9' -> {
                        if (myPos == 0){
                            TUI.writeCharAt(0, 0, key)
                            cannon0 = key
                        } else {
                            TUI.writeCharAt(1, 0, key)
                            cannon1 = key
                        }
                    }
                    '#' -> {
                        if (myPos == 0) {
                            clearCannonOnLine(0)
                            if (shotInvasorSucceeded(list0, cannon0)){
                                deleteInvasorOnLCD(0, invasorPos0 + 1)
                                invasorPos0++
                                if (!teste) currScore++
                            }
                            cannon0 = ' '
                        } else /*myPos == 1*/ {
                            clearCannonOnLine(1)
                            if (shotInvasorSucceeded(list1, cannon1)){
                                deleteInvasorOnLCD(1, invasorPos1 + 1)
                                invasorPos1++
                                if (!teste) currScore++
                            }
                            cannon1 = ' '
                        }
                        printBasesForCannons()
                    }
                }
            }
        }
        writeMessage("End of Game")
    }

    /**
     * Regista o score do jogador, pedindo o nome do jogador e guardando o score
     * no ficheiro "scores.txt"
     **/
    fun registerScore(){
        clearDisplay()
        writeMessageInLine("Score:$currScore", 1, centerelized = false)
        writeMessageInLine("Name:", 0, centerelized = false)

        val name = StringBuilder('A'.toString())
        var cursor = 5
        var index = 0
        var currChar = 'A'
        TUI.writeCharAt(0, cursor, 'A')
        while (true) {
            val key = readKey(18000)
            when (key) {
                '5' -> break
                '4' ->{
                    if (cursor > 5) moveCursor(left = true, right = false).also { cursor--; index-- }
                }
                '6' -> {
                    moveCursor(right = true, left = false)
                    cursor++; currChar = 'A'
                    if (index == name.length - 1) {
                        name.append('A')
                        TUI.writeCharAt(0, cursor, 'A')
                    }
                    index++
                }
                '2' -> {
                    if (currChar < 'Z') currChar++
                    name.setCharAt(index, currChar)
                    TUI.writeCharAt(0, cursor, currChar)
                }
                '8' -> {
                    if(currChar > 'A') currChar--
                    name.setCharAt(index, currChar)
                    TUI.writeCharAt(0, cursor, currChar)
                }
                '*' -> {
                    if (index == name.length -1 && name.length >1){
                        name.deleteCharAt(index)
                        moveCursor(left = true)
                        TUI.writeCharAt(0, cursor, ' ')
                        index--
                        cursor--
                    }
                }
            }
        }
        scores.addPlayer(Player(name.toString(), currScore))
        FileAccess.writeFileScores("scores.txt", scores)
    }

    /**
     * Move o cursor para a esquerda ou para a direita
     * @param left se o cursor vai ser movido para a esquerda
     * @param right se o cursor vai ser movido para a direita
     * @throws IllegalArgumentException se ambos os parâmetros forem verdadeiros ou falsos
     **/
    private fun moveCursor(left: Boolean = false, right: Boolean = false){
        require((left && !right) || (!left && right) ) { "Invalid movement" }

        val (line, col) = TUI.getActualPosition()

        if (left) TUI.setCursor(line, col-1) else TUI.setCursor(line, col+1)
    }

    /**
     * Limpa o display do LCD
     **/
    private fun clearDisplay() = TUI.clearLCD()

    /**
     * Imprime a lista de invasores na linha especificada do LCD
     * @param line linha onde os invasores vão ser impressos
     * @param list lista de invasores
     **/
    private fun printInvasor(line: Int, list: MutableList<Char>){
        for (i in 0..< list.size){
            // Tem de ser i + 2 porque a primeira coluna é para a base do canhão
            // e a segunda coluna é para a nave
            if (list[i] != ' ') TUI.writeCharAt(line, i + 2, list[i])
        }
    }

    /**
     * Apaga o invasor na linha e coluna especificadas
     * @param line linha onde o invasor vai ser apagado
     * @param col coluna onde o invasor vai ser apagado
     **/
    private fun deleteInvasorOnLCD(line: Int, col: Int) = TUI.writeCharAt(line, col, ' ')

    /**
     * Apaga o canhão na linha especificada
     * @param line linha onde o canhão vai ser apagado
     **/
    private fun clearCannonOnLine(line: Int) = TUI.writeCharAt(line, 1, ' ')

    /**
     * Lê uma tecla do teclado
     * @param timeout tempo de espera até ler uma tecla (por 'default' é 8 segundos)
     **/
    private fun readKey(timeout: Long = 8000): Char = TUI.readKey(timeout)

}
