import isel.leic.utils.Time

fun main() {
    println("Hello World!")
    val app = App()

    app.writeMessageCenterelized("Space Invaders", splitInTwoLines = true)
    Time.sleep(1000)                                  //esperar 10s como o enunciado pede

    /**
     * Enquanto nao tiver coins, apresenta apenas a lista de scores
     * uma vez com coins, fica intermitente a mostrar a lista de scores e o número de coins
     * uma vez neste estado espera pela tecla '*' para começar o jogo
    **/
    while (true){
        app.printScoresOnLCD()

        if (app.isMaintenance){
            app.maintenanceMode()
            if(app.isShutDown){
                break
            }
        }

        if(app.gameMode){
            app.startNewGame(teste = false)
            app.registerScore()
        }

    }
    println("Aplication end succesfully")
}