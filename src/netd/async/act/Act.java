package netd.async.act;

import netd.async.task.AsyncTask;
import netd.async.task.TaskSequence;

/**
** The Task-facing API of {@link Action}. DOC
**
** @see Action
*/
abstract public class Act {

	abstract protected void attach(Action child);

	abstract protected void detach(Action child);

	final private Act attachReturn(Action child) {
		attach(child);
		return child;
	}

	public Act attach(AsyncTask task) {
		return attachReturn(new Action(task));
	}

	public Act attach(TaskSequence taskSeq) {
		return attachReturn(new ActionSequence(taskSeq));
	}

	/**
	** Acquire a new blocking object (aka "lock") on this action, which
	** prevents it from finishing until it is released.
	**
	** @return The blocking object, as an {@link Act}. Calls to attach/detach
	** methods on this object are prohibited and will fail.
	*/
	public Act acquire() {
		return attachReturn(new BlockAction());
	}

	/**
	** Release the previously-acquired blocking object.
	*/
	public void release(Act block) {
		detach((BlockAction) block);
	}

}
