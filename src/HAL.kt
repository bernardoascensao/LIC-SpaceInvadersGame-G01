import isel.leic.UsbPort;

object HAL { // Virtualiza o acesso ao sistema UsbPort
    // Inicia a classe

    var atualStateOutput = 0
    fun init() {
        UsbPort.write(0)
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
        atualStateOutput = atualStateOutput.and(mask.inv())

        UsbPort.write(atualStateOutput)
    }
    // Escreve nos bits representados por mask o valor de value
    fun writeBits(mask: Int, value: Int) {
        clrBits(mask)
        setBits(value)
    }
}