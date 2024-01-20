package ru.test.resulttestactivity.tasks

import java.util.concurrent.Executors

class Exchanger<A: Manager.IO.Request, B: Manager.IO.Response>(
    private val behavior: Behavior<A, B>
) {

    private val executor = Executors.newSingleThreadExecutor()
    private val tasks: MutableList<Task<A, B>> = mutableListOf()

    private fun tasks(id: String): List<Task<A, B>> = lock {
        tasks.filter {
            it.request.id == id
        }
    }

    private fun tasks(id: String, f: ((Task<A, B>) -> Unit)) {
        tasks(id).forEach(f)
    }

    fun cancel(id: String, err: Throwable?) = tasks(id) {
        it.request.canceled = err
    }

    fun response(response: B) = tasks(response.id) {
        it.isDone = behavior.isDone(response)
        it.response = response
    }

    fun request(request: A) = request(getTask(request))

    private fun request(task: Task<A, B>): B {
        try {

            this.behavior.onRequest(task.request)

            val start = System.currentTimeMillis()
            val end = start + task.request.timeout

            while (end > System.currentTimeMillis()) {
                if (task.request.canceled == null) {
                    return task.ifDone() ?: continue
                }
                break
            }

            throw task.request.canceled ?: TimeoutTaskException()

        } finally {
            lock {
                tasks.remove(task)
            }
        }
    }

    private fun getTask(request: A) = lock {
        cancel(request.id, NewTaskException())
        val task = Task<A, B>(request)
        tasks.add(task)
        task
    }

    @Synchronized
    private fun <T> lock(f: (() -> T)): T = f()

    fun work(request: A): Work<A, B> {
        return object : Work<A, B> {

            override fun cancel(error: Throwable): Work<A, B> {
                cancel(request.id, error)
                return this
            }

            override fun execute(result: Work.Result<B>): Work<A, B> {
                val task = getTask(request)
                executor.execute {
                    runCatching {
                        request(task)
                    }.onSuccess {
                        runCatching {
                            result.onSuccess(it)
                        }.onFailure {
                            result.onFailed(it)
                        }
                    }.onFailure {
                        result.onFailed(it)
                    }
                }
                return this
            }

        }
    }

    fun onNotify() = lock {
        for (t in tasks) t.response?.let {
            t.isDone = behavior.isDone(it)
        }
    }

    private class Task<A, B>(val request: A) {

        var isDone = false
        var response: B? = null

        fun ifDone(): B? {
            return when(isDone) {
                true -> response
                else -> null
            }
        }

    }

    interface Behavior<A, B> {
        fun onRequest(request: A)
        fun isDone(response: B) = true
    }

    interface Work<A, B> {

        fun execute(result: Result<B>): Work<A, B>
        fun cancel(error: Throwable): Work<A, B>
        fun cancel() = cancel(CancelTaskException())

        interface Result<B> {
            fun onSuccess(ret: B)
            fun onFailed(err: Throwable)
        }

    }

}