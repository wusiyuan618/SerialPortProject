package com.wusy.serialportproject.bean

class OutSideTempBean {

    /**
     * reason : 查询成功!
     * result : {"city":"重庆","realtime":{"temperature":"17","humidity":"63","info":"晴","wid":"00","direct":"北风","power":"2级","aqi":"58"},"future":[{"date":"2020-03-18","temperature":"11/22℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"南风转东南风"},{"date":"2020-03-19","temperature":"14/24℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"东南风转东风"},{"date":"2020-03-20","temperature":"15/25℃","weather":"多云转小雨","wid":{"day":"01","night":"07"},"direct":"东风转东北风"},{"date":"2020-03-21","temperature":"17/22℃","weather":"阴","wid":{"day":"02","night":"02"},"direct":"东风转北风"},{"date":"2020-03-22","temperature":"17/21℃","weather":"小雨","wid":{"day":"07","night":"07"},"direct":"北风"}]}
     * error_code : 0
     */

    var reason: String? = null
    var result: ResultBean? = null
    var error_code: Int = 0

    class ResultBean {
        /**
         * city : 重庆
         * realtime : {"temperature":"17","humidity":"63","info":"晴","wid":"00","direct":"北风","power":"2级","aqi":"58"}
         * future : [{"date":"2020-03-18","temperature":"11/22℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"南风转东南风"},{"date":"2020-03-19","temperature":"14/24℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"东南风转东风"},{"date":"2020-03-20","temperature":"15/25℃","weather":"多云转小雨","wid":{"day":"01","night":"07"},"direct":"东风转东北风"},{"date":"2020-03-21","temperature":"17/22℃","weather":"阴","wid":{"day":"02","night":"02"},"direct":"东风转北风"},{"date":"2020-03-22","temperature":"17/21℃","weather":"小雨","wid":{"day":"07","night":"07"},"direct":"北风"}]
         */

        var city: String? = null
        var realtime: RealtimeBean? = null
        var future: List<FutureBean>? = null

        class RealtimeBean {
            /**
             * temperature : 17
             * humidity : 63
             * info : 晴
             * wid : 00
             * direct : 北风
             * power : 2级
             * aqi : 58
             */

            var temperature: String? = null
            var humidity: String? = null
            var info: String? = null
            var wid: String? = null
            var direct: String? = null
            var power: String? = null
            var aqi: String? = null
        }

        class FutureBean {
            /**
             * date : 2020-03-18
             * temperature : 11/22℃
             * weather : 多云
             * wid : {"day":"01","night":"01"}
             * direct : 南风转东南风
             */

            var date: String? = null
            var temperature: String? = null
            var weather: String? = null
            var wid: WidBean? = null
            var direct: String? = null

            class WidBean {
                /**
                 * day : 01
                 * night : 01
                 */

                var day: String? = null
                var night: String? = null
            }
        }
    }
}
