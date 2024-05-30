object TUI {
    const val LINES = LCD.LINES
    const val COLS = LCD.COLS

    private var actuaLine = 0
    private var actuaColumn = 0

    fun init() {
        KBD.init()
        LCD.init()
        actuaLine = 0
        actuaColumn = 0
    }

    fun readKey(timeout: Long = 8000): Char = KBD.waitKey(timeout)

    fun writeString(str: String){
        if(str.length >= 16){
            for (char in 0..15){
                LCD.write(str[char])
                actuaColumn++
            }
            LCD.cursor(1, 0)
            actuaLine = 1
            actuaColumn = 0
            for (char in 16 until str.length){
                LCD.write(str[char])
                actuaColumn++
            }
        }else{
            LCD.write(str)
            actuaColumn += str.length
        }
    }

    //!!!!!!! restriçoes!!, tanto a primeira parte como a segunda
    //nao podem ter mais de 16 chars cada uma
    fun writeStringCenterelized(str: String, splitInTwoLines: Boolean = false){

        require(str.length <= 28) {"String too long"}

        val part1: String
        val part2: String

        if (splitInTwoLines){
            val parts = str.split(' ', limit = 2)
            part1 = parts.getOrElse(0) { "" }
            part2 = parts.getOrElse(1) { "" }
        } else {
            part1 = str
            part2 = ""
        }

        require(part1.length <= 16 && part2.length <= 16) {"At least one of the lines is too long"}

        val spacesNeeded_part1 = (16 - part1.length) / 2
        val spacesNeeded_part2 = (16 - part2.length) / 2

        for (char in 1..spacesNeeded_part1) {
            LCD.write(' ')
            actuaColumn++
        }
        for (char in 0 until part1.length){
            LCD.write(part1[char])
            actuaColumn++
        }

        if(splitInTwoLines){
            LCD.cursor(1, 0)
            actuaLine = 1
            actuaColumn = 0
            for (char in 1..spacesNeeded_part2) {
                LCD.write(' ')
                actuaColumn++
            }
            for (char in 0 until part2.length){
                LCD.write(part2[char])
                actuaColumn++
            }
        }
    }

    fun writeChar(char: Char){
        if(actuaLine == 0 && actuaColumn > 15){
            LCD.cursor(1, 0)
            actuaLine = 1
            actuaColumn = 0
            LCD.write(char)
            actuaColumn += 1
        }
        else{
            LCD.write(char)
            actuaColumn += 1
        }
    }

    fun clearLCD(){
        LCD.clear()
        actuaLine = 0
        actuaColumn = 0
    }

    fun changeLine(){
        if (actuaLine == 0){
            LCD.cursor(1, actuaColumn)
            actuaLine = 1
        }
        else{
            LCD.cursor(0, actuaColumn)
            actuaLine = 0
        }
    }

    fun writeCharAt(line: Int, col: Int, char: Char) {
        val savedLine = actuaLine
        val savedCol = actuaColumn
        LCD.cursor(line, col)
        writeChar(char)
        actuaLine = savedLine
        actuaColumn = savedCol
        LCD.cursor(savedLine, savedCol)                 //a seguir de printar os invasores, voltar aa posicionar o cursor no sitio onde estávamos
    }

    fun setCursor(line: Int, column: Int){
        actuaLine = line
        actuaColumn = column
        LCD.cursor(line, column)
    }

    fun writeMessageInLine(msg: String, line: Int, centerelized: Boolean) {
        LCD.cursor(line, 0)
        actuaLine = line
        actuaColumn = 0

        if (centerelized) {
            val spacesNeeded = (16 - msg.length) / 2
            writeString(" ".repeat(spacesNeeded) + msg)
        } else {
            writeString(msg)
        }
    }

    fun getActualPosition(): Pair<Int, Int> {
        return Pair(actuaLine, actuaColumn)
    }

    fun writeSpecialChar(c: Int){
        LCD.writeSpecialChar(c)
    }
}

fun main() {
    TUI.init()
    //TUI.writeString("0123456789")

    var col = 15
    while (true){
        val c = TUI.readKey(1000)
        TUI.writeCharAt(0, col, '2')
        TUI.writeCharAt(1, col, '3')
        col--
        when(c) {
            '#' -> TUI.clearLCD()
            '*' -> TUI.changeLine()
            0.toChar() -> print("Não queremos escrever NONE para não avançar o cursor")
            else -> TUI.writeChar(c)
        }
    }
}