package netd.async.task;

import netd.async.ActionControl;

/**
** A task which relies on external code that does not use this library.
*/
abstract public class ExternalTask extends AsyncTask {

	private ActionControl block;

	@Override
	public void launch() {
		block = act().acquire();
		register();
	}

	/**
	** Register a listener on the external API. The listener should wrap all
	** executable contents around {@link #callback(Runnable)}, like so:
	**
	** 	// in register();
	** 	act.attach(new ExternalTask() {
	** 	{@literal @Override} register() {
	** 		externalAPI.addEventListener(new EventListener() {
	** 		{@literal @Override} public void onEvent(final S arg1, final T arg2, ...) {
	** 			// wrapping the runnable inside callback() is vital
	** 			callback(new Runnable() {
	** 			{@literal @Override} public void run() {
	** 				// do something involving arg0, arg1, ...
	** 			}});
	** 		}});
	** 	}});
	*/
	abstract public void register();

	/**
	** Wrap a runnable to handle internal bookkeeping stuff.
	**
	** {@link #launch()} blocks this task whilst the external API is doing its
	** thing; this releases the block after the callback is executed.
	*/
	protected void callback(Runnable run) {
		try {
			run.run();
		} finally {
			act().release(block);
		}
	}

}
