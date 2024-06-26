import isel.leic.utils.Time

class App {
    private val COLUMNS = TUI.COLS
    private val LINES = TUI.LINES
    private val empty = ' '

    private var credits: Int = 0

    private val listOfInvadors = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private var currScore: Int
    var isShutDown = false
    var isMaintenance = false
    var gameMode = false

    private val stats = Statistics
    private val scores = Scores

    init {
        TUI.init()
        ScoreDisplay.init()
        CoinAcceptor.init()
        ScoreDisplay.off(false)
        M.init()
        currScore = 0
        loadScoreAndStatistics()
    }

    /**
     * Apresenta no LCD a lista de 'player' e do respetivo score,
     * se houver coins, apresenta intermitentemente a lista de 'players' e o número de coins
     * até o 'user' premir '*' para começar o jogo
     **/
    fun printScoresOnLCD(){
        while (!gameMode && !isMaintenance) {
            var playerRanking = 1
            for (player in scores.list){
                clearDisplay()
                writeMessageInLine("Space Invaders", 0, centerelized = true)
                writeMessageInLine("$playerRanking-${player.name}: ${player.score}", 1, centerelized = true)
                ScoreDisplay.setScore(player.score)

                var key = readKey(1000)
                if (isInMaintenance()) break
                else if(hasCoin()){ stats.coins += 2; credits += 2 }
                else if (key == '*' && credits > 0){ gameMode = true; break }

                if (credits > 0) {
                    clearDisplay()
                    writeMessageInLine("Space Invaders", 0, centerelized = true)
                    writeMessageInLine("$credits$", 1, centerelized = false)
                    ScoreDisplay.setScore(player.score)

                    key = readKey(1000)
                    if (isInMaintenance()) break
                    else if (key == '*') break
                    else if (hasCoin()){ stats.coins += 2; credits +=2 }
                }
                playerRanking++
            }
        }
    }

