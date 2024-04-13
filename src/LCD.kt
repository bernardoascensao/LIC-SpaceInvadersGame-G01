import isel.leic.utils.Time

object LCD { // Escreve no LCD usando a interface a 4 bits.
    private const val LINES = 2  // Dimensão do display.
    private const val COLS = 16

    // Escreve um byte de comando/dados no LCD em paralelo
//    private fun writeByteParallel(rs: Boolean, data: Int){
//        val dataLow = data.shl(1)
//        val dataHigh = data.shr(3)
//
//        if(rs) HAL.setBits(rsMASK) else HAL.clrBits(rsMASK)
//
//        // Escreve parte baixa
//        HAL.writeBits(dataMASK, dataHigh)
//        Time.sleep(30)
//
//        HAL.setBits(clkRegMASK)
//        Time.sleep(30)
//
//        HAL.clrBits(clkRegMASK)
//        Time.sleep(30)
//
//        // Escreve parte alta
//        HAL.writeBits(dataMASK, dataLow)
//        Time.sleep(30)
//
//        HAL.setBits(clkRegMASK)
//        Time.sleep(30)
//
//        HAL.clrBits(clkRegMASK)
//        Time.sleep(30)
//
//        HAL.setBits(enableMASK)
//        Time.sleep(30)
//
//        HAL.clrBits(enableMASK)
//        Time.sleep(30)
//
//    }
    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int){
        var dataToSend = 0
        if (rs) dataToSend = 0b000000001.or(data.shl(1)) else dataToSend = data.shl(1)


        SerialEmitter.send(addr = SerialEmitter.Destination.LCD, dataToSend, 9)

        Time.sleep(100)
    }
    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int){
//        writeByteParallel(rs, data)
        writeByteSerial(rs, data)
    }
    // Escreve um comando no LCD
    private fun writeCMD(data: Int){
        writeByte(rs = false, data)
    }
    // Escreve um dado no LCD
    private fun writeDATA(data: Int){
        writeByte(rs = true, data)
    }
    // Envia a sequência de iniciação para comunicação a 4 bits.
    fun init(){
        Time.sleep(20)
        writeCMD(0b00110000)
        Time.sleep(10)
        writeCMD(0b00110000)
        Time.sleep(5)
        writeCMD(0b00110000)

        writeCMD(0b00111000)        //N(2lines) = 1, F(40dots) = 0
        writeCMD(0b00001000)
        writeCMD(0b00000001)
        writeCMD(0b00000110)

        writeCMD(0b00001111)
    }
    // Escreve um caráter na posição corrente.
    fun write(c: Char){
        writeDATA(c.code)
    }
    // Escreve uma string na posição corrente.
    fun write(text: String){
        for(c in text){
            write(c)
        }
    }
    // Envia comando para posicionar cursor (‘line’:0..LINES-1 , ‘column’:0..COLS-1)
    fun cursor(line: Int, column: Int){

        require(line == 0 || line == 1) { "Invalid Line, must be between 0 and 1" }

        var address = 0
        if(line == 0){
            address = address.shl(6).or(column)
        }
        else{
            address = 1
            address = address.shl(6).or(column)
        }

        //fazer operação de or para corresponder ao comando de "set DDRAM address"
        writeCMD(address.or(0b10000000))
    }
    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear(){
        writeCMD(0b00000001)
    }
}

fun main(){
    HAL.init()
    KBD.init()
    LCD.init()

    while(true){
        val c = KBD.waitKey(8000)
        LCD.write(c)
        Time.sleep(2000)
        LCD.cursor(1, 5)
    }
}