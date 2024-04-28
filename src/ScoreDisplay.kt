object ScoreDisplay { // Controla o mostrador de pontuação.
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        SerialEmitter.init()
    }

    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
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

        //update display after send the data
        SerialEmitter.send(addr = SerialEmitter.Destination.SCORE, 0b0000110, 7)
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