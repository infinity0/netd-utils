package netd.async.act;

import netd.async.ActionControl;
import netd.async.task.AsyncTask;

/**
** Abstract implementation of {@link ActionControl}.
**
** @see ActionControl
*/
abstract public class AbstractAction implements ActionControl {

	abstract protected void attach(BaseAction child);

	abstract protected void detach(BaseAction child);

	final private ActionControl attachReturn(BaseAction child) {
		attach(child);
		return child;
	}

	@Override
	public ActionControl attach(AsyncTask task) {
		return attachReturn(new BaseAction(task));
	}

	@Override
	public ActionControl acquire() {
		return attachReturn(new BlockAction());
	}

	@Override
	public ActionControl release(ActionControl block) {
		detach((BlockAction) block);
		return this;
	}

}
