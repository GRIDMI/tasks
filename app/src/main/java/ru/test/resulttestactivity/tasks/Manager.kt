package ru.test.resulttestactivity.tasks

open class Manager {

    private val exchangers: MutableList<Exchanger<out IO.Request, out IO.Response>> = mutableListOf()

    protected fun <A: IO.Request, B: IO.Response> bind(b: Exchanger.Behavior<A, B>): Exchanger<A, B> {
        val e = Exchanger(b)
        exchangers.add(e)
        return e
    }

    fun notifyPending() {
        exchangers.forEach {
            it.onNotify()
        }
    }

    open class IO(val id: String) {
        open class Request(id: String): IO(id) {
            var timeout: Long = 86_400_000L
            @Volatile
            var canceled: Throwable? = null
        }
        open class Response(id: String): IO(id)
    }

}