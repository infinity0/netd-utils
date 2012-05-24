package netd.async;

import netd.async.act.AbstractAction;
import netd.async.task.AsyncTask;

/**
 * For user code to call.
 * 
 * @author infinity0
 */
public interface ActionControl {

	enum State {
		/** State upon construction. */
		INIT,
		/** State after {@link #launch()} */
		PEND,
		/** State after {@link #finish()} */
		DONE;
	}

	ActionControl attach(AsyncTask task);

	/**
	 ** Acquire a new blocking object (aka "lock") on this action, which
	 ** prevents it from finishing until it is released.
	 **
	 ** @return The blocking object, as an {@link AbstractAction}. Calls to attach/detach
	 ** methods on this object are prohibited and will fail.
	 */
	ActionControl acquire();

	/**
	 ** Release the previously-acquired blocking object.
	 */
	ActionControl release(ActionControl block);

}