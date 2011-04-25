package netd.async.act;

import netd.async.task.AsyncTask;
import netd.async.task.TaskSequence;

public class ActionSequence extends Action {

	final TaskSequence taskSeq;

	protected int stage = 0;

	ActionSequence(TaskSequence taskSeq) {
		super(null);
		this.taskSeq = taskSeq;
	}

	@Override protected void launch2() {
		attachNextAction();
	}

	@Override protected void finish2() {
		// pass
	}

	protected void attachNextAction() {
		if (stage == taskSeq.length) { return; }
		final int run_stage = stage;
		attach(new Action(new AsyncTask() {
			@Override public void launch() {
				taskSeq.run(run_stage, act);
			}
			@Override public void finish() {
				attachNextAction();
			}
		}));
		stage++;
	}

}
