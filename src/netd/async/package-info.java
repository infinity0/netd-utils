/**
** Graph-based dependency-aware asynchronous execution framework.
**
** * provides a natural API for writing complex asynchronous code that "looks
**   like" the synchronous equivalent
** * able to wrap around simpler third-party async APIs, e.g. event-based ones
** * separates execution logic from application logic, which saves users from
**   thinking about (and trying to solve) the same repetitive problems.
** * transparently handles arbitrary acyclic dependencies between tasks
** * offers a variety of execution strategies, including multithreaded
** * supports automatic restricted execution given user resource constraints
**
** The asynchronous model is divided into several components:
**
** * {@link netd.async.act}: the progress of a single action, and its
**   relationship with other actions. This establishes our fundamental abstract
**   model of asynchronous execution as a graph of inter-dependent actions.
** * {@link netd.async.exec}: strategies for executing the action-graph as a
**   whole, including multithreaded strategies.
** * {@link netd.async.task}: end-user API for writing asynchronous tasks,
**   that aims to hide most of the execution complexity from users.
**
** @author infinity0 <infinity0@gmx.com>
** @see netd.async.act
** @see netd.async.act.Act
** @see netd.async.act.Action
** @see netd.async.exec
** @see netd.async.exec.Executor
** @see netd.async.task
** @see netd.async.task.AsyncTask
** @see netd.async.task.TaskSequence
*/
package netd.async;
