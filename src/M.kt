object M {
    fun init() {
        HAL.init()
    }
    fun isInMaintenance(): Boolean {
        return HAL.isBit(MaintenanceMASK)
//        val key = KBD.waitKey(5000)           //Este código é para poder testar em casa
//        return key == '1'
    }
}

fun main() {
    M.init()

    while (true) {
        val read = readln()
        if (read == "5") {
            HAL.setBits(MaintenanceMASK)
        } else if (read == "0") {
            HAL.clrBits(MaintenanceMASK)
        }

        if (M.isInMaintenance()) {
            println("Maintenance mode")
        } else {
            println("Non Maintenance")
        }
    }
}