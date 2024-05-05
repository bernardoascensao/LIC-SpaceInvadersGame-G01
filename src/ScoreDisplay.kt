object ScoreDisplay { // Controla o mostrador de pontuação.
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        SerialEmitter.init()
    }

    private var actualValue = 0

    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
        //avaliar quantos algarismos mudam de um value para outro
        val algarismos_actualValue = actualValue.toString().length
        val algarismos_value = value.toString().length
        val diff = algarismos_actualValue - algarismos_value

        var flag = false
        var quociente = value / 10
        var resto = value % 10

        for (i in 0..5){
            if(flag){
                break
            }

            if(quociente == 0){
                flag = true
            }

            val dataToSend = resto.shl(3).or(i)
            SerialEmitter.send(addr = SerialEmitter.Destination.SCORE, dataToSend, 7)

            resto = quociente % 10
            quociente = quociente / 10

        }

        if(diff > 0){
            var digit_to_update =  0b000 + algarismos_value
            for (i in 1 .. diff){
                val data_to_write = 0b0000
                val data_to_send = data_to_write.shl(3).or(digit_to_update)
                SerialEmitter.send(addr = SerialEmitter.Destination.SCORE, data_to_send, 7)
                digit_to_update++
            }
        }

        //update display after send the data
        SerialEmitter.send(addr = SerialEmitter.Destination.SCORE, 0b0000110, 7)
        actualValue = value
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean) {
        var dataToSend = 0b1
        if (value) dataToSend = dataToSend.shl(3).or(0b111) else dataToSend = 0b0000111

        SerialEmitter.send(addr = SerialEmitter.Destination.SCORE, dataToSend, 7)
    }
}

fun main() {
    ScoreDisplay.init()

    ScoreDisplay.off(false)

    ScoreDisplay.setScore(53)


}