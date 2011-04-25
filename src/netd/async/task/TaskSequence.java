package netd.async.task;

import netd.async.act.Act;
import netd.async.annot.Nonblocking;

abstract public class TaskSequence {

	final public int length;

	public TaskSequence(int length) {
		this.length = length;
	}

	@Nonblocking
	abstract public void run(int stage, Act act);

	protected IllegalArgumentException invalidStage(int stage) {
		return new IllegalArgumentException("invalid stage: " + stage);
	}

}
