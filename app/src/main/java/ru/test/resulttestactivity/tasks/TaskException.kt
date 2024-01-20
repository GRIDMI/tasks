package ru.test.resulttestactivity.tasks

open class TaskException: RuntimeException()

class CancelTaskException: TaskException()

class TimeoutTaskException: TaskException()

class NewTaskException: TaskException()
