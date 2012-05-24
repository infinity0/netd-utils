package netd.async.act;

import java.util.HashSet;
import java.util.Set;

import netd.async.Asynchronous;
import netd.async.annot.Nonblocking;
import netd.async.task.AsyncTask;

/**
** An on-going action.
**
** This class holds logic and state that relates to execution progress.
**
** Execution {@link State state} is controlled via {@link #launch()},
** {@link #finish()}, {@link #delete()}.
**
** Relationships with other actions is controlled via {@link #attach(BaseAction)},
** {@link #detach(BaseAction)}.
**
** @see netd.async
*/
public class BaseAction extends AbstractAction implements Asynchronous {

	/** Sub-actions, that this action depends on.
	 * Updated by the appropriate invocations on _this_ object. */
	final protected Set<BaseAction> sub = new HashSet<BaseAction>();
	/** Super-actions, that depend on this action.
	 * Updated by the appropriate invocations on the _parent_ action.*/
	final protected Set<BaseAction> sup = new HashSet<BaseAction>();

	/** Task to be executed. If this is {@code null}, we instead use
	 * {@link #launch2()}, {@link #finish2()} as appropriate. **/
	final protected AsyncTask task;
	/** Current state of the execution. */
	protected State state = State.INIT;

	BaseAction() {
		this.task = null;
	}

	BaseAction(AsyncTask task) {
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
	** State#PEND}, **and** if we have no child dependents.
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

	@Override
	public synchronized void cancel() throws IllegalStateException {
		throw new UnsupportedOperationException("not implemented");
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
		for (BaseAction parent: sup.toArray(new BaseAction[sup.size()])) {
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
	** Whether to allow {@link #attach(BaseAction)}. By default, only during {@link
	** State#PEND}, **and** if the action is not a parent of this.
	*/
	public synchronized boolean canAttach(BaseAction child) {
		return state == State.PEND && !isDescendedFrom(child);
	}

	/**
	** Whether to allow {@link #detach(BaseAction)}. By default, only during {@link
	** State#PEND}, **and** if the action is a direct child of this.
	*/
	public synchronized boolean canDetach(BaseAction child) {
		return state == State.PEND && sub.contains(child);
	}

	/**
	** Whether this is descended from the given action.
	*/
	protected boolean isDescendedFrom(BaseAction action) {
		// TODO OPTIMISE
		// TODO possibly not thread safe
		// FIXME definitely not thread safe over the entire graph
		if (action == this) { return true; }
		for (BaseAction child: action.sub) {
			if (isDescendedFrom(child)) { return true; }
		}
		return false;
	}

	/**
	** Attach a child dependency, and attach this onto it as a dependant.
	**
	** @see #canAttach(BaseAction)
	*/
	@Override
	public synchronized void attach(BaseAction child) throws IllegalStateException {
		// need to ensure DAG order (non-cyclic) before attempting lock on child
		if (!canAttach(child)) { throw new IllegalArgumentException("DOC"); }
		synchronized(child) {
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
	** @see #canDetach(BaseAction)
	*/
	@Override
	public synchronized void detach(BaseAction child) throws IllegalStateException {
		// need to ensure DAG order (non-cyclic) before attempting lock on child
		if (!canDetach(child)) { throw new IllegalArgumentException("DOC"); }
		synchronized(child) {
			child.detachHookPre(this);
			boolean remove_child = child.sup.remove(this);
			boolean remove_parent = this.sub.remove(child);
			assert remove_child && remove_parent;
			child.detachHookPost(this);
		}
	}

	protected synchronized void deleteHookPre() {}
	protected synchronized void deleteHookPost() {}

	protected synchronized void detachHookPre(BaseAction parent) {}
	protected synchronized void detachHookPost(BaseAction parent) {}
	protected synchronized void attachHookPre(BaseAction parent) {}
	protected synchronized void attachHookPost(BaseAction parent) {}

}
