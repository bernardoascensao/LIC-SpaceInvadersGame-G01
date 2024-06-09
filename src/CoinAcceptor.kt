import isel.leic.utils.Time

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

    fun hasCoin(timeout : Long): Boolean {
        val first_call_time = Time.getTimeInMillis()
        var current_time = first_call_time

        while (current_time - first_call_time < timeout){
            if (isThereAnyCoin){
                acceptCoin()
                return true
            }
            current_time = Time.getTimeInMillis()
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