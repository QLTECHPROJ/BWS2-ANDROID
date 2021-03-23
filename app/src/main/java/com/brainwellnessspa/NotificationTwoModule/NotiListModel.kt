package com.brainwellnessspa.NotificationTwoModule

class NotiListModel(title: String?, desc: String?, time: String?) {
    var title: String? = null
    var desc: String? = null
    var time: String? = null

    init {
        this.title = title!!
        this.desc = desc!!
        this.time = time!!
    }
}