import isel.leic.utils.Time

object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.
    const val NONE = 0.toChar();
    val ackMASK = 0b00000001
    val dvalMASK =  0b00010000
    val QsignalMASK = 0b00001111

    val teclado = listOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#', NONE, NONE, NONE, NONE)

    // Inicia a classe
    fun init(){
        HAL.clrBits(ackMASK)
    }
    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {
        if(HAL.isBit(dvalMASK)){
            val k = HAL.readBits(QsignalMASK)       // ler o valor da tecla premida
            HAL.setBits(ackMASK)                   // colocar KACK a '1'

            while(HAL.isBit(dvalMASK)){             // verificar se a tecla ainda está permida
                Time.sleep(10)
            }

            HAL.clrBits(ackMASK)                   // voltar a colocar o KACK a '0' para iniciar a varredura do teclado
            val char = teclado[k]                   // obter o caracter correspondente da tecla permida

            return char
        }
        else return NONE
    }
    /* Retorna a tecla premida, caso ocorra antes do ‘timeout’ (representado em milissegundos), ou
    NONE caso contrário. */
    fun waitKey(timeout: Long): Char {
        val first_call_time = Time.getTimeInMillis()
        var current_time = first_call_time
        var key = NONE
        while(current_time - first_call_time < timeout){

            key = getKey()
            if(key != NONE){
                break
            }
            current_time = Time.getTimeInMillis()
        }
        return key
    }
}
fun main(args: Array<String>) {
    KBD.init()
    while (true){
        val value = KBD.waitKey(70000)
        println(value)
    }

}