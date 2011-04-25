package netd.async;

import netd.async.annot.Nonblocking;

/**
** An asynchronous execution.
**
** This is essentially an asynchronous counterpart to {@link Runnable}, and is
** similarly-general in scope and purpose. Many classes in this package use
** this pattern of execution, in place of {@link Runnable}.
*/
public interface Asynchronous {

	@Nonblocking
	public void launch();

	@Nonblocking
	public void finish();

}
