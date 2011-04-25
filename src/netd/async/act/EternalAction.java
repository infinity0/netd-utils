package netd.async.act;

/**
** An action that cannot finish naturally.
**
** Implementations may override {@link #delete()} to allow premature deletion.
*/
abstract public class EternalAction extends Action {

	protected EternalAction() {
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
	public boolean canFinish() {
		return false;
	}

	protected void launch2() {
		// pass
	}

}
