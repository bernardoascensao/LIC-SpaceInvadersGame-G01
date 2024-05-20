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