object TUI {

    private var actuaLine = 0
    private var actuaColumn = 0

    fun init() {
        HAL.init()
        KBD.init()
        LCD.init()
        actuaLine = 0
        actuaColumn = 0
    }

    fun readKey(): Char = KBD.waitKey(8000)

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
}

fun main() {
    TUI.init()
    TUI.writeString("0123456789")

    while (true){
        val c = TUI.readKey()
        when(c) {
            '#' -> TUI.clearLCD()
            '*' -> TUI.changeLine()
            else -> TUI.writeChar(c)
        }
    }
}