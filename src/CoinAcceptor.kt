object CoinAcceptor {

    fun init() {
        HAL.clrBits(acceptCoinMASK)
    }

    val isThereAnyCoin get() = (HAL.readBits(CoinMASK) != 0)

    fun acceptCoin(){
        //pulsar sinal de accept
        HAL.setBits(acceptCoinMASK)
        while (true){
            if (!HAL.isBit(CoinMASK)){
                break
            }
        }
        HAL.clrBits(acceptCoinMASK)
    }

    fun hasCoin(): Boolean {
        if (isThereAnyCoin){
            acceptCoin()
            return true
        }
        return false
    }

}

fun main(){
    CoinAcceptor.init()

    while (true){
        if(CoinAcceptor.isThereAnyCoin){
            CoinAcceptor.acceptCoin()
        }
    }
}