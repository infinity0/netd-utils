package netd.async.act;

import netd.async.Asynchronous;
import netd.async.annot.Nonblocking;

import netd.async.task.AsyncTask;

import java.util.Set;
import java.util.HashSet;

/**
** An on-going action.
**
** This class holds logic and state that relates to execution progress.
**
** Execution {@link State state} is controlled via {@link #launch()},
** {@link #finish()}, {@link #delete()}.
**
** Relationships with other actions is controlled via {@link #attach(Action)},
** {@link #detach(Action)}.
**
** @see netd.async
*/
public class Action extends Act implements Asynchronous {

	public enum State {
		/** State upon construction. */
		INIT,
		/** State after {@link #launch()} */
		PEND,
		/** State after {@link #finish()} */
		DONE;
	}

	/** Sub-actions, that this action depends on.
	 * Updated by the appropriate invocations on _this_ object. */
	final protected Set<Action> sub = new HashSet<Action>();
	/** Super-actions, that depend on this action.
	 * Updated by the appropriate invocations on the _parent_ action.*/
	final protected Set<Action> sup = new HashSet<Action>();

	/** Task to be executed. If this is {@code null}, we instead use
	 * {@link #launch2()}, {@link #finish2()} as appropriate. **/
	final protected AsyncTask task;
	/** Current state of the execution. */
	protected State state = State.INIT;

	Action() {
		this.task = null;
	}

	Action(AsyncTask task) {
		this.task = task;
		task.assign(this);
	}

	///////////////////////////////////////////////////////////////////////////
	// public interface Asynchronous
	///////////////////////////////////////////////////////////////////////////

	public synchronized State getState() {
		return state;
	}

	/**
	** Whether to allow {@link #launch()}. By default, only during {@link
	** State#INIT}.
	*/
	public synchronized boolean canLaunch() {
		return state == State.INIT;
	}

	/**
	** Whether to allow {@link #finish()}. By default, only during {@link
	** State#PEND}, **and** if we have no child dependants.
	*/
	public synchronized boolean canFinish() {
		return state == State.PEND && sub.isEmpty();
	}

	/**
	** Whether to allow {@link #delete()}. By default, only during {@link
	** State#DONE}.
	*/
	public synchronized boolean canDelete() {
		return state == State.DONE;
	}

	/**
	** @see #canLaunch()
	** @see State#PEND
	*/
	@Override
	public synchronized void launch() throws IllegalStateException {
		if (!canLaunch()) { throw new IllegalStateException("DOC"); }
		if (task == null) {
			launch2();
		} else {
			try {
				task.launch();
			} catch (RuntimeException e) {
				// TODO(infinity0): account for error conditions
			}
		}
		state = State.PEND;
	}

	/**
	** @see #canFinish()
	** @see State#DONE
	*/
	@Override
	public synchronized void finish() throws IllegalStateException {
		if (!canFinish()) { throw new IllegalStateException("DOC"); }
		if (task == null) {
			finish2();
		} else {
			try {
				task.finish();
			} catch (RuntimeException e) {
				// TODO(infinity0): account for error conditions
			}
		}
		state = State.DONE;
	}

	/**
	** Delete this action, and detach it from its parents so they no longer
	** depend on it.
	**
	** @see #canDelete()
	*/
	@Nonblocking
	public synchronized void delete() throws IllegalStateException {
		if (!canDelete()) { throw new IllegalStateException("DOC"); }
		// TODO(infinity0): account for error conditions
		deleteHookPre();
		for (Action parent: sup.toArray(new Action[sup.size()])) {
			parent.detach(this);
			//parent.delete(); executor handles this
		}
		deleteHookPost();
	}

	/**
	** Alternate launch mechanism, used mostly by subclasses that implement
	** special logic that does not easily fit a single delegate task.
	*/
	@Nonblocking
	protected void launch2() {
		throw new UnsupportedOperationException("cannot call launch2() on base Action class");
	}

	/**
	** Alternate finish mechanism, used mostly by subclasses that implement
	** special logic that does not easily fit a single delegate task.
	*/
	@Nonblocking
	protected void finish2() {
		throw new UnsupportedOperationException("cannot call finish2() on base Action class");
	}

	///////////////////////////////////////////////////////////////////////////
	// abstract public class Act
	///////////////////////////////////////////////////////////////////////////

	/**
	** Whether to allow {@link #attach(Action)}. By default, only during {@link
	** State#PEND}, **and** if the action is not a parent of this.
	*/
	public synchronized boolean canAttach(Action child) {
		return state == State.PEND && !isDescendedFrom(child);
	}

	/**
	** Whether to allow {@link #detach(Action)}. By default, only during {@link
	** State#PEND}, **and** if the action is a direct child of this.
	*/
	public synchronized boolean canDetach(Action child) {
		return state == State.PEND && sub.contains(child);
	}

	/**
	** Whether this is descended from the given action.
	*/
	protected boolean isDescendedFrom(Action action) {
		// OPTIMISE
		// TODO possibly not thread safe
		if (action == this) { return true; }
		for (Action child: action.sub) {
			if (isDescendedFrom(child)) { return true; }
		}
		return false;
	}

	/**
	** Attach a child dependency, and attach this onto it as a dependant.
	**
	** @see #canAttach(Action)
	*/
	@Override
	public synchronized void attach(Action child) throws IllegalStateException {
		synchronized(child) {
			if (!canAttach(child)) { throw new IllegalArgumentException("DOC"); }
			child.attachHookPre(this);
			boolean insert_child = child.sup.add(this);
			boolean insert_parent = this.sup.add(child);
			assert insert_child && insert_parent;
			child.attachHookPost(this);
		}
	}

	/**
	** Detach a child dependency, and detach this from it as its dependant.
	**
	** @see #canDetach(Action)
	*/
	@Override
	public synchronized void detach(Action child) throws IllegalStateException {
		synchronized(child) {
			if (!canDetach(child)) { throw new IllegalArgumentException("DOC"); }
			child.detachHookPre(this);
			boolean remove_child = child.sup.remove(this);
			boolean remove_parent = this.sub.remove(child);
			assert remove_child && remove_parent;
			child.detachHookPost(this);
		}
	}

	protected synchronized void deleteHookPre() {}
	protected synchronized void deleteHookPost() {}

	protected synchronized void detachHookPre(Action parent) {}
	protected synchronized void detachHookPost(Action parent) {}
	protected synchronized void attachHookPre(Action parent) {}
	protected synchronized void attachHookPost(Action parent) {}

}
