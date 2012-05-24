package netd.async.act;

/**
** An action whose only purpose is to block its parent action from finishing.
**
** This class should only be used by code that handles execution and actions,
** not by user-implemented Task-related code.
**
** TODO(infinity0): if the block takes too long, the Executor may forcibly
** delete it with a TimeoutException.
**
** @see netd.async.task.ExternalTask
*/
class BlockAction extends EternalAction {

	BlockAction() {
		super();
	}

	// TODO enforce single parent
	// TODO enforce auto-timeout (re-enable delete)

	///////////////////////////////////////////////////////////////////////////
	// abstract public class Act
	///////////////////////////////////////////////////////////////////////////

	@Override
	public void attach(BaseAction child) {
		throw new IllegalStateException("cannot attach() on BlockAction");
	}

	@Override
	public void detach(BaseAction child) {
		throw new IllegalStateException("cannot detach() on BlockAction");
	}

}
