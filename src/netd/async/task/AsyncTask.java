package netd.async.task;

import netd.async.ActionControl;
import netd.async.Asynchronous;

/**
** The target of an action, an asynchronous task.
**
** Separates execution state from the rest of the user logic. DOC
*/
abstract public class AsyncTask implements Asynchronous {

	private ActionControl act;

	final public synchronized void assign(ActionControl act) {
		if (this.act != null) { throw new IllegalArgumentException("DOC"); }
		this.act = act;
	}
	
	protected ActionControl act() {
		if (this.act != null) { throw new IllegalArgumentException("DOC"); }
		return act;
	}

	@Override
	abstract public void launch();
	
	@Override
	abstract public void cancel();
	
	@Override
	abstract public void finish();
	
}