    /**
     * Inicia um novo jogo, caso o 'user' tenha coins suficietes.
     * Os invasores vão aparecer no LCD no lado direito e vão deslocar-se para a esquerda.
     * O jogo termina quando os invasores chegarem à posiçãop da nave, escrevendo no LCD "End! Score: "
     * @param teste se é um teste ou não (caso seja o score não é contabilizado)
     **/
    fun startNewGame(teste: Boolean){
        if (!teste && credits <= 0) {
            writeMessage("No credits. Press '*' to exit")
            while (true){
                val c = readKey(1000)
                if (c == '*') break
            }
            clearDisplay()
            return
        }

        credits--
        stats.numberOfGames++
        ScoreDisplay.setScore(0)

        writeMessageCenterelized("New Game!", splitInTwoLines = true)
        sleep(1)
        val list0 = MutableList(COLUMNS - 2) {empty}       //estado atual da linha 0 do LCD
        val list1 = MutableList(COLUMNS - 2) {empty}       //estado atual da linha 1 do LCD
        var cannon0 = empty                                     //o canhao começa vazio
        var cannon1 = empty
        currScore = 0
        var invasorPos0 = COLUMNS - 1                        //posição do invasor mais próximo,
        var invasorPos1 = COLUMNS - 1                        //os invasores começam a vir da esquerda
        var myPos =  0                                       //a nave começa na posição 0
        clearDisplay()
        printBasesForSpaceShip()
        setSpaceShipOnLine(0)

        while (true){
            if (invasorPos0 > (COLUMNS - list0.size) && invasorPos1 > (COLUMNS - list1.size)) {
                //se os invasores ainda nao chegram
                val newInvasrorLine0 = listOfInvadors.random()
                val newInvasrorLine1 = listOfInvadors.random()

                updateListInvasors(list0, newInvasrorLine0)    //atualiza apenas a lista
                printInvasor(0, list0)                      //invasor na linha de cima
                invasorPos0--

                updateListInvasors(list1, newInvasrorLine1)    //atualiza apenas a lista
                printInvasor(1, list1)                      //printa sempre na coluna 15
                invasorPos1--
            } else {
                //os invasores chegaram
                writeMessage("End! Score: $currScore")
                sleep(1)
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
                            setSpaceShipOnLine(1)
                        } else {
                            myPos = 0
                            setSpaceShipOnLine(0)
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
                            if (shotInvasorSucceeded(list0, cannon0)){
                                deleteInvasorOnLCD(0, invasorPos0 + 1)
                                invasorPos0++
                                currScore++
                                ScoreDisplay.setScore(currScore)
                            }
                            cannon0 = empty
                        } else /*myPos == 1*/ {
                            if (shotInvasorSucceeded(list1, cannon1)){
                                deleteInvasorOnLCD(1, invasorPos1 + 1)
                                invasorPos1++
                                currScore++
                                ScoreDisplay.setScore(currScore)
                            }
                            cannon1 = empty
                        }
                        printBasesForSpaceShip()
                    }
                }
            }
        }
        gameMode = false
    }

    /**
     * Regista o score do jogador, pedindo o nome do jogador e guardando o score
     * no ficheiro "scores.txt"
     **/
    fun registerScore(){
        clearDisplay()
        val msg = "Name:"
        writeMessageInLine("Score:$currScore", 1, centerelized = false)
        writeMessageInLine(msg, 0, centerelized = false)

        val name = StringBuilder('A'.toString())
        var cursor = msg.length
        var index = 0
        var currChar = 'A'
        TUI.writeCharAt(0, cursor, 'A')
        while (true) {
            val key = readKey(18000)
            when (key) {
                '5' -> break
                '4' ->{
                    if (cursor > msg.length) moveCursor(left = true, right = false).also { cursor--; index-- }
                }
                '6' -> {
                    if (cursor < COLUMNS - 1){
                        moveCursor(right = true, left = false)
                        cursor++
                        if (index == name.length - 1) {
                            currChar = 'A'
                            name.append(currChar)
                            TUI.writeCharAt(0, cursor, currChar)
                        }
                        index++
                    }
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
        scores.list.addPlayer(Player(name.toString(), currScore))
    }

    /**
     * Entra no modo de manutenção
     */
    fun maintenanceMode(){

        while (isMaintenance){
            clearDisplay()
            writeMessageInLine("Maintenance Mode", 0, centerelized = true)
            writeMessageInLine("*-Count  #-ShutD", 1, centerelized = true)

            val key = readKey(1000)
            if(!isInMaintenance()) break
            when (key) {
                '*' -> {
                    showCoinsAndGames()
                }
                '#' -> {
                    shutDownMenu()
                    break
                }
                in '0'..'9' -> {
                    startNewGame(teste = true)
                    clearDisplay()
                    writeMessageInLine("***GameOver***", 0, centerelized = true)
                    writeMessageInLine("Score: $currScore", 1, centerelized = false)
                }
            }
        }
    }

    /**
     * Imprime no LCD o número de coins e o número de jogos realizados
     */
    private fun showCoinsAndGames(){
        clearDisplay()
        writeMessageInLine("Coins: ${stats.coins}", 0, centerelized = true)
        writeMessageInLine("Games: ${stats.numberOfGames}", 1, centerelized = true)
        readKey(3000)
    }

    /**
     * Imprime no LCD o menu de desligar o sistema
     */
    private fun shutDownMenu(){
        clearDisplay()
        writeMessageInLine("Shutdown", 0, centerelized = true)
        writeMessageInLine("5-Yes   other-No", 1, centerelized = true)

        val key = readKey(3000)
        if (key == '5') {
            Statistics.writeStatistics()
            Scores.writeScores()
            clearDisplay()
            shutDown()
        }
    }

    /**
     * Desliga o sistema
     **/
    private fun shutDown(){
        isShutDown = true
    }

    /**
     * Carrega o número de coins,o número de jogos realizados
     * e as estatísticas dos jogadores
     **/
    fun loadScoreAndStatistics(){
        stats.readStatistics()
        scores.readScores()
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
    private fun printBasesForSpaceShip(){
        TUI.writeCharAt(0, 0, ']')
        TUI.writeCharAt(1, 0, ']')
    }

    /**
     * Apaga a nave na linha atual,
     * coloca o cursor na posição onde queremos colocar a nova nave
     * e escreve a nave
     **/
    private fun setSpaceShipOnLine(line: Int){
        if (line == 0){
            clearCannonOnLine(1)
            TUI.setCursor(line, 1)
            writeSpaceShip()
            TUI.setCursor(line, 1)                  //serve para quando mudamos a nave de linha, o cursor não ir temporáriamente para a frente
        } else {
            clearCannonOnLine(0)
            TUI.setCursor(line, 1)
            writeSpaceShip()
            TUI.setCursor(line, 1)                  //serve para quando mudamos a nave de linha, o cursor não ir temporáriamente para a frente
        }
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

    /**
     * Escreve o caracter especial da nave na posição atual do cursor
     **/
    private fun writeSpaceShip(){
        TUI.writeSpecialChar(0)
    }

    /**
     * Verifica se o sistema está em manutenção
     */
    private fun isInMaintenance(): Boolean {
        if (M.isInMaintenance()){
            isMaintenance = true
            return true
        } else {
            isMaintenance = false
            return false
        }
    }

    /**
     * Verifica se está a ser inserida uma moeda
     */
    private fun hasCoin(timeout: Long = 2000): Boolean = CoinAcceptor.hasCoin(timeout)

    /**
     * Espera por um determinado tempo em segundos
     */
    private fun sleep(seconds: Long): Unit = Time.sleep(seconds * 1000)
}
