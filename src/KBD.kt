import isel.leic.utils.Time

object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.
    const val NONE = 0.toChar();
    val kackMASK = 0x01
    val kvalMASK =  0x10
    val ksignalMASK = 0x0F

    val teclado = listOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#', NONE, NONE, NONE, NONE)

    // Inicia a classe
    fun init(){
        HAL.clrBits(kackMASK)
    }
    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {
        if(HAL.isBit(kvalMASK)){
            val k = HAL.readBits(ksignalMASK)       // ler o valor da tecla premida
            HAL.setBits(kackMASK)                   // colocar KACK a '1'

            while(HAL.isBit(kvalMASK)){             // verificar se a tecla ainda está permida
                Time.sleep(10)
            }

            HAL.clrBits(kackMASK)                   // voltar a colocar o KACK a '0' para iniciar a varredura do teclado
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
        val value = KBD.waitKey(5000)
        println(value)
    }

}