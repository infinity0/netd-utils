package netd.async.act;

/**
** A action at the root of an executor.
*/
public class RootAction extends EternalAction {

	public RootAction() {
		super();
	}

	///////////////////////////////////////////////////////////////////////////
	// public interface Asynchronous
	///////////////////////////////////////////////////////////////////////////

	/**
	** {@inheritDoc}
	**
	** This implementation returns {@code false}.
	*/
	public boolean canDelete() {
		return false;
	}

}
