package netd.async.task;

import netd.async.annot.Nonblocking;

/**
** A task whose entire execution can be done in one single non-blocking phase.
*/
abstract public class DiscreteTask extends AsyncTask implements Runnable {

	@Override
	@Nonblocking
	final public void launch() {
		run();
	}

	@Override
	@Nonblocking
	abstract public void run();

	@Override
	@Nonblocking
	final public void finish() {
		// pass
	}

}
