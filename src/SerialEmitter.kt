
object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.

    enum class Destination {LCD, SCORE}

    // Inicia a classe
    fun init() {
        HAL.init()
        HAL.setBits(LCDselMASK)          //colocar o LCDsel a '1'
        HAL.setBits(SCselMASK)           //colocar o SCsel a '1'
        HAL.clrBits(SCLKMASK)            //colocar SLCK a '0'
    }

    /*
    Envia uma trama para o SerialReceiver identificado o destino em addr,
    os bits de dados em ‘data’ e em size o número de bits a enviar.
    */
    fun send(addr: Destination, data: Int, size : Int) {
        if (addr == Destination.LCD){
            //caso o destino seja o LCD
            HAL.clrBits(LCDselMASK)

        }else if(addr == Destination.SCORE){
            //caso o destino seja o SCORE
            HAL.clrBits(SCselMASK)
        }

        var num_of_1 = 0
        for (bitNum in 0 until size){

            //calcular SDX
            val sdx = 1.shl(bitNum).and(data).shr(bitNum)

            //colocar no usbPort o valor correto de SDX
            if(sdx == 1){
                num_of_1++
                HAL.setBits(SDXMASK)
            }else{
                HAL.clrBits(SDXMASK)
            }

            //pulsar o SCLK
            HAL.setBits(SCLKMASK)
            HAL.clrBits(SCLKMASK)
        }

        //enviar bit de paridade
        if (num_of_1 % 2 == 0){
            HAL.clrBits(SDXMASK)
        }else{
            HAL.setBits(SDXMASK)
        }

        //pulsar SCLK
        HAL.setBits(SCLKMASK)
        HAL.clrBits(SCLKMASK)

        if (addr == Destination.LCD){
            //caso o destino seja o LCD
            HAL.setBits(LCDselMASK)

        }else if(addr == Destination.SCORE){
            //caso o destino seja o SCORE
            HAL.setBits(SCselMASK)
        }
    }
}