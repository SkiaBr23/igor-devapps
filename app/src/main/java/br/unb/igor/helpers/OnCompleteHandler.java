package br.unb.igor.helpers;

public class OnCompleteHandler {

    public interface OnCompleteCallback {
        void onComplete(boolean cancelled, Object extra);
    }

    private boolean finished = false;
    private Object extra = null;
    private int goal = 1;
    private OnCompleteCallback callback;

    public OnCompleteHandler(int steps, OnCompleteCallback callback) {
        this.goal = steps;
        this.callback = callback;
    }

    public synchronized void setExtra(Object extra) {
        this.extra = extra;
    }

    public synchronized void cancel() {
        OnCompleteCallback callback = this.callback;
        if (callback != null) {
            this.callback = null;
            callback.onComplete(true, extra);
        }
    }

    public synchronized void advance() {
        goal--;
        if (goal <= 0 && !finished && this.callback != null) {
            OnCompleteCallback callback = this.callback;
            this.callback = null;
            this.finished = true;
            callback.onComplete(false, extra);
        }
    }

}
