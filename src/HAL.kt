import isel.leic.UsbPort

const val ackMASK = 0b00000001
const val dvalMASK =  0b00010000
const val QsignalMASK = 0b00001111

const val rsMASK = 0b00100000
const val dataMASK = 0b00011110
const val enableMASK = 0b01000000
const val clkRegMASK = 0b10000000

const val SDXMASK = 0b00100000
const val SCLKMASK = 0b01000000
const val LCDselMASK = 0b10000000
const val SCselMASK = 0b00000010
const val acceptCoinMASK = 0b00000100
const val CoinMASK = 0b00100000
object HAL { // Virtualiza o acesso ao sistema UsbPort
    // Inicia a classe

    var atualStateOutput = 0
    fun init() {
        //apenas os bits do SS_Score e SS_LCD ficam ativos pois são active low
        //atualStateOutput = 0b10000010
        atualStateOutput = 0b00000000
        UsbPort.write(atualStateOutput)
    }
    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int): Boolean {
        val value = UsbPort.read().and(mask)

        return value != 0
    }
    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int {
        val value = UsbPort.read().and(mask)

        return value
    }
    // Coloca os bits representados por mask no valor lógico ‘1’
    fun setBits(mask: Int) {
        atualStateOutput = atualStateOutput.or(mask)

        UsbPort.write(atualStateOutput)
    }
    // Coloca os bits representados por mask no valor lógico ‘0’
    fun clrBits(mask: Int) {
        val maskInv = mask.inv()
        atualStateOutput = atualStateOutput.and(maskInv)

        UsbPort.write(atualStateOutput)
    }
    // Escreve nos bits representados por mask o valor de value
    fun writeBits(mask: Int, value: Int) {
        clrBits(mask)
        setBits(value.and(mask))
    }
}