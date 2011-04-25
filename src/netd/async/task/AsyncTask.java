package netd.async.task;

import netd.async.Asynchronous;
import netd.async.act.Act;

/**
** The target of an action, an asynchronous task.
**
** Separates execution state from the rest of the user logic. DOC
*/
abstract public class AsyncTask implements Asynchronous {

	protected Act act;

	final public synchronized void assign(Act act) {
		if (this.act != null) { throw new IllegalArgumentException("DOC"); }
		this.act = act;
	}

	@Override
	abstract public void launch();

	@Override
	abstract public void finish();

}
