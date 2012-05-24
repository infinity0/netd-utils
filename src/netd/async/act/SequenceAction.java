package netd.async.act;

import java.util.Iterator;

import netd.async.task.AsyncTask;

class SequenceAction extends BaseAction {

	final Iterator<AsyncTask> taskSeq;
	AsyncTask current = null;
	
	SequenceAction(Iterator<AsyncTask> taskSeq) {
		super(null);
		this.taskSeq = taskSeq;
	}

	@Override protected void launch2() {
		attachNextAction();
	}

	@Override protected void finish2() {
		// pass
	}

	protected synchronized void attachNextAction() {
		if (!taskSeq.hasNext()) { return; }
		final AsyncTask subj = taskSeq.next();
		attach(new AsyncTask() {
			@Override public void launch() {
				subj.launch();
			}
			@Override public void finish() {
				subj.finish();
				attachNextAction();
			}
			@Override public void cancel() {
				subj.cancel();
			}
		});
		current = subj;
	}

}
